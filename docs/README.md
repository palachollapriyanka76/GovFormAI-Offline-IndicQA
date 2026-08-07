# Project Architecture & Developer Handbook — GovFormAI-Offline-IndicQA

This handbook outlines the system architecture, component breakdown, team responsibilities, and operational guidelines for **GovFormAI-Offline-IndicQA**.

---

## Team Division of Responsibility

| Module | Responsible Developer | Key Output Deliverables |
| :--- | :--- | :--- |
| [`/model`](file:///C:/GovFormAI-Offline-IndicQA/model) | **Developer A** | Fine-tuned PyTorch checkpoints, GGUF conversion scripts, quantized builds (`Q4_K_M`, `Q5_K_M`). |
| [`/eval`](file:///C:/GovFormAI-Offline-IndicQA/eval) | **Developer B** | IndicQA scoring harness (`eval_indicqa.py`), Exact Match / F1 metrics, validation reports. |
| [`/bench`](file:///C:/GovFormAI-Offline-IndicQA/bench) | **Developer B** | On-device profiling scripts (`on_device_profile.py`), latency/RAM/tokens-per-sec benchmark logs. |
| [`/app`](file:///C:/GovFormAI-Offline-IndicQA/app) | **Developer C** | Android app (`MainActivity.kt`), Kotlin Jetpack Compose UI, offline LLM runtime integration. |
| [`/docs`](file:///C:/GovFormAI-Offline-IndicQA/docs) | **All** | System documentation and hardware/model operational limits ([`docs/LIMITS.md`](file:///C:/GovFormAI-Offline-IndicQA/docs/LIMITS.md)). |

---

## Synchronized Git Workflow

To keep all developers on the exact same directory scaffold regardless of operating system or hardware setup:
1. Always clone or work within the root directory structure (`/model`, `/eval`, `/bench`, `/app`, `/docs`).
2. Heavy model weights are automatically ignored by `.gitignore`. Do not override `.gitignore` to commit large binary weights directly to GitHub.
3. Quantized GGUF models are stored locally in `model/quant/` or distributed via external Hugging Face repository releases.
