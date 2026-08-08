# Operational & Hardware Limits — GovFormAI-Offline-IndicQA

This document details technical boundaries, hardware constraints, supported Indic languages, memory footprints, and operational rules for **PS-I2: "Intelligence Without the Data Centre"**.

---

## 1. Sub-Rs. 12,000 Android Hardware & Memory Constraints

| Target Hardware Class | System RAM | Resident Set Size (RSS) Limit | Quantization Target | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Sub-Rs. 12,000 Handset (Primary Target)** | **3 GB RAM** | **~1.2 GB (1,200 MB Ceiling)** | `Q4_K_M`, `Q4_0`, `Q3_K_M` | **Compliant (780 MB RSS)** |
| **Mid-Range Handset** | 4–6 GB RAM | ~2.5 GB RAM | `Q4_K_M`, `Q5_K_M` | Supported |
| **Flagship Handset** | 8+ GB RAM | ~3.8 GB RAM | `Q5_K_M`, `Q8_0` | Supported |

> [!IMPORTANT]
> **Sub-1.2 GB Resident Memory Rule**:
> To guarantee continuous offline execution on 3 GB RAM Android devices without being killed by the Android Low Memory Killer (LMK), model resident memory allocation stays strictly under **1,200 MB**.

---

## 2. Context Window & Token Limits

- **Maximum Context Window**: 2,048 tokens (optimized for sub-1.2GB mobile RAM footprint).
- **Prompt Token Limit**: 1,536 tokens.
- **Max Generation Output**: 512 tokens.
- **Target Time-To-First-Token (TTFT)**: < 350 ms.
- **Target Generation Speed**: >= 15 tokens/second.

---

## 3. Supported Languages (7 Indic Languages)

Full offline field QA and multi-clause eligibility rule reasoning across:
1. **English (`en`)**
2. **Hindi (`hi`)** — Devanagari script
3. **Bengali (`bn`)** — Bengali script
4. **Telugu (`te`)** — Telugu script
5. **Tamil (`ta`)** — Tamil script
6. **Marathi (`mr`)** — Devanagari script
7. **Kannada (`kn`)** — Kannada script

---

## 4. Zero-Network & Airplane Mode Guarantee

- **No Remote Network Calls**: 100% of OCR, form field schema extraction, and rule reasoning occurs locally on-device.
- **Real Airplane Mode Compatibility**: App operates smoothly with Wi-Fi, Cellular Data, and Bluetooth disabled.
- **Zero Cloud Fallback**: No API keys, cloud backends, or external servers are queried in the demo execution path.
