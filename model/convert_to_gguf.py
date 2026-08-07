#!/usr/bin/env python3
"""
convert_to_gguf.py
------------------
Utility script enforcing the standardized build-naming convention:
  Format: model_{quantlevel}_{tokenizer_version}_{build_version}.gguf
  Examples: model_q4_k_m_tok1_v2.gguf, model_q5_k_m_tok2_v1.gguf
"""

import argparse
import os
import re
import sys

BUILD_NAMING_PATTERN = r"^model_[a-z0-9_]+_tok[0-9]+_v[0-9]+\.gguf$"

def format_build_name(quant_level: str, tokenizer_version: str, build_version: str) -> str:
    """Formats model filename to adhere to standard async handoff naming convention."""
    quant_clean = quant_level.lower().replace("-", "_")
    tok_clean = tokenizer_version.lower() if tokenizer_version.startswith("tok") else f"tok{tokenizer_version}"
    ver_clean = build_version.lower() if build_version.startswith("v") else f"v{build_version}"
    return f"model_{quant_clean}_{tok_clean}_{ver_clean}.gguf"

def main():
    parser = argparse.ArgumentParser(description="Convert model checkpoints to quantized GGUF format with standard naming.")
    parser.add_argument("--checkpoint", type=str, required=True, help="Path to input checkpoint directory")
    parser.add_argument("--out-dir", type=str, default="./quant", help="Output directory for GGUF model")
    parser.add_argument("--quant-type", type=str, default="q4_k_m", help="Quantization type (e.g. q4_k_m, q5_k_m, q8_0)")
    parser.add_argument("--tokenizer-ver", type=str, default="tok1", help="Tokenizer version tag (e.g. tok1, tok2)")
    parser.add_argument("--build-ver", type=str, default="v1", help="Build version tag (e.g. v1, v2)")

    args = parser.parse_args()

    filename = format_build_name(args.quant_type, args.tokenizer_ver, args.build_ver)
    os.makedirs(args.out_dir, exist_ok=True)
    out_filename = os.path.join(args.out_dir, filename)

    print(f"[Build-Naming Standard] Standardized Build Name: {filename}")
    print(f"[Model Scaffold] Input Checkpoint: {args.checkpoint}")
    print(f"[Model Scaffold] Target Output Path: {out_filename}")
    print("\nNote: Execute conversion with llama.cpp 'convert_hf_to_gguf.py' and 'llama-quantize' binary.")

if __name__ == "__main__":
    main()
