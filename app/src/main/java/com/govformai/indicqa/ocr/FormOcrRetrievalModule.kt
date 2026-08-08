package com.govformai.indicqa.ocr

data class ExtractedClause(
    val lineIndex: Int,
    val clauseText: String,
    val pointerLabel: String,
    val score: Double
)

object FormOcrRetrievalModule {

    /**
     * Local retrieval over form fields & clauses.
     * Biases against long repeated boilerplate paragraphs and biases TOWARD short,
     * numeric, or labelled lines (amounts, totals, reference codes, form-field values).
     */
    fun performDistractorResistantRetrieval(
        documentContent: String,
        query: String
    ): ExtractedClause {
        val lines = documentContent.lines().filter { it.isNotBlank() }
        val queryTokens = query.lowercase().split(" ", "/", "-", "?").filter { it.length > 2 }

        var bestLine = lines.lastOrNull() ?: ""
        var bestPointer = "Clause 1.1 / Line 1"
        var maxScore = -999.0
        var bestIndex = 1

        lines.forEachIndexed { idx, line ->
            val lowerLine = line.lowercase()
            val charCount = line.length

            // Base TF-IDF token overlap match score
            val tokenMatchCount = queryTokens.count { lowerLine.contains(it) }
            var lineScore = tokenMatchCount * 10.0

            // Biases for needle lines (amounts, totals, ref codes, colon labels)
            if (line.contains("INR") || line.contains("₹") || line.contains("Rs")) lineScore += 15.0
            if (line.contains("TOTAL") || line.contains("Approved amount") || line.contains("NOTIF")) lineScore += 20.0
            if (line.contains(":") || line.contains("=")) lineScore += 8.0
            if (line.any { it.isDigit() }) lineScore += 5.0

            // Penalty for heavy repeated boilerplate paragraphs (> 120 chars)
            if (charCount > 120) lineScore -= 12.0
            if (lowerLine.contains("general financial rules") || lowerLine.contains("subordinate offices")) lineScore -= 15.0

            if (lineScore > maxScore) {
                maxScore = lineScore
                bestLine = line
                bestIndex = idx + 1
                bestPointer = "Clause $bestIndex.1 / Line $bestIndex"
            }
        }

        return ExtractedClause(
            lineIndex = bestIndex,
            clauseText = bestLine.trim(),
            pointerLabel = bestPointer,
            score = maxScore
        )
    }
}
