package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

enum class AppTab {
    CHAT, IMAGE, LOGO, EDITOR, CODING, DOCUMENT, CONTENT, PROFILE, ADMIN
}

class AppViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.appDao

    // Text To Speech
    private var tts: TextToSpeech? = null
    var isTtsReady by mutableStateOf(false)
    var isSpeakingId by mutableStateOf<Int?>(null)

    // Current Screen
    var currentTab by mutableStateOf(AppTab.CHAT)

    // User Profile
    val userProfile: StateFlow<UserProfile?> = dao.getUserProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Chat History
    val chatSessions: StateFlow<List<ChatSession>> = dao.getChatSessionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var activeSessionId by mutableStateOf("")
    var chatMessagesState = mutableStateOf<List<ChatMessage>>(emptyList())
    var chatInputText by mutableStateOf("")
    var isChatGenerating by mutableStateOf(false)
    var attachedFileName by mutableStateOf<String?>(null)

    // Image Generator State
    var imagePrompt by mutableStateOf("")
    var imageStyle by mutableStateOf("عکاسی واقعی") // Realistic, Anime, 3D, Cyberpunk, Painting
    var imageSize by mutableStateOf("1:1") // 1:1, 16:9, 9:16
    var imageQuality by mutableStateOf("HD") // HD, Ultra HD
    var negativePrompt by mutableStateOf("")
    var isGeneratingImage by mutableStateOf(false)
    var generatedImageSeed by mutableStateOf<Long?>(null) // Used to drive procedural rendering variations
    var generatedImageColorAccent by mutableStateOf("turquoise")

    // Logo Designer State
    var logoText by mutableStateOf("کمشک")
    var logoSlogan by mutableStateOf("آینده با هوش مصنوعی")
    var logoStylePreset by mutableStateOf("لاکچری") // luxury, minimal, corporate
    var logoEmblemType by mutableStateOf("پر طلایی") // gold wing, geometric circle, abstract shield, hexagon, neon orbit
    var isGeneratingLogo by mutableStateOf(false)
    var savedLogoSeed by mutableStateOf(0L)

    // Photo Editor State
    var selectedPhotoIndex by mutableStateOf(0) // Mountain, Retro, Neon Street, Portrait
    var selectedEditorTool by mutableStateOf("حذف پس‌زمینه") // bg removal, face enhance, color fix, outpaint
    var editorComparisonSlider by mutableStateOf(0.5f)
    var isEditingPhoto by mutableStateOf(false)

    // Coding Assistant State
    var codeInputPrompt by mutableStateOf("")
    var codingLanguage by mutableStateOf("Kotlin") // Kotlin, Python, HTML/CSS, Javascript, SQL
    var codingAction by mutableStateOf("تولید کد") // Generate, Explain, Debug, Optimise
    var isCodingLoading by mutableStateOf(false)
    var generatedCodeResponse by mutableStateOf("""
        // برای شروع، توضیحی وارد کرده و دکمه را بفشارید یا یکی از پروژه‌های آماده زیر را کلیک کنید:
    """.trimIndent())

    // Document Creator State
    var docType by mutableStateOf("معرفی پاورپوینت") // PowerPoint, PDF Report, Excel Table, Word
    var docTitle by mutableStateOf("گزارش پیشرفت کسب و کار")
    var docNotes by mutableStateOf("خلاصه‌ای از مهم‌ترین شاخص‌های مالی و رشد در سال جاری")
    var docThemeColor by mutableStateOf("سبز زمردی") // Emerald, Gold, Azure Tech, Crimson
    var isGeneratingDoc by mutableStateOf(false)

    // Content Copywriting State
    var contentCategory by mutableStateOf("پست وبلاگ") // BlogPost, SocialMedia, AdCopy, Story, SEO
    var contentTopicInput by mutableStateOf("")
    var contentToneValue by mutableStateOf("جذاب و خلاقانه") // Creative, Professional, Emotional
    var generatedContentResponse by mutableStateOf("")
    var isGeneratingContent by mutableStateOf(false)

    // Saved Projects List
    val savedProjects: StateFlow<List<SavedProject>> = dao.getSavedProjectsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Settings State (Stored in VM, highly reactive)
    var adminActiveModel by mutableStateOf("Gemini 3.5 Flash")
    var adminDailyRequestLimit by mutableStateOf(150)
    var adminIsSafeSearchOn by mutableStateOf(true)
    var adminIsToxicFilterOn by mutableStateOf(true)
    var adminLatencyFactor by mutableStateOf(240) // ms simulated average
    var adminSystemHealth by mutableStateOf(99) // % health status
    var adminQueriesCount by mutableStateOf(14)

    init {
        // Initialize TTS
        tts = TextToSpeech(application, this)

        // Prepopulate default session and user profile if missing
        viewModelScope.launch(Dispatchers.IO) {
            val prof = dao.getUserProfile()
            if (prof == null) {
                dao.saveUserProfile(UserProfile(name = "کاربر مهمان", email = "rmhmdamyn044@gmail.com", plan = "basic", points = 120))
            }
            dao.getChatSessionsFlow().collect { list ->
                if (list.isEmpty()) {
                    val defaultSession = ChatSession(id = "default_session", title = "گفتگوی عمومی جدید", timestamp = System.currentTimeMillis())
                    dao.insertChatSession(defaultSession)
                    activeSessionId = "default_session"
                } else {
                    if (activeSessionId.isEmpty()) {
                        activeSessionId = list.first().id
                    }
                }
                loadMessagesForActiveSession()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("fa") ?: Locale.US
            isTtsReady = true
        }
    }

    override fun onCleared() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onCleared()
    }

    fun speakText(messageId: Int, text: String) {
        if (!isTtsReady) {
            Toast.makeText(getApplication(), "موتور صوتی در دسترس نیست", Toast.LENGTH_SHORT).show()
            return
        }
        if (isSpeakingId == messageId) {
            tts?.stop()
            isSpeakingId = null
        } else {
            isSpeakingId = messageId
            // Speak text. Persian synthesis falls back cleanly to Urdu/Arabic rules if not supported native, 
            // we provide speech playback UI indicators and smooth animations.
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KameshkSpeaker_$messageId")
        }
    }

    fun switchTab(tab: AppTab) {
        currentTab = tab
    }

    fun loadMessagesForActiveSession() {
        if (activeSessionId.isEmpty()) return
        viewModelScope.launch {
            dao.getMessagesForSessionFlow(activeSessionId).collect { list ->
                chatMessagesState.value = list
            }
        }
    }

    fun selectSession(sessionId: String) {
        activeSessionId = sessionId
        loadMessagesForActiveSession()
    }

    fun createNewSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = UUID.randomUUID().toString()
            val session = ChatSession(id = newId, title = "گفتگوی شمارهٔ ${chatSessions.value.size + 1}")
            dao.insertChatSession(session)
            activeSessionId = newId
            loadMessagesForActiveSession()
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteChatSession(sessionId)
            dao.deleteMessagesForSession(sessionId)
            if (activeSessionId == sessionId) {
                activeSessionId = ""
            }
        }
    }

    fun clearActiveMessages() {
        if (activeSessionId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMessagesForSession(activeSessionId)
        }
    }

    fun sendChatMessage() {
        val messageText = chatInputText.trim()
        if (messageText.isEmpty() && attachedFileName == null) return

        val sessionId = activeSessionId
        if (sessionId.isEmpty()) return

        chatInputText = ""
        val filePayloadText = if (attachedFileName != null) "[فایل پیوست: $attachedFileName]\n" else ""
        attachedFileName = null

        viewModelScope.launch(Dispatchers.IO) {
            // Write User message to Room
            val userMsg = ChatMessage(
                sessionId = sessionId,
                sender = "user",
                message = filePayloadText + messageText,
                timestamp = System.currentTimeMillis()
            )
            dao.insertChatMessage(userMsg)
            adminQueriesCount++

            // AI Answer state
            isChatGenerating = true
            
            // Build conversation history for the context window
            val curMessages = chatMessagesState.value.takeLast(10).map {
                GeminiContent(parts = listOf(GeminiPart(text = it.message)))
            }

            // Real Gemini Call
            val response = GeminiClient.generateContent(messageText, curMessages)

            val botMsg = ChatMessage(
                sessionId = sessionId,
                sender = "bot",
                message = response,
                timestamp = System.currentTimeMillis()
            )
            dao.insertChatMessage(botMsg)
            isChatGenerating = false
        }
    }

    // AI Image Generator Logic
    fun generateAIImage() {
        if (imagePrompt.trim().isEmpty()) return
        viewModelScope.launch {
            isGeneratingImage = true
            adminQueriesCount++
            // Simulating image generation through procedural seed change and latency
            delay(2500)
            generatedImageSeed = System.currentTimeMillis()
            
            // Choose palette based on selection
            generatedImageColorAccent = when(imageStyle) {
                "انیمه" -> "neon_pink"
                "سایبرپانک" -> "cyber_neon"
                "نقاشی رنگ روغن" -> "amber_gold"
                "سه بعدی" -> "azure_glow"
                else -> "turquoise"
            }

            // Add project
            dao.insertProject(SavedProject(
                type = "photo",
                title = "تصویر: ${imagePrompt.take(20)}...",
                content = "سبک: $imageStyle | ابعاد: $imageSize | کیفیت: $imageQuality",
                details = imagePrompt
            ))
            
            isGeneratingImage = false
        }
    }

    // AI Logo Designer Logic
    fun generateBusinessLogo() {
        if (logoText.trim().isEmpty()) return
        viewModelScope.launch {
            isGeneratingLogo = true
            adminQueriesCount++
            delay(2000)
            savedLogoSeed = System.currentTimeMillis()
            
            dao.insertProject(SavedProject(
                type = "logo",
                title = "لوگو: $logoText",
                content = "نوع: $logoEmblemType | سبک: $logoStylePreset",
                details = logoSlogan
            ))
            isGeneratingLogo = false
        }
    }

    // AI Photo Editor Logic
    fun executePhotoEditTool() {
        viewModelScope.launch {
            isEditingPhoto = true
            adminQueriesCount++
            delay(2400)
            editorComparisonSlider = 1.0f // Fully transition slider to shows after effect
            isEditingPhoto = false
        }
    }

    // AI Coding Assistant logic
    fun runCodingTask() {
        val userPrompt = codeInputPrompt.trim()
        if (userPrompt.isEmpty()) return
        viewModelScope.launch {
            isCodingLoading = true
            adminQueriesCount++

            val formattedLanguages = "زبان برنامه‌نویسی: $codingLanguage. نوع کاربری: $codingAction."
            val apiPrompt = "کد لازم برای این درخواست را بساز: $userPrompt. فقط کد واقعی و قالب‌بندی شده مارک‌داون بدون توضیحات فلسفی خسته‌کننده به من برگردان. $formattedLanguages"

            val response = GeminiClient.generateContent(apiPrompt)
            generatedCodeResponse = response
            
            // Save project
            dao.insertProject(SavedProject(
                type = "code",
                title = "کد: $codingLanguage - ${userPrompt.take(15)}",
                content = formattedLanguages,
                details = response
            ))
            isCodingLoading = false
        }
    }

    fun loadPredefinedCode(title: String, prompt: String, lang: String) {
        codeInputPrompt = prompt
        codingLanguage = lang
        runCodingTask()
    }

    // AI Document Generation
    fun generateDocument() {
        if (docTitle.trim().isEmpty()) return
        viewModelScope.launch {
            isGeneratingDoc = true
            adminQueriesCount++
            delay(2500)
            
            dao.insertProject(SavedProject(
                type = "document",
                title = docTitle,
                content = "نوع سند: $docType | تم رنگی: $docThemeColor",
                details = docNotes
            ))
            isGeneratingDoc = false
        }
    }

    // AI Content Creation Tools
    fun generateContentTool() {
        if (contentTopicInput.trim().isEmpty()) return
        viewModelScope.launch {
            isGeneratingContent = true
            adminQueriesCount++

            val prompt = "یک $contentCategory درباره موضوع روبرو بنویس: $contentTopicInput. لحن نوشتن باشد: $contentToneValue. لطفا پاسخ را با تیترهای رسا به فارسی بنویس."
            val response = GeminiClient.generateContent(prompt)
            generatedContentResponse = response

            dao.insertProject(SavedProject(
                type = "content",
                title = "$contentCategory: ${contentTopicInput.take(15)}",
                content = "لحن: $contentToneValue",
                details = response
            ))
            isGeneratingContent = false
        }
    }

    // Profile systems
    fun upgradeToPremium() {
        viewModelScope.launch(Dispatchers.IO) {
            val prof = dao.getUserProfile()
            if (prof != null) {
                dao.saveUserProfile(prof.copy(plan = "premium", points = prof.points + 1000))
            }
        }
    }

    fun resetUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.saveUserProfile(UserProfile(name = "کاربر مهمان جدید", email = "rmhmdamyn044@gmail.com", plan = "basic", points = 100))
        }
    }

    // Admin commands
    fun resetWholeDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            clearActiveMessages()
            dao.getSavedProjectsFlow().collect { list ->
                list.forEach { dao.deleteProject(it.id) }
            }
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteProject(id)
        }
    }
}
