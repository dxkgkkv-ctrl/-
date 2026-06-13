package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "topK") val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateContent(prompt: String, conversationHistory: List<GeminiContent> = emptyList()): String {
        return try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                return "خطا: کلید API معتبر یافت نشد. لطفا Secrets را در AI Studio بررسی کنید."
            }

            val contents = mutableListOf<GeminiContent>()
            contents.addAll(conversationHistory)
            contents.add(GeminiContent(parts = listOf(GeminiPart(text = prompt))))

            val systemPrompt = "شما 'کمشک جیپیتی' (Kameshk GPT) هستید؛ " +
                    "یک دستیار پلتفرم هوش مصنوعی همه کاره، فوق العاده باهوش، حرفه ای و مدرن ایرانی. " +
                    "کاربران شما فارسی زبان هستند. پاسخ ها باید کاملاً به زبان فارسی، روان، مودبانه و با کیفیت باشد. " +
                    "در صورت داشتن قطعه کدهای برنامه نویسی، حتما آنها را به زیبایی قالب بندی مارک داون ارائه کنید."

            val request = GeminiRequest(
                contents = contents,
                generationConfig = GeminiGenerationConfig(temperature = 0.7f),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
            )

            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "من متوجه سوال شما نشدم. لطفا مجدد تلاش بفرمایید."
        } catch (e: Exception) {
            "خطا در ارتباط با سرور هوش مصنوعی: ${e.localizedMessage}"
        }
    }
}
