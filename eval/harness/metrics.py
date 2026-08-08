#!/usr/bin/env python3
"""
metrics.py
----------
Metric functions for calculating Exact Match (EM), token-level F1 score,
Rule Reasoning Accuracy, and Field/Clause Pointer Attribution Accuracy.
Supports 7 languages: en, hi, bn, te, ta, mr, kn.
"""

import collections
import re
import string

INDIC_PUNCTUATION = '।॥' + string.punctuation

def normalize_text(s: str) -> str:
    """Lowercases text, removes Indic & English punctuation, and normalizes whitespace."""
    s = s.lower()
    s = "".join(ch for ch in s if ch not in set(INDIC_PUNCTUATION))
    return " ".join(s.split())

def compute_exact_match(prediction: str, truth: str) -> float:
    """Returns 100.0 if normalized prediction matches normalized ground truth exactly, else 0.0."""
    return 100.0 if normalize_text(prediction) == normalize_text(truth) else 0.0

def compute_f1(prediction: str, truth: str) -> float:
    """Computes token-level F1 score between prediction and ground truth across Indic scripts."""
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

def compute_clause_pointer_accuracy(pred_pointer: str, ground_truth_pointer: str) -> float:
    """Calculates accuracy of extracted field/clause pointer attribution."""
    norm_pred = normalize_text(pred_pointer)
    norm_truth = normalize_text(ground_truth_pointer)
    return 100.0 if norm_pred == norm_truth or norm_truth in norm_pred else 0.0

def compute_rule_reasoning_accuracy(prediction: str, truth: str) -> dict:
    """
    Evaluates rule-based eligibility reasoning:
    1. Decision Accuracy: whether the binary decision (Eligible/Yes vs Ineligible/No) matches.
    2. Rationale F1: token overlap on the explanatory clause.
    """
    norm_pred = normalize_text(prediction)
    norm_truth = normalize_text(truth)

    positive_keywords = {"eligible", "yes", "पात्र", "योग्य", "ಅರ್ಹರು", "தகுதியானவர்", "अर्हులు", "होय", "हाँ", "হ্যাঁ"}
    pred_pos = any(kw in norm_pred for kw in positive_keywords)
    truth_pos = any(kw in norm_truth for kw in positive_keywords)

    decision_match = 100.0 if (pred_pos == truth_pos) else 0.0
    rationale_f1 = compute_f1(prediction, truth)
    combined_score = (0.6 * decision_match) + (0.4 * rationale_f1)

    return {
        "decision_match": decision_match,
        "rationale_f1": rationale_f1,
        "combined_reasoning_score": combined_score
    }
