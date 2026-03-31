package com.aria.app.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OpenAiService(
    private val client: OkHttpClient = OkHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {

    suspend fun chat(apiKey: String, model: String, systemPrompt: String, userPrompt: String): String {
        return kotlinx.coroutines.Dispatchers.IO.let { dispatcher ->
            kotlinx.coroutines.withContext(dispatcher) {
                runCatching {
                    val body = ChatRequest(
                        model = model,
                        messages = listOf(
                            Message("system", systemPrompt),
                            Message("user", userPrompt)
                        )
                    )
                    val req = Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer $apiKey")
                        .addHeader("Content-Type", "application/json")
                        .post(json.encodeToString(ChatRequest.serializer(), body).toRequestBody(JSON))
                        .build()

                    client.newCall(req).execute().use { res ->
                        val payload = res.body?.string().orEmpty()
                        if (!res.isSuccessful) {
                            return@withContext "OpenAI error ${res.code}: $payload"
                        }
                        val parsed = json.decodeFromString(ChatResponse.serializer(), payload)
                        parsed.choices.firstOrNull()?.message?.content ?: "No reply generated."
                    }
                }.getOrElse { err ->
                    "Network error: ${err.message}"
                }
            }
        }
    }

    private companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
    }
}

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: AssistantMessage
)

@Serializable
data class AssistantMessage(
    @SerialName("content") val content: String
)
