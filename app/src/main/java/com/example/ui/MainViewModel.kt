package com.example.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DashboardWidget
import com.example.data.local.NicheTrend
import com.example.data.local.SavedKeyword
import com.example.data.local.SavedVideoSEO
import com.example.data.remote.YouTubeService
import com.example.data.remote.YouTubeVideo
import com.example.data.repository.AuthManager
import com.example.data.repository.GeminiRepository
import com.example.util.PdfExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val authManager = AuthManager(application)
    private val geminiRepo = GeminiRepository()
    private val youtubeService = YouTubeService()

    // Auth & Keys state flowing reactive
    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn
    private val _isCustomMode = MutableStateFlow(false)
    val isCustomMode: StateFlow<Boolean> = _isCustomMode.asStateFlow()

    // Creator Stats UI bindings
    val creatorNiche = MutableStateFlow(authManager.getCreatorNiche())
    val channelName = MutableStateFlow(authManager.getChannelName())
    val channelId = MutableStateFlow(authManager.getChannelId())

    private val _youtubeSubscribers = MutableStateFlow("142.8K")
    val youtubeSubscribers = _youtubeSubscribers.asStateFlow()

    private val _youtubeViews = MutableStateFlow("4.2M")
    val youtubeViews = _youtubeViews.asStateFlow()

    private val _youtubeVideosCount = MutableStateFlow("248")
    val youtubeVideosCount = _youtubeVideosCount.asStateFlow()

    // Multi-platform sync values (YouTube, TikTok, Instagram)
    private val _tiktokFollowers = MutableStateFlow("85.2K")
    val tiktokFollowers = _tiktokFollowers.asStateFlow()

    private val _instagramFollowers = MutableStateFlow("112.4K")
    val instagramFollowers = _instagramFollowers.asStateFlow()

    // Dynamic Live Videos linked from YouTube Search
    private val _liveVideosList = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val liveVideosList = _liveVideosList.asStateFlow()

    // In-app Alert Center for viral trends
    private val _alertNotification = MutableStateFlow<String?>(null)
    val alertNotification = _alertNotification.asStateFlow()

    // Background Operation Status
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    // Room DB StateFlows
    val savedKeywords: StateFlow<List<SavedKeyword>> = db.keywordDao.getAllKeywords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val optimizedVideos: StateFlow<List<SavedVideoSEO>> = db.videoSeoDao.getAllOptimizedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val widgets: StateFlow<List<DashboardWidget>> = db.widgetDao.getAllWidgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trends: StateFlow<List<NicheTrend>> = db.trendDao.getAllTrends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            checkAndPrepopulateDatabase()
            refreshStats()
        }
    }

    private suspend fun checkAndPrepopulateDatabase() {
        // Build initial widgets if none exist
        val existingWidgets = db.widgetDao.getAllWidgetsSync()
        if (existingWidgets.isEmpty()) {
            val initialList = listOf(
                DashboardWidget("channel_reach", "Reach & Multi-Platform Stats", true, 0),
                DashboardWidget("retention_chart", "Viewer Retention Analytics", true, 1),
                DashboardWidget("predictive_engagement", "Predictive Engagement Estimator", true, 2),
                DashboardWidget("niche_trends_alert", "Niche Viral Alerts Monitor", true, 3),
                DashboardWidget("keyword_research_launcher", "Keyword Quick Tool", true, 4)
            )
            db.widgetDao.insertWidgets(initialList)
        }

        // Build default trends if none exist
        val items = db.trendDao.getAllTrends().first()
        if (items.isEmpty()) {
            val mockTrends = listOf(
                NicheTrend(niche = "AI & Technology", term = "Jetpack Compose Navigation Type-Safety", growthPercent = 145.2, searchVolume = "Explosive", competition = "Low", isAlerted = true),
                NicheTrend(niche = "AI & Technology", term = "Gemini 1.5 Flash Integration API Tutorial", growthPercent = 98.7, searchVolume = "Very High", competition = "Low", isAlerted = true),
                NicheTrend(niche = "AI & Technology", term = "Kotlin 2.2 Coroutines Flow Performance", growthPercent = 65.4, searchVolume = "Medium", competition = "Medium", isAlerted = true)
            )
            db.trendDao.insertTrends(mockTrends)
        }
    }

    fun login(youtubeKey: String, geminiKey: String, niche: String, userChannelName: String, userChannelId: String) {
        authManager.login(youtubeKey, geminiKey, niche, userChannelName, userChannelId)
        creatorNiche.value = niche
        channelName.value = userChannelName
        channelId.value = userChannelId
        
        // Mode toggle dependent on provided keys custom
        _isCustomMode.value = youtubeKey.isNotEmpty() || geminiKey.isNotEmpty()
        
        viewModelScope.launch {
            refreshStats()
            triggerTrendCheck()
        }
    }

    fun logout() {
        authManager.logout()
        creatorNiche.value = "AI & Software Development"
        channelName.value = "AI Coding Ninja"
        channelId.value = "UCxxxxxxxxxxxxxxxx"
        _isCustomMode.value = false
        _youtubeSubscribers.value = "142.8K"
        _youtubeViews.value = "4.2M"
        _youtubeVideosCount.value = "248"
        _liveVideosList.value = emptyList()

        viewModelScope.launch {
            db.keywordDao.clearAll()
            db.videoSeoDao.clearAll()
            checkAndPrepopulateDatabase()
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            val ytKey = authManager.getYouTubeApiKey()
            val yId = authManager.getChannelId()
            
            if (ytKey.isNotEmpty() && yId.isNotEmpty()) {
                val stats = youtubeService.fetchChannelStats(yId, ytKey)
                if (stats != null) {
                    _youtubeSubscribers.value = formatNumber(stats.subscriberCount.toDoubleOrNull() ?: 0.0)
                    _youtubeViews.value = formatNumber(stats.viewCount.toDoubleOrNull() ?: 0.0)
                    _youtubeVideosCount.value = stats.videoCount
                }
                val list = youtubeService.fetchChannelVideos(yId, ytKey)
                _liveVideosList.value = list
            } else {
                // Populate high fidelity mock video uploads for beautiful experience
                _liveVideosList.value = listOf(
                    YouTubeVideo(
                        "v1",
                        "Build an Android App in 24 Hours with AI",
                        "In this tutorial we build a clean architecture mobile app in Android using Kotlin, Jetpack Compose and Gemini REST API integrations.",
                        "",
                        "2 days ago"
                    ),
                    YouTubeVideo(
                        "v2",
                        "Jetpack Compose Animation Fundamentals",
                        "Learn how to create physics springs, dynamic visual fades, and customized floating graphics in modern Android Material 3 development.",
                        "",
                        "1 week ago"
                    ),
                    YouTubeVideo(
                        "v3",
                        "Gemini API - Prototyping vs Production",
                        "A crash guide covering secret environment setups, API key secure managers, and configuring Firebase App check or direct REST endpoints.",
                        "",
                        "3 weeks ago"
                    )
                )
            }
        }
    }

    fun changeCreatorNiche(niche: String) {
        authManager.updateNiche(niche)
        creatorNiche.value = niche
        viewModelScope.launch {
            triggerTrendCheck()
        }
    }

    fun updateWidgetVisibility(widgetId: String, visible: Boolean) {
        viewModelScope.launch {
            db.widgetDao.updateWidgetVisibility(widgetId, visible)
        }
    }

    fun performKeywordAnalysis(keyword: String) {
        if (keyword.isBlank()) return
        
        viewModelScope.launch {
            _isAnalyzing.value = true
            val apiToken = authManager.getGeminiApiKey()
            
            if (apiToken.isNotEmpty()) {
                val result = geminiRepo.analyzeKeyword(keyword, creatorNiche.value, apiToken)
                if (result != null) {
                    db.keywordDao.insertKeyword(result)
                } else {
                    Toast.makeText(getApplication(), "Failed to fetch keyword analysis, check your API key.", Toast.LENGTH_LONG).show()
                }
            } else {
                // Return immediate beautifully-simulated high-fidelity result
                val overall = (60..88).random()
                val vol = (55..95).random()
                val comp = (20..65).random()
                val mockResult = SavedKeyword(
                    keyword = keyword,
                    overallScore = overall,
                    volumeScore = vol,
                    competitionScore = comp,
                    relatedKeywords = "$keyword tips, $keyword setup, learn $keyword in 2026, best $keyword tutorial",
                    recommendedTags = "$keyword, $keyword tutorial, coding with $keyword, mobile development",
                    summary = "This keyword '$keyword' shows a very promising compatibility score. With a Search Volume of $vol% and a balanced Competition score of $comp%, optimizing content titles with related hooks should trigger significant reach in the '$creatorNiche' niche."
                )
                db.keywordDao.insertKeyword(mockResult)
            }
            _isAnalyzing.value = false
        }
    }

    fun performVideoSEOOptimization(draftTitle: String, draftDescription: String) {
        if (draftTitle.isBlank()) return
        
        viewModelScope.launch {
            _isAnalyzing.value = true
            val apiToken = authManager.getGeminiApiKey()

            if (apiToken.isNotEmpty()) {
                val result = geminiRepo.optimizeVideoMetadata(draftTitle, draftDescription, creatorNiche.value, apiToken)
                if (result != null) {
                    db.videoSeoDao.insertOptimizedVideo(result)
                } else {
                    Toast.makeText(getApplication(), "Failed to optimize video metadata, check your API key.", Toast.LENGTH_LONG).show()
                }
            } else {
                // Return high-fidelity mock optimized record
                val score = (85..98).random()
                val mockResult = SavedVideoSEO(
                    originalTitle = draftTitle,
                    optimizedTitles = "$draftTitle (Step-by-Step Tutorial)|Why You NEED to learn $draftTitle TODAY!|I Built a complete $draftTitle App on live camera!",
                    descriptionOptimized = "🔥 Get Started with $draftTitle right now! \n\nIn this video, I break down everything you need to know about $draftTitle under our niche: ${creatorNiche.value}.\n\n📌 CHAPTERS:\n0:00 - Introduction\n2:15 - Core concepts setup\n5:40 - Advanced implementation guide\n11:20 - Summary and Next steps.",
                    tagsOptimized = "$draftTitle, tutorial, ${creatorNiche.value}, modern coding, productivity, creator guide",
                    predictedViews = "${(12..45).random()}K - ${(50..80).random()}K",
                    predictedLikes = "${(800..2500).random()}",
                    predictedComments = "${(90..450).random()}",
                    seoScore = score
                )
                db.videoSeoDao.insertOptimizedVideo(mockResult)
            }
            _isAnalyzing.value = false
        }
    }

    fun triggerTrendCheck() {
        viewModelScope.launch {
            val apiToken = authManager.getGeminiApiKey()
            val currentTrendNiche = creatorNiche.value
            
            if (apiToken.isNotEmpty()) {
                val newTrends = geminiRepo.fetchNicheTrends(currentTrendNiche, apiToken)
                if (newTrends.isNotEmpty()) {
                    db.trendDao.insertTrends(newTrends)
                    val unalerted = db.trendDao.getUnalertedTrends()
                    if (unalerted.isNotEmpty()) {
                        val mostViral = unalerted.first()
                        _alertNotification.value = "Viral Trend Alert: '$mostViral' is packing +${mostViral.growthPercent}% growth in '$currentTrendNiche' right now! Click to inspect."
                        db.trendDao.markTrendAsAlerted(mostViral.id)
                    }
                }
            } else {
                // Fallback: Trigger trending banner alert randomly
                val mockTopic = listOf(
                    "Ultra-responsive layouts in Kotlin 2.2",
                    "Jetpack Compose canvas performance hacks",
                    "Offline-first sync techniques"
                ).random()
                val mockGrowth = (120..280).random()
                
                val newTrend = NicheTrend(
                    niche = currentTrendNiche,
                    term = mockTopic,
                    growthPercent = mockGrowth.toDouble(),
                    searchVolume = "Explosive",
                    competition = "Low",
                    isAlerted = true
                )
                db.trendDao.insertTrends(listOf(newTrend))
                _alertNotification.value = "Niche Trend Warning: '$mockTopic' is trending upwards with +$mockGrowth% search momentum in the $currentTrendNiche niche! Prepare content immediately!"
            }
        }
    }

    fun dismissNotification() {
        _alertNotification.value = null
    }

    fun deleteKeyword(keyword: String) {
        viewModelScope.launch {
            db.keywordDao.deleteKeyword(keyword)
        }
    }

    fun deleteOptimizedVideo(id: Int) {
        viewModelScope.launch {
            db.videoSeoDao.deleteOptimizedVideo(id)
        }
    }

    fun handleExportPdfReport() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            
            val keywordNames = savedKeywords.value.map { it.keyword }
            val avgSeoScore = if (optimizedVideos.value.isEmpty()) 88 else optimizedVideos.value.map { it.seoScore }.average().toInt()

            val file = PdfExporter.generateMonthlyReport(
                context = app,
                channelName = channelName.value,
                niche = creatorNiche.value,
                subscribers = youtubeSubscribers.value,
                views = youtubeViews.value,
                keywords = keywordNames,
                uploadedVideosCount = optimizedVideos.value.size,
                averageSeoScore = avgSeoScore,
                tiktokFollowers = _tiktokFollowers.value,
                instagramFollowers = _instagramFollowers.value
            )

            if (file != null) {
                PdfExporter.sharePdfReport(app, file)
            } else {
                Toast.makeText(app, "Unable to generate PDF report, verify device disk cache space", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatNumber(number: Double): String {
        return when {
            number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
            number >= 1000 -> String.format("%.1fK", number / 1000.0)
            else -> number.toInt().toString()
        }
    }
}
