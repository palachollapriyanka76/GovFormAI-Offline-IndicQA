# /app — Developer C: Android Project & Offline Runtime

This directory contains the Android application for **GovFormAI-Offline-IndicQA**, providing an offline voice/text form assistant powered by local quantized GGUF models.

---

## Architecture Overview

- **UI Framework**: Android Jetpack Compose / Material 3.
- **Inference Runtime**: `llama.cpp` JNI bindings / ONNX Runtime / MediaPipe Task API for zero-network on-device inference.
- **Model Asset Management**: Local GGUF models are stored in `assets/models/` or loaded from external device storage (`/sdcard/Download/GovFormAI/`).

---

## Directory Scaffold

```
app/
├── src/
│   └── main/
│       ├── AndroidManifest.xml
│       └── java/com/govformai/indicqa/
│           └── MainActivity.kt
├── build.gradle.kts      # Gradle app build setup
├── settings.gradle.kts   # Gradle project settings
└── README.md             # Developer C guide
```

---

## Building & Deploying

1. Copy the quantized GGUF model produced by Developer A (`model/quant/indicqa-model-q4_k_m.gguf`) into the test device or app assets.
2. Build the Android app package:
   ```bash
   ./gradlew assembleDebug
   ```
3. Install the APK onto connected Android test hardware:
   ```bash
   adb install -r build/outputs/apk/debug/app-debug.apk
   ```
