package com.wwf.projectmanagement.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

/*
 * Project statistics from the WWF backend, mirroring the web client:
 *
 *  Straw-headed Bulbul (`fetchSurveyDataForHomePage()` → `getPublicStats()`):
 *  - `POST /surveys {purpose: "getPublicStatistics", databaseName}` →
 *    `{statistics: {totalObservations, uniqueLocations, numberOfYears}, userCount}`.
 *
 *  Rifle Range Road (`getRifleRangeRoadSurveyData()`):
 *  - `POST /rifleRangeRoad/surveys {purpose: "retrieve"}` → `{surveys: [...]}`; the statistics are
 *    derived from the records like the web's `calculateStatistics`: one observation per record,
 *    distinct names in "Name of Surveyors" as volunteers, and years since the earliest
 *    "Survey Date".
 */
class StatsApi(private val baseUrl: String = GalleryApi.BASE_URL) {

    suspend fun strawHeadedBulbul(): ProjectStats = withContext(Dispatchers.IO) {
        val json = postJson(
            "$baseUrl/surveys",
            JSONObject().put("purpose", "getPublicStatistics").put("databaseName", SHB_DATABASE),
        )
        if (!json.optBoolean("success")) throw IOException("Statistics request was not successful")
        val statistics = json.optJSONObject("statistics") ?: JSONObject()
        ProjectStats(
            observations = statistics.optString("totalObservations", "0").ifBlank { "0" },
            locations = statistics.optString("uniqueLocations", "0").ifBlank { "0" },
            volunteers = json.optString("userCount", "0").ifBlank { "0" },
            yearsActive = statistics.optString("numberOfYears", "0").ifBlank { "0" },
        )
    }

    suspend fun rifleRangeRoad(): ProjectStats = withContext(Dispatchers.IO) {
        val json = postJson("$baseUrl/rifleRangeRoad/surveys", JSONObject().put("purpose", "retrieve"))
        val surveys = json.optJSONArray("surveys") ?: throw IOException("No surveys in response")
        val volunteers = LinkedHashSet<String>()
        var earliest: LocalDate? = null
        for (i in 0 until surveys.length()) {
            val record = surveys.optJSONObject(i) ?: continue
            record.optString("Name of Surveyors").split(',')
                .map { it.trim() }
                .filter { it.length > 1 }
                .forEach { volunteers += it }
            parseSurveyDate(record.optString("Survey Date"))?.let { date ->
                if (earliest == null || date.isBefore(earliest)) earliest = date
            }
        }
        val years = earliest?.let { ChronoUnit.YEARS.between(it, LocalDate.now()).toInt() } ?: 0
        ProjectStats(
            observations = surveys.length().toString(),
            locations = "0",
            volunteers = volunteers.size.toString(),
            yearsActive = "${years.coerceAtLeast(1)}+",
        )
    }

    private fun parseSurveyDate(raw: String): LocalDate? {
        val text = raw.trim()
        if (text.isEmpty()) return null
        for (format in DATE_FORMATS) {
            try {
                return LocalDate.parse(text, format)
            } catch (_: DateTimeParseException) {
                // try the next format
            }
        }
        return null
    }

    private fun postJson(url: String, body: JSONObject): JSONObject {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            if (connection.responseCode !in 200..299) throw IOException("Server responded with HTTP ${connection.responseCode}")
            return JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        /** Database the web's Straw-headed Bulbul pages query (`WWFSG` returns all zeros). */
        private const val SHB_DATABASE = "StrawHeadedBulbul"
        private const val TIMEOUT_MS = 30_000
        private val DATE_FORMATS = listOf(
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yy"),
        )
    }
}
