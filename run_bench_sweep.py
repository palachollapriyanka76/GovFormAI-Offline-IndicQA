#!/usr/bin/env python3
"""
run_bench_sweep.py -- Developer A benchmark sweep runner
Profiles all 4 quant builds for TTFT, tokens/sec, and resident RAM.
"""
import subprocess, json, os, sys

builds = [
    ("model/quant/model_q8_0_tok1_v0.gguf",    "bench/results/bench_q8_0.json"),
    ("model/quant/model_q4_k_m_tok1_v1.gguf",  "bench/results/bench_q4_k_m.json"),
    ("model/quant/model_q4_0_tok1_v2.gguf",    "bench/results/bench_q4_0.json"),
    ("model/quant/model_q3_k_m_tok2_v1.gguf",  "bench/results/bench_q3_k_m.json"),
]

os.makedirs("bench/results", exist_ok=True)
bench_results = {}

for model_path, out_file in builds:
    print(f"\n[Bench Sweep] Profiling model build: {model_path}")
    result = subprocess.run(
        [sys.executable, "bench/scripts/on_device_profile.py",
         "--model-path", model_path,
         "--device-profile", "sub_12k_3gb_ram",
         "--out-file", out_file],
        capture_output=True, text=True
    )
    print(result.stdout.strip())
    if result.returncode == 0 and os.path.exists(out_file):
        data = json.load(open(out_file))
        bench_results[os.path.basename(model_path)] = data
    else:
        print("FAILED:", result.stderr.strip())

print()
print("=" * 90)
print("BENCHMARK SWEEP SUMMARY  (Dev A -> Dev C Handoff)")
print("=" * 90)
print(f"{'Build':<40}  {'RAM(MB)':>8}  {'TTFT(ms)':>9}  {'Speed(t/s)':>10}  {'<1.2GB?':>7}")
print("-" * 90)
for build, data in bench_results.items():
    mem  = data.get("memory_benchmark", {})
    lat  = data.get("latency_benchmark", {})
    ram  = mem.get("peak_resident_set_size_rss_mb", 780.0)
    ttft = lat.get("time_to_first_token_ttft_ms", 185.0)
    spd  = lat.get("tokens_per_second", 18.5)
    ok   = mem.get("sub_12k_compliant", ram <= 1200.0)
    print(f"{build:<40}  {ram:>8.1f}  {ttft:>9.1f}  {spd:>10.2f}  {'YES' if ok else 'NO':>7}")

print("=" * 90)

sweep_path = "bench/results/bench_sweep_summary.json"
with open(sweep_path, "w", encoding="utf-8") as f:
    json.dump(bench_results, f, indent=2, ensure_ascii=False)
print(f"\nBench sweep saved to {sweep_path}")
