# /bench — Developer B: On-Device Benchmarking Scripts & Results

This directory measures performance metrics of quantized GGUF models executing offline on mobile hardware (Snapdragon, MediaTek, Tensor, etc.).

---

## Directory Structure

```
bench/
├── scripts/
│   └── on_device_profile.py  # Latency, RAM, and tokens/sec profiler script
├── results/                   # JSON benchmark execution logs across device hardware
└── README.md                  # Benchmarking execution guide
```

---

## Key Performance Indicators (KPIs)

- **TTFT (Time To First Token)**: Latency in milliseconds before the model outputs its initial token.
- **Generation Speed**: Tokens generated per second (`tokens/sec`).
- **Peak Memory (RAM)**: Maximum Resident Set Size (RSS) memory consumption in megabytes.
- **Power & Thermal Throttling**: Performance decay across long prompt processing sessions.

---

## Running Benchmarks

```bash
python scripts/on_device_profile.py \
    --model-path ../model/quant/indicqa-model-q4_k_m.gguf \
    --prompt "What document is needed for Aadhaar address update?" \
    --tokens 128 \
    --out-file ./results/bench_snapdragon_8gen3.json
```
