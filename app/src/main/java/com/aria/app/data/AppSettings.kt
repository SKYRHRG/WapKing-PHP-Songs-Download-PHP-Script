package com.aria.app.data

data class AppSettings(
    val openAiApiKey: String = "",
    val claudeApiKey: String = "",
    val elevenLabsApiKey: String = "",
    val elevenLabsVoiceId: String = "",
    val activeModel: String = "gpt-4o-mini",
    val personalityMode: String = "Companion",
    val preferredLanguage: String = "Bangla/English Mix"
)
