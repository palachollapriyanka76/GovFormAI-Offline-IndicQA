# /model — Developer A: Checkpoints, Quantization & GGUF Builds

This directory manages fine-tuned model checkpoints, format conversions, and GGUF quantization builds for on-device deployment.

---

## Directory Structure

```
model/
├── checkpoints/       # PyTorch / HuggingFace fine-tuned checkpoints (Git-ignored)
├── quant/             # Quantized GGUF outputs (Q4_K_M, Q5_K_M, Q8_0) (Git-ignored)
├── convert_to_gguf.py # Conversion script template using llama.cpp / Hugging Face tools
└── README.md          # Workflow documentation
```

---

## Model Conversion & Quantization Workflow

### 1. Place Fine-Tuned Checkpoints
Copy or save fine-tuned Hugging Face / PyTorch weights into `model/checkpoints/`:
```bash
# Example structure inside checkpoints/
checkpoints/indicqa-lora-v1/
├── config.json
├── model.safetensors
└── tokenizer.json
```

### 2. Run GGUF Quantization
Use `convert_to_gguf.py` or llama.cpp conversion tools:
```bash
python convert_to_gguf.py \
    --checkpoint ./checkpoints/indicqa-lora-v1 \
    --out-dir ./quant \
    --quant-type Q4_K_M
```

### 3. Recommended Quantization Targets for Mobile
- **`Q4_K_M`**: Best balance of RAM usage (~3.8 GB for 7B model) and accuracy for Android devices.
- **`Q5_K_M`**: Higher accuracy, requiring ~4.5 GB RAM.
- **`Q8_0`**: High accuracy baseline for desktop / server evaluation.
