package com.wwf.projectmanagement.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.LruCache
import androidx.exifinterface.media.ExifInterface
import com.wwf.projectmanagement.data.Projects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/** Fetch state of one gallery file, observed by its grid tile and by the full-screen viewer. */
sealed interface MediaLoad {
    data object Idle : MediaLoad
    /** Queued or streaming; [percent] is `null` while queued or when the server sends no size. */
    data class Downloading(val percent: Int?) : MediaLoad
    /** The whole file, held in memory only; never written to storage. */
    class Ready(val bytes: ByteArray) : MediaLoad
    data class Failed(val message: String) : MediaLoad
}

/**
 * Streams gallery files from the backend into memory and exposes each file's [MediaLoad] so the
 * UI can show progress rings while a photo or video arrives. Files are never saved to disk:
 * bytes are kept in a bounded memory cache (half the heap; the app opts into `largeHeap`) and
 * re-fetched from the server if evicted. Photos are re-encoded to at most [PHOTO_MAX_SIDE] px on
 * arrival (originals are 4000px+, ~5 MB) so the whole gallery fits in memory. [prefetchAll]
 * runs at app launch so every photo and video is ready to view by the time the gallery opens.
 *
 * Fetches go through a priority queue served by [MAX_PARALLEL] workers: files the user is
 * looking at right now ([load] from a tile or the viewer) jump ahead of the background prefetch,
 * so opening the Rifle Range Road gallery never waits behind the Straw-headed Bulbul videos.
 */
class GalleryRepository(private val api: GalleryApi = GalleryApi()) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val loads = mutableMapOf<String, MutableStateFlow<MediaLoad>>()
    private val pending = ArrayDeque<GalleryMedia>()
    private val tickets = Channel<Unit>(Channel.UNLIMITED)
    private val listings = mutableMapOf<String, List<GalleryMedia>>()
    private val memory = object : LruCache<String, ByteArray>((Runtime.getRuntime().maxMemory() / 2).toInt()) {
        override fun sizeOf(key: String, value: ByteArray) = value.size

        override fun entryRemoved(evicted: Boolean, key: String, old: ByteArray, new: ByteArray?) {
            if (!evicted) return
            // Drop the reference so the bytes can be collected; the UI will re-fetch on demand.
            synchronized(loads) { loads[key] }?.let { flow ->
                if (flow.value is MediaLoad.Ready) flow.value = MediaLoad.Idle
            }
        }
    }

    init {
        repeat(MAX_PARALLEL) {
            scope.launch {
                for (ticket in tickets) {
                    val media = synchronized(pending) { pending.removeFirstOrNull() } ?: continue
                    fetch(media, flowFor(media))
                }
            }
        }
    }

    /** The gallery listing for [projectId], fetched once and reused. */
    suspend fun list(projectId: String): List<GalleryMedia> =
        synchronized(listings) { listings[projectId] } ?: fetchListing(projectId).also {
            synchronized(listings) { listings[projectId] = it }
        }

    private suspend fun fetchListing(projectId: String): List<GalleryMedia> = when (projectId) {
        Projects.STRAW_HEADED_BULBUL_ID -> api.listStrawHeadedBulbul()
        Projects.RIFLE_RANGE_ROAD_ID -> api.listRifleRangeRoad()
        else -> emptyList()
    }

    /** Fetches every project's listing and files so the galleries are fully ready when opened. */
    fun prefetchAll() {
        Projects.all.forEach { project ->
            scope.launch {
                val items = try { list(project.id) } catch (_: Exception) { return@launch }
                items.forEach { enqueue(it, flowFor(it), urgent = false) }
            }
        }
    }

    /**
     * Current state for [media]; the file is fetched (or moved to the front of the queue) because
     * it is on screen right now.
     */
    fun load(media: GalleryMedia): StateFlow<MediaLoad> {
        val flow = flowFor(media)
        enqueue(media, flow, urgent = true)
        return flow.asStateFlow()
    }

    private fun flowFor(media: GalleryMedia): MutableStateFlow<MediaLoad> =
        synchronized(loads) { loads.getOrPut(media.id) { MutableStateFlow(MediaLoad.Idle) } }

    fun retry(media: GalleryMedia) {
        val flow = synchronized(loads) { loads[media.id] } ?: return
        if (flow.value is MediaLoad.Failed) enqueue(media, flow, urgent = true)
    }

    private fun enqueue(media: GalleryMedia, flow: MutableStateFlow<MediaLoad>, urgent: Boolean) {
        synchronized(pending) {
            when (flow.value) {
                is MediaLoad.Idle, is MediaLoad.Failed -> {
                    flow.value = MediaLoad.Downloading(null)
                    if (urgent) pending.addFirst(media) else pending.addLast(media)
                }
                // Already queued (or running): urgent requests move to the front of the queue.
                is MediaLoad.Downloading -> if (urgent && pending.remove(media)) pending.addFirst(media)
                is MediaLoad.Ready -> return
            }
        }
        tickets.trySend(Unit)
    }

    private suspend fun fetch(media: GalleryMedia, flow: MutableStateFlow<MediaLoad>) {
        try {
            val raw = api.stream(media) { percent -> flow.value = MediaLoad.Downloading(percent) }
            val bytes = if (media.isVideo) raw else shrinkPhoto(raw)
            memory.put(media.id, bytes)
            flow.value = MediaLoad.Ready(bytes)
        } catch (e: Exception) {
            flow.value = MediaLoad.Failed(e.message ?: e.javaClass.simpleName)
        }
    }

    /** Re-encodes a photo (EXIF-rotated) to at most [PHOTO_MAX_SIDE] px; falls back to the original. */
    private fun shrinkPhoto(raw: ByteArray): ByteArray {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(raw, 0, raw.size, bounds)
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= PHOTO_MAX_SIDE || bounds.outHeight / (sample * 2) >= PHOTO_MAX_SIDE) sample *= 2
        val decoded = BitmapFactory.decodeByteArray(raw, 0, raw.size, BitmapFactory.Options().apply { inSampleSize = sample })
            ?: return raw
        val rotated = rotate(decoded, raw)
        val out = ByteArrayOutputStream(raw.size / 4)
        rotated.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, out)
        if (rotated !== decoded) rotated.recycle()
        decoded.recycle()
        return out.toByteArray()
    }

    private fun rotate(bitmap: Bitmap, raw: ByteArray): Bitmap {
        val orientation = try {
            ExifInterface(ByteArrayInputStream(raw)).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
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

    companion object {
        private const val MAX_PARALLEL = 6
        private const val PHOTO_MAX_SIDE = 2048
        private const val PHOTO_QUALITY = 88

        @Volatile
        private var instance: GalleryRepository? = null

        fun get(): GalleryRepository =
            instance ?: synchronized(this) { instance ?: GalleryRepository().also { instance = it } }
    }
}
