# /eval — Developer B: Scoring Harness & Evaluation Results

This directory contains evaluation datasets, scoring metrics (Exact Match, F1, Indic BLEU/ROUGE), and evaluation reports for offline QA performance on government forms.

---

## Directory Structure

```
eval/
├── harness/
│   ├── eval_indicqa.py  # Evaluation execution harness runner
│   └── metrics.py        # Accuracy, Exact Match (EM), F1, and BLEU scoring metrics
├── datasets/             # Test evaluation datasets (IndicQA form filling pairs)
├── results/              # Output evaluation logs and JSON score reports
└── README.md             # Guide to running evaluations
```

---

## Running Evaluations

To run evaluation on a quantized model:

```bash
python harness/eval_indicqa.py \
    --model-path ../model/quant/indicqa-model-q4_k_m.gguf \
    --dataset ./datasets/test_indicqa.json \
    --out-file ./results/eval_q4_k_m_report.json
```

## Metrics Calculated
- **Exact Match (EM)**: Percentage of predictions matching exact standard government field responses.
- **F1 Score**: Word-level token overlap between reference and predicted answers.
- **Indic Script Precision**: Measures token correctness in Indic scripts (Hindi, Tamil, Telugu, Kannada, Marathi, Bengali, etc.).
