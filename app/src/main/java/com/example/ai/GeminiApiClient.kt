package com.example.ai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiApiClient {
    suspend fun generateContent(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null,
        temperature: Float = 0.2f
    ): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-3.5-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    this.temperature = temperature
                },
                systemInstruction = systemInstruction?.let { content { text(it) } }
            )
            
            val response = generativeModel.generateContent(prompt)
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun generateContentWithImage(
        apiKey: String,
        prompt: String,
        bitmap: Bitmap,
        systemInstruction: String? = null,
        temperature: Float = 0.2f
    ): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-3.5-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    this.temperature = temperature
                },
                systemInstruction = systemInstruction?.let { content { text(it) } }
            )
            
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }
            
            val response = generativeModel.generateContent(inputContent)
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
