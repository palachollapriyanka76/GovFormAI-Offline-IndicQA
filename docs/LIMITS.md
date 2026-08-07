# Operational & Hardware Limits — GovFormAI-Offline-IndicQA

This document details technical boundaries, hardware constraints, supported Indic languages, memory footprints, and known operational edge cases.

---

## 1. Hardware & Memory Constraints (Android Devices)

| Quantization Target | Minimum System RAM | Peak RSS Memory | Recommended Android Hardware |
| :--- | :--- | :--- | :--- |
| **`Q4_K_M` (Recommended)** | 6 GB RAM | ~3.8 GB RAM | Snapdragon 7 Gen 1+, MediaTek Dimensity 8000+, 6GB+ RAM |
| **`Q5_K_M`** | 8 GB RAM | ~4.5 GB RAM | Snapdragon 8 Gen 2/3, 8GB+ RAM |
| **`Q8_0`** | 12 GB RAM | ~7.2 GB RAM | High-end Android flagship / Workstation test rigs |

> [!WARNING]
> Running models with RSS memory close to the total device RAM can trigger the Android Out-Of-Memory (LMK / Low Memory Killer) daemon, terminating the application process.

---

## 2. Context Window & Token Limits

- **Maximum Context Window**: 2,048 tokens (optimized for mobile RAM footprint).
- **Prompt Token Limit**: 1,536 tokens.
- **Max Generation Output**: 512 tokens.

---

## 3. Language & Indic QA Coverage

### Supported Indic Languages
1. **Hindi (`hi`)** — Full support (95%+ EM field accuracy)
2. **Tamil (`ta`)** — High support
3. **Telugu (`te`)** — High support
4. **Kannada (`kn`)** — High support
5. **Marathi (`mr`)** — High support
6. **Bengali (`bn`)** — High support
7. **Gujarati (`gu`)** — Medium support

---

## 4. Known Edge Cases & Operational Limits

1. **Scanned PDF Optical Quality**: Low-dpi scans (< 150 DPI) or severely degraded hand-written government form images require pre-processing (OCR cleaning) before passing context to the QA model.
2. **Zero-Network Dependency**: Model runs 100% offline. No remote cloud API calls or internet connectivity are required or permitted during execution.
3. **Thermal Throttling**: Extended continuous inference (> 10 consecutive form completions) on passive mobile cooling may reduce generation throughput by 15–20%.
