# ARIA (Adaptive Real-time Intelligent Assistant)

Android Native starter project for **Phase 1** of ARIA.

## What is included

- Kotlin + Jetpack Compose app skeleton
- Chat UI (dark gradient theme)
- Settings panel for API keys and model configuration
- Local settings persistence using DataStore
- Basic speech-to-text input via Android `RecognizerIntent`
- OpenAI chat integration (`/v1/chat/completions`) with `gpt-4o-mini` default

## Open in Android Studio

1. Open Android Studio (Hedgehog+ recommended).
2. Choose **Open** and select this project root.
3. Let Android Studio install SDKs/build tools if prompted.
4. Sync Gradle.
5. Run the `app` module on Android 10+ emulator/device.

## Immediate post-import checks

- In app Settings: add your OpenAI API key.
- Keep model as `gpt-4o-mini` initially.
- Tap **Save Settings**.
- Use text chat or press 🎙 for voice input.

## Notes

- ElevenLabs/Claude fields are wired in settings storage for next phases.
- Accessibility automation, WhatsApp control, wake-word, and local memory DB are intentionally staged for later phases.
- If Gradle wrapper regeneration is requested by your local setup, run: `gradle wrapper --gradle-version 8.6`.

## Next recommended tasks

1. Add encrypted key storage using Android Keystore.
2. Implement SQLite memory schema + retrieval.
3. Add ElevenLabs TTS client and playback pipeline.
4. Add runtime permission workflow screen.
5. Add notification and accessibility services.
