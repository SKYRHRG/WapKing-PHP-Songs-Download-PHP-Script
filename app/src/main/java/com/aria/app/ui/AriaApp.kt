package com.aria.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aria.app.data.AppSettings
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AriaApp(
    state: StateFlow<AriaUiState>,
    onTextMessage: (String) -> Unit,
    onSaveSettings: (AppSettings) -> Unit,
    onVoiceInput: () -> Unit
) {
    val uiState by state.collectAsState()
    var message by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ARIA") },
                actions = {
                    Button(onClick = { showSettings = !showSettings }) {
                        Text(if (showSettings) "Chat" else "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF0B1020), Color(0xFF1E1B4B))))
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            if (showSettings) {
                SettingsPanel(settings = uiState.settings, onSave = onSaveSettings)
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages) { msg ->
                        ChatBubble(message = msg)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Type a message") }
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onVoiceInput, modifier = Modifier.padding(start = 8.dp)) { Text("🎙") }
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = message.isNotBlank() && !uiState.isLoading,
                    onClick = {
                        val sending = message.trim()
                        message = ""
                        onTextMessage(sending)
                    }
                ) {
                    Text(if (uiState.isLoading) "Thinking..." else "Send")
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message.content,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (message.fromUser) Color(0xFF1D4ED8) else Color(0xFFE5E7EB)
        )
    }
}

@Composable
private fun SettingsPanel(settings: AppSettings, onSave: (AppSettings) -> Unit) {
    var local by remember(settings) { mutableStateOf(settings) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("API Keys", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
        item {
            OutlinedTextField(
                value = local.openAiApiKey,
                onValueChange = { local = local.copy(openAiApiKey = it) },
                label = { Text("OpenAI API Key") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = local.claudeApiKey,
                onValueChange = { local = local.copy(claudeApiKey = it) },
                label = { Text("Claude API Key") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = local.elevenLabsApiKey,
                onValueChange = { local = local.copy(elevenLabsApiKey = it) },
                label = { Text("ElevenLabs API Key") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = local.elevenLabsVoiceId,
                onValueChange = { local = local.copy(elevenLabsVoiceId = it) },
                label = { Text("ElevenLabs Voice ID") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = local.activeModel,
                onValueChange = { local = local.copy(activeModel = it) },
                label = { Text("Active Model") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(onClick = { onSave(local) }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Settings")
            }
        }
    }
}
