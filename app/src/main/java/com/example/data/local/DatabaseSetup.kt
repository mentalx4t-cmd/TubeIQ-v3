package com.example.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "saved_keywords")
data class SavedKeyword(
    @PrimaryKey val keyword: String,
    val overallScore: Int,
    val volumeScore: Int,
    val competitionScore: Int,
    val relatedKeywords: String, // Comma-separated
    val recommendedTags: String, // Comma-separated
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_video_seo")
data class SavedVideoSEO(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalTitle: String,
    val optimizedTitles: String, // Comma-separated (typically 3 items)
    val descriptionOptimized: String,
    val tagsOptimized: String, // Comma-separated
    val predictedViews: String,
    val predictedLikes: String,
    val predictedComments: String,
    val seoScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "dashboard_widgets")
data class DashboardWidget(
    @PrimaryKey val widgetId: String,
    val title: String,
    val isVisible: Boolean,
    val displayOrder: Int
)

@Entity(tableName = "niche_trends")
data class NicheTrend(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val niche: String,
    val term: String,
    val growthPercent: Double,
    val searchVolume: String,
    val competition: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAlerted: Boolean = false
)

// --- DAOs (Data Access Objects) ---

@Dao
interface SavedKeywordDao {
    @Query("SELECT * FROM saved_keywords ORDER BY timestamp DESC")
    fun getAllKeywords(): Flow<List<SavedKeyword>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: SavedKeyword)

    @Query("DELETE FROM saved_keywords WHERE keyword = :keyword")
    suspend fun deleteKeyword(keyword: String)

    @Query("DELETE FROM saved_keywords")
    suspend fun clearAll()
}

@Dao
interface SavedVideoSeoDao {
    @Query("SELECT * FROM saved_video_seo ORDER BY timestamp DESC")
    fun getAllOptimizedVideos(): Flow<List<SavedVideoSEO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptimizedVideo(video: SavedVideoSEO)

    @Query("DELETE FROM saved_video_seo WHERE id = :id")
    suspend fun deleteOptimizedVideo(id: Int)

    @Query("DELETE FROM saved_video_seo")
    suspend fun clearAll()
}

@Dao
interface DashboardWidgetDao {
    @Query("SELECT * FROM dashboard_widgets ORDER BY displayOrder ASC")
    fun getAllWidgets(): Flow<List<DashboardWidget>>

    @Query("SELECT * FROM dashboard_widgets ORDER BY displayOrder ASC")
    suspend fun getAllWidgetsSync(): List<DashboardWidget>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(widgets: List<DashboardWidget>)

    @Query("UPDATE dashboard_widgets SET isVisible = :visible WHERE widgetId = :widgetId")
    suspend fun updateWidgetVisibility(widgetId: String, visible: Boolean)
}

@Dao
interface NicheTrendDao {
    @Query("SELECT * FROM niche_trends ORDER BY timestamp DESC")
    fun getAllTrends(): Flow<List<NicheTrend>>

    @Query("SELECT * FROM niche_trends WHERE isAlerted = 0 ORDER BY timestamp DESC")
    suspend fun getUnalertedTrends(): List<NicheTrend>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrends(trends: List<NicheTrend>)

    @Query("UPDATE niche_trends SET isAlerted = 1 WHERE id = :id")
    suspend fun markTrendAsAlerted(id: Int)
}

// --- Room Database ---

@Database(
    entities = [SavedKeyword::class, SavedVideoSEO::class, DashboardWidget::class, NicheTrend::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val keywordDao: SavedKeywordDao
    abstract val videoSeoDao: SavedVideoSeoDao
    abstract val widgetDao: DashboardWidgetDao
    abstract val trendDao: NicheTrendDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vidiq_creator_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
