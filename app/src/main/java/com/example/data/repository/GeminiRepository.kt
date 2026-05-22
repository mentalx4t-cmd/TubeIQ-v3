package com.example.data.repository

import com.example.data.local.NicheTrend
import com.example.data.local.SavedKeyword
import com.example.data.local.SavedVideoSEO
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import org.json.JSONArray
import org.json.JSONObject

class GeminiRepository {

    private fun cleanJson(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```")) {
            // Remove markdown code fence if present
            text = text.replace(Regex("^```[a-zA-Z]*\\s*"), "")
            text = text.replace(Regex("\\s*```$"), "")
        }
        return text.trim()
    }

    suspend fun analyzeKeyword(keyword: String, niche: String, apiKey: String): SavedKeyword? {
        if (apiKey.isEmpty()) return null

        val prompt = """
            Perform professional YouTube keyword research for the phrase '$keyword' tailored to the creator niche '$niche'.
            Provide specific metrics for Search Volume, Competition, and an Overall compatibility score out of 100. Generate highly-optimized related keywords and search tags.
            Return ONLY a valid JSON object matching this schema (do NOT include markdown codeblocks or any extra conversational text, just the raw JSON):
            {
              "overallScore": 75,
              "volumeScore": 82,
              "competitionScore": 34,
              "relatedKeywords": "related term 1, related term 2, related term 3",
              "tags": "tag1, tag2, tag3, tag4",
              "summary": "Short paragraph analyzing why this keyword is high or low compatibility for this niche."
            }
        """.trimIndent()

        return try {
            val content = GeminiContent(listOf(GeminiPart(prompt)))
            val request = GeminiRequest(listOf(content))
            val response = GeminiClient.api.generateContent(apiKey, request)
            
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val jsonText = cleanJson(rawText)
            
            val jsonObject = JSONObject(jsonText)
            SavedKeyword(
                keyword = keyword,
                overallScore = jsonObject.optInt("overallScore", 50),
                volumeScore = jsonObject.optInt("volumeScore", 50),
                competitionScore = jsonObject.optInt("competitionScore", 50),
                relatedKeywords = jsonObject.optString("relatedKeywords", ""),
                recommendedTags = jsonObject.optString("tags", ""),
                summary = jsonObject.optString("summary", "Analysis completed successfully.")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun optimizeVideoMetadata(
        draftTitle: String,
        draftDescription: String,
        niche: String,
        apiKey: String
    ): SavedVideoSEO? {
        if (apiKey.isEmpty()) return null

        val prompt = """
            Optimize the following YouTube draft metadata:
            Draft Title: "$draftTitle"
            Draft Description: "$draftDescription"
            Creator Niche: "$niche"
            
            Generate 3 alternate CTR-optimized titles (viral titles). Improve the description with clean SEO styling and chapters. Recommend top-ranking optimization tag fields. Compare these options with predicted analytics (predicted engagement brackets).
            Return ONLY a valid JSON object matching this schema (do NOT include markdown codeblocks or other text, just the raw JSON):
            {
              "title1": "Alternate Catchy Title Option 1",
              "title2": "Alternate Catchy Title Option 2",
              "title3": "Alternate Catchy Title Option 3",
              "description": "Clean SEO-optimized description",
              "tags": "tag1, tag2, tag3, tag4, tag5",
              "predictedViews": "5.5K - 9.8K",
              "predictedLikes": "250 - 550",
              "predictedComments": "45 - 90",
              "seoScore": 94
            }
        """.trimIndent()

        return try {
            val content = GeminiContent(listOf(GeminiPart(prompt)))
            val request = GeminiRequest(listOf(content))
            val response = GeminiClient.api.generateContent(apiKey, request)

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val jsonText = cleanJson(rawText)

            val jsonObject = JSONObject(jsonText)
            val titles = listOf(
                jsonObject.optString("title1"),
                jsonObject.optString("title2"),
                jsonObject.optString("title3")
            ).filter { it.isNotEmpty() }.joinToString("|")

            SavedVideoSEO(
                originalTitle = draftTitle,
                optimizedTitles = titles,
                descriptionOptimized = jsonObject.optString("description", draftDescription),
                tagsOptimized = jsonObject.optString("tags", ""),
                predictedViews = jsonObject.optString("predictedViews", "TBD"),
                predictedLikes = jsonObject.optString("predictedLikes", "TBD"),
                predictedComments = jsonObject.optString("predictedComments", "TBD"),
                seoScore = jsonObject.optInt("seoScore", 70)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchNicheTrends(niche: String, apiKey: String): List<NicheTrend> {
        if (apiKey.isEmpty()) return emptyList()

        val prompt = """
            Identify the top 3 absolute trending search terms/keywords in the YouTube creator niche '$niche' for this month. 
            Provide high-potential growth rates (as percentages), search volume category (e.g., High, Excellent, Explosive), and competition category (e.g. Low, Medium, High).
            Return ONLY a valid JSON object matching this schema (do NOT include markdown codeblocks, just raw JSON text):
            {
              "trends": [
                {
                  "term": "trending topic 1",
                  "growth": 145.2,
                  "volume": "Explosive",
                  "competition": "Low"
                },
                {
                  "term": "trending topic 2",
                  "growth": 98.7,
                  "volume": "High",
                  "competition": "Medium"
                },
                {
                  "term": "trending topic 3",
                  "growth": 65.4,
                  "volume": "Very High",
                  "competition": "Low"
                }
              ]
            }
        """.trimIndent()

        val list = mutableListOf<NicheTrend>()
        try {
            val content = GeminiContent(listOf(GeminiPart(prompt)))
            val request = GeminiRequest(listOf(content))
            val response = GeminiClient.api.generateContent(apiKey, request)

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val jsonText = cleanJson(rawText)

            val jsonObject = JSONObject(jsonText)
            val trendsArray = jsonObject.getJSONArray("trends")
            for (i in 0 until trendsArray.length()) {
                val item = trendsArray.getJSONObject(i)
                list.add(
                    NicheTrend(
                        niche = niche,
                        term = item.optString("term", "Default Trend"),
                        growthPercent = item.optDouble("growth", 40.0),
                        searchVolume = item.optString("volume", "High"),
                        competition = item.optString("competition", "Low")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
