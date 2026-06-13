package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val plan: String = "basic", // basic or premium
    val points: Int = 100
)

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val id: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: String,
    val sender: String, // user or bot
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedFilePath: String? = null
)

@Entity(tableName = "saved_projects")
data class SavedProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // logo, code, document, photo, content
    val title: String,
    val content: String,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AppDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getChatSessionsFlow(): Flow<List<ChatSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSession(session: ChatSession)

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    suspend fun deleteChatSession(id: String)

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSessionFlow(sessionId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)

    @Query("SELECT * FROM saved_projects ORDER BY timestamp DESC")
    fun getSavedProjectsFlow(): Flow<List<SavedProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: SavedProject)

    @Query("DELETE FROM saved_projects WHERE id = :id")
    suspend fun deleteProject(id: Int)
}

@Database(entities = [UserProfile::class, ChatSession::class, ChatMessage::class, SavedProject::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val appDao: AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kameshk_gpt_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
