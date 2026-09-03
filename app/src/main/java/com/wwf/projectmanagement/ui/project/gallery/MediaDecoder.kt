package com.wwf.projectmanagement.ui.project.gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaDataSource
import android.media.MediaMetadataRetriever
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

/**
 * Decodes in-memory gallery files into bitmaps sized for where they're shown (a grid tile or the
 * full screen), honouring EXIF rotation, and keeps recent results in memory (1/6 of the heap).
 * Backend photos are 4000px+ originals, so decoding at the target size matters.
 */
object MediaDecoder {
    private val memory = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 6).toInt()) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    suspend fun photo(id: String, bytes: ByteArray, maxSize: Int): Bitmap? = cached("$id@$maxSize") {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, maxSize)
            inPreferredConfig = Bitmap.Config.RGB_565.takeIf { maxSize <= THUMB_SIZE } ?: Bitmap.Config.ARGB_8888
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.let { applyExifRotation(bytes, it) }
    }

    suspend fun videoFrame(id: String, bytes: ByteArray, maxSize: Int): Bitmap? = cached("$id@frame$maxSize") {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(ByteArrayMediaDataSource(bytes))
            val frame = retriever.getFrameAtTime(FRAME_TIME_US, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime
            frame?.let { scaleDown(it, maxSize) }
        } catch (_: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private suspend fun cached(key: String, decode: () -> Bitmap?): Bitmap? {
        memory.get(key)?.let { return it }
        return withContext(Dispatchers.IO) {
            decode()?.also { memory.put(key, it) }
        }
    }

    private fun sampleSize(width: Int, height: Int, maxSize: Int): Int {
        var sample = 1
        while (width / (sample * 2) >= maxSize || height / (sample * 2) >= maxSize) sample *= 2
        return sample
    }

    private fun scaleDown(bitmap: Bitmap, maxSize: Int): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= maxSize) return bitmap
        val scale = maxSize.toFloat() / longest
        return bitmap.scale((bitmap.width * scale).toInt(), (bitmap.height * scale).toInt())
    }

    private fun applyExifRotation(bytes: ByteArray, bitmap: Bitmap): Bitmap {
        val orientation = try {
            ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /** Longest edge for grid thumbnails; keeps 40 tiles well under the memory cache. */
    const val THUMB_SIZE = 640
    /** Longest edge in the full-screen viewer (leaves headroom for 4x pinch zoom). */
    const val FULL_SIZE = 2048
    private const val FRAME_TIME_US = 1_000_000L
}

/** Lets [MediaMetadataRetriever] read a video that only exists in memory. */
class ByteArrayMediaDataSource(private val bytes: ByteArray) : MediaDataSource() {
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position >= bytes.size) return -1
        val count = minOf(size, bytes.size - position.toInt())
        System.arraycopy(bytes, position.toInt(), buffer, offset, count)
        return count
    }

    override fun getSize(): Long = bytes.size.toLong()

    override fun close() = Unit
}

/** Decodes [bytes] off the main thread; `null` until ready (or if the file can't be decoded). */
@Composable
fun rememberDecodedMedia(id: String, bytes: ByteArray?, isVideo: Boolean, maxSize: Int): State<ImageBitmap?> =
    produceState<ImageBitmap?>(initialValue = null, id, bytes, isVideo, maxSize) {
        value = bytes?.let {
            (if (isVideo) MediaDecoder.videoFrame(id, it, maxSize) else MediaDecoder.photo(id, it, maxSize))?.asImageBitmap()
        }
    }
