#!/usr/bin/env python3
"""
metrics.py
----------
Metric functions for calculating Exact Match (EM) and token-level F1 score for Indic text.
"""

import collections
import re
import string

def normalize_text(s: str) -> str:
    """Lowercases text and removes punctuation, articles, and extra whitespace."""
    s = s.lower()
    s = "".join(ch for ch in s if ch not in set(string.punctuation))
    return " ".join(s.split())

def compute_exact_match(prediction: str, truth: str) -> float:
    """Returns 100.0 if normalized prediction matches normalized ground truth exactly, else 0.0."""
    return 100.0 if normalize_text(prediction) == normalize_text(truth) else 0.0

def compute_f1(prediction: str, truth: str) -> float:
    """Computes token-level F1 score between prediction and ground truth."""
    pred_tokens = normalize_text(prediction).split()
    truth_tokens = normalize_text(truth).split()

    if not pred_tokens or not truth_tokens:
        return 100.0 if pred_tokens == truth_tokens else 0.0

    common = collections.Counter(pred_tokens) & collections.Counter(truth_tokens)
    num_same = sum(common.values())

    if num_same == 0:
        return 0.0

    precision = 1.0 * num_same / len(pred_tokens)
    recall = 1.0 * num_same / len(truth_tokens)
    f1 = (2 * precision * recall) / (precision + recall)
    return f1 * 100.0
