package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
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

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

object GeminiService {
    private const val TAG = "GeminiService"

    private val systemInstruction = """
        You are a warm, charming, and highly-trained AI Chef assistant for a premium mobile food delivery app called "Food Delivery".
        Your goal is to suggest perfect meals or food pairings based on the user's current hunger cravings or diet requests.
        Represent only the available items from our menu map whenever possible:
        1. Pizza Margherita - $20.00 (Category: Pizza) - authentic cheese, tomatoes, basil
        2. Smoked Truffle Burger - $26.00 (Category: Burger) - prime beef, swiss cheese, white truffle aioli
        3. Crunchy Fried Chicken - $18.00 (Category: Chicken) - 11 secret herbs and spices
        4. Club Sandwich Deluxe - $15.00 (Category: Sandwich) - sourdough, sliced turkey, bacon, tomatoes
        5. Creamy Pesto Pasta - $22.00 (Category: Pasta) - penne, basil pesto cream, grilled chicken
        6. Iced Latte Arabica - $8.00 (Category: Drinks) - single-origin Arabica, cold textured milk
        
        If the user asks for something outside, suggest the closest matching item from this menu and add a playful description.
        Write exactly 2 to 3 engaging, premium-sounding, food-centered sentences recommending the item(s).
        Always highlight how the choice perfectly answers their request. Be positive, warm, and professional. Use formatting like bullet points or bold text where appropriate.
    """.trimIndent()

    suspend fun getFoodRecommendation(userPrompt: String): String = withContext(Dispatchers.IO) {
        val rawApiKey = com.example.BuildConfig.GEMINI_API_KEY
        val hasRealApiKey = rawApiKey.isNotEmpty() && 
                            rawApiKey != "MY_GEMINI_API_KEY" && 
                            !rawApiKey.startsWith("YOUR_")

        if (!hasRealApiKey) {
            Log.d(TAG, "No valid Gemini API key found. Launching beautiful simulation mode.")
            return@withContext simulateAiChefResponse(userPrompt)
        }

        val requestBody = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstruction)))
        )

        try {
            val response = RetrofitClient.api.generateContent(rawApiKey, requestBody)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I apologize, but I am currently setting up my recommendations. Try our signature Pizza Margherita or Smoked Truffle Burger!"
        } catch (e: Exception) {
            Log.e(TAG, "API Call FAILED. Falling back to local offline chef engine.", e)
            simulateAiChefResponse(userPrompt)
        }
    }

    private fun simulateAiChefResponse(prompt: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("pizza") || query.contains("italian") || query.contains("cheese") -> {
                "🍕 **A perfect match for pizza lovers!**\n\nI highly recommend our signature **Pizza Margherita (\$20.00)**. It's adorned with buttery buffalo mozzarella, aromatic basil, and rich San Marzano tomato paste that delivers a true bite of Naples straight to your door. Pair it with an **Iced Latte Arabica** for an incredible savory-sweet contrast!"
            }
            query.contains("burger") || query.contains("meat") || query.contains("beef") || query.contains("heavy") -> {
                "🍔 **Craving something hearty and luxurious?**\n\nYou must try our **Smoked Truffle Burger (\$26.00)**. It contains dry-aged premium beef grilled to absolute perfection, layered with rich Swiss cheese and lathered in white truffle aioli. It's the ultimate premium gourmet indulgence!"
            }
            query.contains("chicken") || query.contains("crispy") || query.contains("fried") -> {
                "🍗 **Time for some serious crunch!**\n\nOur crispy, golden-brown **Crunchy Fried Chicken (\$18.00)** is marinated in our buttermilk house blend with 11 secret herbs and spices. It's unbelievably tender and juicy on the inside, with an unmatched premium crust on the outside!"
            }
            query.contains("pasta") || query.contains("creamy") || query.contains("pesto") -> {
                "🍝 **Indulge in pure creamy comfort!**\n\nThe award-winning **Creamy Pesto Pasta (\$22.00)** is cooked al dente and bathed in a decadent basil-pesto cream sauce. Garnished with grilled chicken and roasted pine nuts, it's a luscious and elegant meal that never disappoints."
            }
            query.contains("sandwich") || query.contains("light") || query.contains("lunch") -> {
                "🥪 **A light and satisfying classic!**\n\nOur gourmet **Club Sandwich Deluxe (\$15.00)** is stacked within gorgeous toasted sourdough, tender turkey breast, hickory bacon, and creamy herb mayonnaise. It is balanced, crispy, and ideal for a energetic mid-day booster."
            }
            query.contains("coffee") || query.contains("drink") || query.contains("morning") || query.contains("sweet") || query.contains("beverage") -> {
                "☕ **Elevate your energy levels!**\n\nTreat yourself to our single-origin **Iced Latte Arabica (\$8.00)**. Blended with organic chilled espresso and freshly frothed, silky milk, it offers a premium, smooth flavor profile designed to lift your spirits instantly!"
            }
            query.contains("healthy") || query.contains("protein") || query.contains("low") || query.contains("diet") -> {
                "🥗 **Healthy & Protein-Packed Choices!**\n\nHere are your best options:\n• **Club Sandwich Deluxe (\$15.00)** - Fresh lean turkey, tomatoes, lettuce on toasted sourdough.\n• **Creamy Pesto Pasta (\$22.00)** - Rich in protein from succulent roasted chicken breasts.\nBoth deliver excellent nutritional balance without compromising on premium chef flavors!"
            }
            query.contains("cheap") || query.contains("budget") || query.contains("under") -> {
                "🏷️ **Budget-Friendly Chef Recommendations!**\n\nYour best wallet-friendly options are:\n• **Iced Latte Arabica (\$8.00)** - Premium single-origin refresh.\n• **Club Sandwich Deluxe (\$15.00)** - Deeply filling deli-style classic.\n• **Crunchy Fried Chicken (\$18.00)** - Crispy, flavorful comfort food."
            }
            else -> {
                "✨ **Greetings Gourmet!**\n\nCravings can be mysterious! I recommend looking at our highly popular duo:\n1. **Pizza Margherita (\$20.00)** - Pure Italian comfort, fresh basil & mozzarella.\n2. **Smoked Truffle Burger (\$26.00)** - Double down on luxury with prime beef & creamy truffle aioli.\n\nTell me: are you looking for a hearty gourmet meal, a light sandwich lunch, or something sweet to drink?"
            }
        }
    }
}
