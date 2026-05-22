package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val publishTime: String
)

data class YouTubeChannelStats(
    val subscriberCount: String,
    val viewCount: String,
    val videoCount: String
)

class YouTubeService {
    private val client = OkHttpClient()

    suspend fun fetchChannelStats(channelId: String, apiKey: String): YouTubeChannelStats? = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || channelId.isEmpty()) return@withContext null

        val url = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=$channelId&key=$apiKey"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyText = response.body?.string() ?: return@withContext null
                val json = JSONObject(bodyText)
                val items = json.getJSONArray("items")
                if (items.length() > 0) {
                    val stats = items.getJSONObject(0).getJSONObject("statistics")
                    return@withContext YouTubeChannelStats(
                        subscriberCount = stats.optString("subscriberCount", "0"),
                        viewCount = stats.optString("viewCount", "0"),
                        videoCount = stats.optString("videoCount", "0")
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }

    suspend fun fetchChannelVideos(channelId: String, apiKey: String): List<YouTubeVideo> = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || channelId.isEmpty()) return@withContext emptyList()

        val url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=$channelId&type=video&maxResults=10&order=date&key=$apiKey"
        val request = Request.Builder().url(url).build()

        val list = mutableListOf<YouTubeVideo>()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val bodyText = response.body?.string() ?: return@withContext emptyList()
                val json = JSONObject(bodyText)
                val items = json.getJSONArray("items")
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val idObj = item.getJSONObject("id")
                    val videoId = idObj.optString("videoId", "")
                    val snippet = item.getJSONObject("snippet")
                    val title = snippet.optString("title", "")
                    val description = snippet.optString("description", "")
                    val thumbnails = snippet.getJSONObject("thumbnails")
                    val mediumThumb = thumbnails.optJSONObject("medium") ?: thumbnails.optJSONObject("default")
                    val thumbnailUrl = mediumThumb?.optString("url", "") ?: ""
                    val publishTime = snippet.optString("publishedAt", "")

                    if (videoId.isNotEmpty()) {
                        list.add(
                            YouTubeVideo(
                                videoId = videoId,
                                title = title,
                                description = description,
                                thumbnailUrl = thumbnailUrl,
                                publishTime = publishTime
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list
    }
}
