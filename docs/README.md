# Project Architecture & Developer Handbook — GovFormAI-Offline-IndicQA

This handbook outlines the system architecture, component breakdown, team responsibilities, dataset structure, build-naming standards, async communication protocols, and Git onboarding verification.

---

## 1. Team Division of Responsibility

| Module | Responsible Developer | Key Output Deliverables |
| :--- | :--- | :--- |
| [`/model`](file:///C:/GovFormAI-Offline-IndicQA/model) | **Developer A** | Fine-tuned PyTorch checkpoints, GGUF conversion scripts, quantized builds (`q4_k_m`, `q5_k_m`). |
| [`/eval`](file:///C:/GovFormAI-Offline-IndicQA/eval) | **Developer B** | IndicQA scoring harness (`eval_indicqa.py`), Exact Match / F1 metrics, validation reports. |
| [`/bench`](file:///C:/GovFormAI-Offline-IndicQA/bench) | **Developer B** | On-device profiling scripts (`on_device_profile.py`), latency/RAM/tokens-per-sec benchmark logs. |
| [`/app`](file:///C:/GovFormAI-Offline-IndicQA/app) | **Developer C** | Android app (`MainActivity.kt`), Kotlin Jetpack Compose UI, offline LLM runtime integration. |
| [`/docs`](file:///C:/GovFormAI-Offline-IndicQA/docs) | **All** | System documentation and hardware/model operational limits ([`docs/LIMITS.md`](file:///C:/GovFormAI-Offline-IndicQA/docs/LIMITS.md)). |

---

## 2. Shared Datasets & Hardware Profiles (`datasets/ps_i2_ondevice/`)

All developers must pull `datasets/ps_i2_ondevice/` containing:
- **120 Forms Index (`forms_120_index.json`)**: Form field schemas across 7 Indic languages.
- **QA Dataset (`qa_flat.csv`)**: Ground truth question-answer pairs for scoring.
- **Fertility Probe Set (`fertility_probe_set.json`)**: Subword token fertility tests for Indic scripts.
- **4 Hardware Profiles (`profiles/`)**: Snapdragon 8 Gen 3, Dimensity 9300, Tensor G3, Helio G99.

> [!WARNING]
> **Deliberately Zeroed Telemetry File (`telemetry_raw.log`)**:
> `telemetry_raw.log` is intentionally 0 bytes to prevent stale historical thermal/memory skews. Everyone reads the dataset README before touching the telemetry file to ensure fresh cold-boot hardware measurements.

---

## 3. Standardized Build-Naming Convention

To prevent async handoff errors (e.g. Developer B accidentally scoring an outdated model while Developer A is 3 builds ahead), all GGUF artifacts MUST follow this exact naming format:

$$\text{Format: } \mathbf{\text{model\_\{quantlevel\}\_\{tokenizer-version\}\_\{build-version\}.gguf}}$$

### Valid Build Examples:
- `model_q4_k_m_tok1_v2.gguf` (Quantization: Q4_K_M, Tokenizer: tok1, Build Version: v2)
- `model_q5_k_m_tok2_v1.gguf` (Quantization: Q5_K_M, Tokenizer: tok2, Build Version: v1)

---

## 4. Async Communication & Ping Protocol

All status updates, build releases, and scoring handoffs take place asynchronously on the team communication channel (WhatsApp / Discord / Slack thread) replacing unnecessary meetings.

### Standard Ping Format Template:
```text
🚨 [NEW BUILD READY]
Build: model_q4_k_m_tok1_v2.gguf
Location: HuggingFace / Local /quant/
Fertility Score: 1.34 tokens/word (Devanagari)
Peak RAM: 3.8 GB
Status: Ready for Dev B evaluation & Dev C Android testing.
```

---

## 5. Git Remote Verification Checklist (Hour 0 Requirement)

All 3 team members must verify Git installation, push permissions, and pull synchronization **BEFORE** starting development.

### Step-by-Step Verification:
```bash
# 1. Confirm Git installation
git --version

# 2. Clone / Pull repository
git clone https://github.com/palachollapriyanka76/GovFormAI-Offline-IndicQA.git
cd GovFormAI-Offline-IndicQA
git pull origin main

# 3. Test Push Permission (Create temporary test branch)
git checkout -b test_access_<your_name>
git push -u origin test_access_<your_name>

# 4. Clean up test branch
git checkout main
git push origin --delete test_access_<your_name>
```
