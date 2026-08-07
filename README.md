# GovFormAI-Offline-IndicQA

An offline, on-device AI assistant designed for government form understanding and Question Answering across Indic languages.

---

## Repository Scaffold & Developer Workflow

To ensure seamless collaboration across hardware platforms and machines, all pushes and pulls follow a strictly shared folder structure:

| Directory | Lead / Role | Purpose & Contents |
| :--- | :--- | :--- |
| [`/model`](file:///C:/GovFormAI-Offline-IndicQA/model) | **Developer A** | Checkpoints, GGUF conversion scripts, and quantization builds (`q4_k_m`, `q5_k_m`, etc.). |
| [`/eval`](file:///C:/GovFormAI-Offline-IndicQA/eval) | **Developer B** | Scoring harness (`eval_indicqa.py`), test datasets, Exact Match / F1 / BLEU metric calculators, and evaluation results. |
| [`/bench`](file:///C:/GovFormAI-Offline-IndicQA/bench) | **Developer B** | On-device benchmarking scripts (`on_device_profile.py`) measuring TTFT, tokens/sec, and RAM footprint across mobile SoCs. |
| [`/app`](file:///C:/GovFormAI-Offline-IndicQA/app) | **Developer C** | Android project scaffold (Kotlin UI, offline inference engine integration via llama.cpp/ONNX). |
| [`/docs`](file:///C:/GovFormAI-Offline-IndicQA/docs) | **All** | System documentation ([`docs/README.md`](file:///C:/GovFormAI-Offline-IndicQA/docs/README.md)) and operational limits ([`docs/LIMITS.md`](file:///C:/GovFormAI-Offline-IndicQA/docs/LIMITS.md)). |
| [`/datasets/ps_i2_ondevice`](file:///C:/GovFormAI-Offline-IndicQA/datasets/ps_i2_ondevice) | **All** | Shared dataset (120 forms, `qa_flat.csv`, fertility probe set, 4 device profiles, zeroed telemetry log). |

---

## ⚠️ Important Datasets Note (`datasets/ps_i2_ondevice/`)

Everyone must read [`datasets/ps_i2_ondevice/README.md`](file:///C:/GovFormAI-Offline-IndicQA/datasets/ps_i2_ondevice/README.md) before touching the benchmark harness. The `telemetry_raw.log` file is **deliberately zeroed out** to prevent stale hardware thermal/memory baseline skew and enforce fresh cold-boot profiling.

---

## Standardized Build-Naming Convention

To ensure clear async handoffs, all quantized GGUF artifacts MUST be named using this exact convention:

$$\text{Format: } \mathbf{\text{model\_\{quantlevel\}\_\{tokenizer-version\}\_\{build-version\}.gguf}}$$

Examples:
- `model_q4_k_m_tok1_v2.gguf`
- `model_q5_k_m_tok2_v1.gguf`

---

## Async Communication Channel

Updates, build releases, and score handoffs take place on WhatsApp / Discord / Slack using the standard ping format:
`[NEW BUILD READY] model_q4_k_m_tok1_v2.gguf | Fertility: 1.34 tok/word | RAM: 3.8GB`

---

## Git Access Verification

Confirm push/pull access for all 3 developers before starting development:
```bash
git clone https://github.com/palachollapriyanka76/GovFormAI-Offline-IndicQA.git
cd GovFormAI-Offline-IndicQA
git pull origin main
```
