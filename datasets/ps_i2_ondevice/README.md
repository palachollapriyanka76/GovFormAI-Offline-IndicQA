# Datasets & Hardware Profiles — `datasets/ps_i2_ondevice/`

This folder contains the standardized **GovFormAI-Offline-IndicQA** evaluation benchmarks, hardware profiles, and probe sets used across all developer machines.

---

## Folder Structure

```
datasets/ps_i2_ondevice/
├── README.md                     # Dataset documentation & telemetry explanation
├── forms_120_index.json          # Index of the 120 government form schemas
├── qa_flat.csv                   # Flattened QA pairs for Indic form filling
├── fertility_probe_set.json      # Indic script tokenization & fertility probe questions
├── telemetry_raw.log             # Deliberately zeroed telemetry baseline log
└── profiles/                     # Hardware profiles for target mobile devices
    ├── snapdragon_8gen3.json
    ├── dimensity_9300.json
    ├── tensor_g3.json
    └── midrange_helio_g99.json
```

---

## ⚠️ CRITICAL NOTICE: Deliberately Zeroed Telemetry File (`telemetry_raw.log`)

> [!IMPORTANT]
> The file `telemetry_raw.log` is **deliberately zeroed out (0 bytes baseline)**.
> **Why?** Pre-filled telemetry logs from previous dev machines cause stale thermal and memory baseline skew. Zeroing this file forces every benchmark run (`bench/scripts/on_device_profile.py`) to generate fresh, uncalibrated runtime telemetry directly from the target mobile hardware during cold-boot execution.
> **DO NOT** populate or overwrite `telemetry_raw.log` manually prior to running benchmark tests.

---

## Dataset Components

1. **120 Government Forms Index (`forms_120_index.json`)**: Contains schema definitions for 120 Indic government application forms (Aadhaar, PAN, Voter ID, Ration Card, Income Certificate, Form 16, etc.).
2. **Flattened QA Pairs (`qa_flat.csv`)**: Canonical Question-Answer mappings for offline evaluation across 7 Indic languages.
3. **Fertility Probe Set (`fertility_probe_set.json`)**: Edge-case Indic script probe questions designed to test subword token fertility ratio (tokens per Indic character).
4. **4 Device Hardware Profiles (`profiles/`)**: Hardware resource limits and target execution thresholds for Snapdragon 8 Gen 3, Dimensity 9300, Tensor G3, and Helio G99.
