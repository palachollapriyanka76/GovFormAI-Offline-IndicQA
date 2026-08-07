# GovFormAI-Offline-IndicQA

An offline, on-device AI assistant designed for government form understanding and Question Answering across Indic languages.

---

## Repository Scaffold & Developer Workflow

To ensure seamless collaboration across hardware platforms and machines, all pushes and pulls follow a strictly shared folder structure:

| Directory | Lead / Role | Purpose & Contents |
| :--- | :--- | :--- |
| [`/model`](file:///C:/GovFormAI-Offline-IndicQA/model) | **Developer A** | Checkpoints, GGUF conversion scripts, and quantization builds (`Q4_K_M`, `Q5_K_M`, etc.). |
| [`/eval`](file:///C:/GovFormAI-Offline-IndicQA/eval) | **Developer B** | Scoring harness (`eval_indicqa.py`), test datasets, Exact Match / F1 / BLEU metric calculators, and evaluation results. |
| [`/bench`](file:///C:/GovFormAI-Offline-IndicQA/bench) | **Developer B** | On-device benchmarking scripts (`on_device_profile.py`) measuring TTFT (Time-To-First-Token), tokens/sec, and RAM footprint across mobile SoCs. |
| [`/app`](file:///C:/GovFormAI-Offline-IndicQA/app) | **Developer C** | Android project scaffold (Kotlin UI, offline inference engine integration via llama.cpp/ONNX). |
| [`/docs`](file:///C:/GovFormAI-Offline-IndicQA/docs) | **All** | System documentation ([`docs/README.md`](file:///C:/GovFormAI-Offline-IndicQA/docs/README.md)) and operational limits ([`docs/LIMITS.md`](file:///C:/GovFormAI-Offline-IndicQA/docs/LIMITS.md)). |

---

## Quickstart Guide

### 1. Model Quantization & Conversion (Developer A)
```bash
cd model
python convert_to_gguf.py --model-path ./checkpoints/my_torch_model --out-dir ./quant --format q4_k_m
```

### 2. Run Evaluation Scoring (Developer B)
```bash
cd eval
python harness/eval_indicqa.py --model-path ../model/quant/indicqa-model-q4_k_m.gguf --dataset ./datasets/test_indicqa.json
```

### 3. Run On-Device Benchmarking (Developer B)
```bash
cd bench
python scripts/on_device_profile.py --model-path ../model/quant/indicqa-model-q4_k_m.gguf --iterations 50
```

### 4. Build Android Application (Developer C)
```bash
cd app
./gradlew assembleDebug
```

---

## Contribution Rules & Git Hygiene
- **Never push large binary models (`.bin`, `.gguf`, `.safetensors`, `.ckpt`, `.apk`) to Git.** Place quantized models locally in `model/quant/` or download via external storage / Hugging Face Hub releases.
- Always commit code, scripts, configs, and documentation to their designated folder in this scaffold.
