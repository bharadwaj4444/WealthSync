package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Sends a custom financial planning query to the Gemini 3.5 Flash API.
     * Integrates API key directly from BuildConfig.
     */
    suspend fun getFinancialAdvice(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API Key is not set or using placeholder value.")
            return@withContext "API_KEY_MISSING"
        }

        try {
            // Build request JSON
            val rootJson = JSONObject()
            
            // Contents
            val contentsArray = org.json.JSONArray()
            val textPart = JSONObject().put("text", prompt)
            val partsArray = org.json.JSONArray().put(textPart)
            val contentObj = JSONObject().put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            // System Instruction if provided
            if (!systemInstruction.isNullOrEmpty()) {
                val sysTextPart = JSONObject().put("text", systemInstruction)
                val sysPartsArray = org.json.JSONArray().put(sysTextPart)
                val sysContentObj = JSONObject().put("parts", sysPartsArray)
                rootJson.put("systemInstruction", sysContentObj)
            }

            // Generation config
            val genConfig = JSONObject().put("temperature", 0.3)
            rootJson.put("generationConfig", genConfig)

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "$BASE_URL?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBodyStr = response.body?.string()
                if (!response.isSuccessful || responseBodyStr == null) {
                    Log.e(TAG, "Request failed: ${response.code} - $responseBodyStr")
                    return@withContext "Error: Request failed with status ${response.code}."
                }

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No text response structure.")
                    }
                }
                return@withContext "Unrecognizable model response format. Please try again."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini generation: ${e.message}", e)
            return@withContext "Connection Error: Unable to reach AI Planner servers. Details: ${e.localizedMessage}"
        }
    }
}
