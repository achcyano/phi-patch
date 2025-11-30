package me.achqing.phipatch.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import me.achqing.phipatch.BuildConfig
import java.time.OffsetDateTime

object UpdateChecker {
    suspend fun fetchLatestTag(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val rb = Request.Builder()
                .url(Const.UPDATE_API_URL)
                .get()
            OkHttpClient().newCall(rb.build()).execute().use { resp ->
                val body = resp.body.string()
                Result.success(JSONObject(body).getString("published_at"))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    fun compareSemver(latest: String): Boolean {
        val buildData = OffsetDateTime.parse(BuildConfig.BUILD_TIME).plusDays(1)
        val latestData = OffsetDateTime.parse(latest)
        return !latestData.isAfter(buildData)
    }

    suspend fun isUpdateAvailable(): Result<Boolean> =
        fetchLatestTag().mapCatching { latest -> compareSemver(latest) }.onFailure{
            it.printStackTrace()
        }
}