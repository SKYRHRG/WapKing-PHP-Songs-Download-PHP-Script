package com.aria.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aria.app.data.AppSettings
import com.aria.app.data.SettingsRepository
import com.aria.app.network.OpenAiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AriaViewModel(
    private val settingsRepository: SettingsRepository,
    private val openAiService: OpenAiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AriaUiState())
    val uiState: StateFlow<AriaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun onUserMessage(message: String) {
        _uiState.update {
            it.copy(messages = it.messages + ChatMessage(content = message, fromUser = true), isLoading = true)
        }

        viewModelScope.launch {
            val settings = _uiState.value.settings
            if (settings.openAiApiKey.isBlank()) {
                postSystemMessage("OpenAI API key missing. Add it in Settings.")
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val reply = openAiService.chat(
                apiKey = settings.openAiApiKey,
                model = settings.activeModel,
                systemPrompt = "You are ARIA, a supportive assistant.",
                userPrompt = message
            )

            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage(content = reply, fromUser = false),
                    isLoading = false
                )
            }
        }
    }

    fun postSystemMessage(message: String) {
        _uiState.update {
            it.copy(messages = it.messages + ChatMessage(content = message, fromUser = false))
        }
    }

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch {
            settingsRepository.save(settings)
            postSystemMessage("Settings saved.")
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = SettingsRepository(context)
                    val service = OpenAiService()
                    @Suppress("UNCHECKED_CAST")
                    return AriaViewModel(repository, service) as T
                }
            }
    }
}

data class AriaUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Hey, I'm ARIA. Configure API keys in Settings to start.", false)
    ),
    val isLoading: Boolean = false,
    val settings: AppSettings = AppSettings()
)

data class ChatMessage(
    val content: String,
    val fromUser: Boolean
)
