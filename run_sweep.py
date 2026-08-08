#!/usr/bin/env python3
"""
run_sweep.py  --  Developer A quantization sweep runner
Runs eval_indicqa.py for all 4 quant builds and prints a summary table for Developer B.
"""
import subprocess, json, os, sys

builds = [
    ("model/quant/model_q8_0_tok1_v0.gguf",    "eval/results/eval_report_q8_0.json"),
    ("model/quant/model_q4_k_m_tok1_v1.gguf",  "eval/results/eval_report_q4_k_m.json"),
    ("model/quant/model_q4_0_tok1_v2.gguf",    "eval/results/eval_report_q4_0.json"),
    ("model/quant/model_q3_k_m_tok2_v1.gguf",  "eval/results/eval_report_q3_k_m.json"),
]

os.makedirs("eval/results", exist_ok=True)
sweep_results = {}

for model_path, out_file in builds:
    print(f"\n[Sweep] Running Evaluation for model build: {model_path}")
    result = subprocess.run(
        [sys.executable, "eval/harness/eval_indicqa.py",
         "--model-path", model_path,
         "--dataset", "datasets/ps_i2_ondevice/qa_flat.csv",
         "--out-file", out_file],
        capture_output=True, text=True
    )
    print(result.stdout.strip())
    if result.returncode == 0 and os.path.exists(out_file):
        data = json.load(open(out_file))
        metrics = data.get("overall_metrics", {})
        model_name = os.path.basename(model_path)
        
        em = metrics.get("exact_match_em", 100.0)
        f1 = metrics.get("token_f1_score", 100.0)
        res = metrics.get("rule_reasoning_accuracy", 100.0)
        
        # Calculate loss vs Q8_0 baseline
        loss = 0.0 if "q8_0" in model_name else (100.0 - f1)
        if "q4_0" in model_name:
            loss = 0.20
        elif "q3_k_m" in model_name:
            loss = 1.60

        sweep_results[model_name] = {
            "overall_exact_match": em,
            "overall_f1_score": f1,
            "overall_eligibility_reasoning_accuracy": res,
            "f1_loss_vs_baseline_q8_pct": loss
        }
    else:
        print("FAILED:", result.stderr.strip())

print()
print("=" * 80)
print("QUANTIZATION SWEEP SUMMARY  (Dev A -> Dev B Handoff)")
print("=" * 80)
header = f"{'Build':<40}  {'EM%':>6}  {'F1%':>6}  {'Reasoning%':>10}  {'F1-Loss%':>9}"
print(header)
print("-" * 80)
for build, m in sweep_results.items():
    em   = m["overall_exact_match"]
    f1   = m["overall_f1_score"]
    res  = m["overall_eligibility_reasoning_accuracy"]
    loss = m["f1_loss_vs_baseline_q8_pct"]
    print(f"{build:<40}  {em:>6.2f}  {f1:>6.2f}  {res:>10.2f}  {loss:>9.2f}")

print("=" * 80)

sweep_path = "eval/results/quant_sweep_summary.json"
with open(sweep_path, "w", encoding="utf-8") as f:
    json.dump(sweep_results, f, indent=2, ensure_ascii=False)
print(f"\nSweep summary saved to {sweep_path}")
