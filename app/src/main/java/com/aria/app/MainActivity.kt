package com.aria.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.aria.app.ui.AriaApp
import com.aria.app.ui.AriaViewModel
import com.aria.app.ui.theme.AriaTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: AriaViewModel by viewModels {
        AriaViewModel.factory(applicationContext)
    }

    private var pendingVoiceCapture by mutableStateOf(false)

    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
            if (text.isNotBlank()) {
                viewModel.onUserMessage(text)
            }
        }
        pendingVoiceCapture = false
    }

    private val audioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingVoiceCapture) launchSpeechRecognizer()
        if (!granted) viewModel.postSystemMessage("Microphone permission was not granted.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AriaTheme {
                AriaApp(
                    state = viewModel.uiState,
                    onTextMessage = viewModel::onUserMessage,
                    onSaveSettings = viewModel::saveSettings,
                    onVoiceInput = {
                        pendingVoiceCapture = true
                        ensureAudioPermissionAndCapture()
                    }
                )
            }
        }
    }

    private fun ensureAudioPermissionAndCapture() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchSpeechRecognizer()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun launchSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to ARIA")
        }
        speechRecognizerLauncher.launch(intent)
    }
}
