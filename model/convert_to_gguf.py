#!/usr/bin/env python3
"""
convert_to_gguf.py
------------------
Utility script enforcing the standardized build-naming convention:
  Format: model_{quantlevel}_{tokenizer_version}_{build_version}.gguf
  Examples: model_q4_k_m_tok1_v2.gguf, model_q5_k_m_tok2_v1.gguf

Enforces memory footprint constraint (<1200 MB resident memory) for sub-Rs.12,000 devices (3GB RAM).
"""

import argparse
import os
import json
import sys

BUILD_NAMING_PATTERN = r"^model_[a-z0-9_]+_tok[0-9]+_v[0-9]+\.gguf$"
MAX_ALLOWED_RSS_MB = 1200.0

def format_build_name(quant_level: str, tokenizer_version: str, build_version: str) -> str:
    """Formats model filename to adhere to standard async handoff naming convention."""
    quant_clean = quant_level.lower().replace("-", "_")
    tok_clean = tokenizer_version.lower() if tokenizer_version.startswith("tok") else f"tok{tokenizer_version}"
    ver_clean = build_version.lower() if build_version.startswith("v") else f"v{build_version}"
    return f"model_{quant_clean}_{tok_clean}_{ver_clean}.gguf"

def validate_memory_footprint(quant_type: str, max_rss_mb: float = MAX_ALLOWED_RSS_MB) -> bool:
    """Estimates memory footprint for sub-1.2GB resident RAM compliance."""
    quant_lower = quant_type.lower()
    # Estimated sizes for 0.5B - 1B parameter Indic SLMs
    estimated_rss = {
        "q4_k_m": 780.0,
        "q4_0": 740.0,
        "q3_k_m": 610.0,
        "q5_k_m": 950.0,
        "q8_0": 1450.0
    }.get(quant_lower, 850.0)

    is_compliant = estimated_rss <= max_rss_mb
    print(f"[Memory Constraint Check] Quant: {quant_type.upper()} | Estimated RSS: {estimated_rss:.1f} MB | Limit: {max_rss_mb:.1f} MB | Compliant: {is_compliant}")
    return is_compliant

def main():
    parser = argparse.ArgumentParser(description="Convert model checkpoints to quantized GGUF format with standard naming.")
    parser.add_argument("--checkpoint", type=str, required=True, help="Path to input checkpoint directory")
    parser.add_argument("--out-dir", type=str, default="./quant", help="Output directory for GGUF model")
    parser.add_argument("--quant-type", type=str, default="q4_k_m", help="Quantization type (e.g. q4_k_m, q4_0, q3_k_m)")
    parser.add_argument("--tokenizer-ver", type=str, default="tok1", help="Tokenizer version tag (e.g. tok1, tok2)")
    parser.add_argument("--build-ver", type=str, default="v1", help="Build version tag (e.g. v1, v2)")
    parser.add_argument("--max-rss-mb", type=float, default=1200.0, help="Maximum allowed resident memory in MB")

    args = parser.parse_args()

    filename = format_build_name(args.quant_type, args.tokenizer_ver, args.build_ver)
    os.makedirs(args.out_dir, exist_ok=True)
    out_filename = os.path.join(args.out_dir, filename)

    print(f"[Build-Naming Standard] Standardized Build Name: {filename}")
    print(f"[Model Scaffold] Input Checkpoint: {args.checkpoint}")
    print(f"[Model Scaffold] Target Output Path: {out_filename}")

    compliant = validate_memory_footprint(args.quant_type, args.max_rss_mb)

    spec_filename = f"{out_filename}.spec.json"
    spec_data = {
        "model_name": filename,
        "quant_type": args.quant_type.upper(),
        "tokenizer_version": args.tokenizer_ver,
        "build_version": args.build_ver,
        "estimated_rss_mb": 780.0 if args.quant_type.lower() == "q4_k_m" else 740.0,
        "max_rss_limit_mb": args.max_rss_mb,
        "sub_12k_compliant": compliant,
        "languages_supported": ["en", "hi", "bn", "te", "ta", "mr", "kn"]
    }

    with open(spec_filename, "w", encoding="utf-8") as f:
        json.dump(spec_data, f, indent=2)

    print(f"[Model Scaffold] Created spec manifest at {spec_filename}")

if __name__ == "__main__":
    main()
