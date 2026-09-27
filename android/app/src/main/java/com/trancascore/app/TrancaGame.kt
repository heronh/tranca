package com.trancascore.app

data class RoundScore(
    val teamOne: Int,
    val teamTwo: Int,
)

data class TrancaGame(
    val rounds: List<RoundScore> = emptyList(),
) {
    val teamOneTotal: Int
        get() = rounds.sumOf(RoundScore::teamOne)

    val teamTwoTotal: Int
        get() = rounds.sumOf(RoundScore::teamTwo)

    fun addRound(teamOne: Int, teamTwo: Int): TrancaGame =
        copy(rounds = rounds + RoundScore(teamOne, teamTwo))

    fun removeRound(index: Int): TrancaGame =
        if (index in rounds.indices) {
            copy(rounds = rounds.filterIndexed { position, _ -> position != index })
        } else {
            this
        }

    fun reset(): TrancaGame =
        copy(rounds = emptyList())
}

object ScoreInput {
    /** Hyphen, minus sign, and the dashes keyboards insert in place of "-". */
    private val minusSigns = setOf('-', '−', '–', '—', '‐', '‑')

    /**
     * Keeps digits and one leading sign. The last "+" or minus in the text wins,
     * so both "-10" and "10-" are negative, and "+" forces the number positive.
     */
    fun sanitize(text: String): String {
        val trimmed = text.trim()
        var negative = false
        for (char in trimmed) {
            when {
                char in minusSigns -> negative = true
                char == '+' -> negative = false
            }
        }
        val digits = trimmed.filter { it in '0'..'9' }
        if (digits.isEmpty()) return if (negative) "-" else ""
        return if (negative) "-$digits" else digits
    }

    fun toggleSign(text: String): String {
        val cleaned = sanitize(text)
        return if (cleaned.startsWith("-")) cleaned.removePrefix("-") else "-$cleaned"
    }

    fun parse(text: String): Int? {
        val digits = text.removePrefix("-")
        if (digits.isEmpty() || !digits.all { it in '0'..'9' }) return null
        return text.toIntOrNull()
    }
}

fun teamName(name: String, fallback: String): String =
    name.trim().ifEmpty { fallback }
