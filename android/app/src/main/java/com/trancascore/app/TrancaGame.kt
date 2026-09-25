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
    /** Keeps only digits and a leading minus, so "-10abc" becomes "-10". */
    fun sanitize(text: String): String {
        val negative = text.trim().startsWith("-")
        val digits = text.filter { it in '0'..'9' }
        if (digits.isEmpty()) return if (negative) "-" else ""
        return if (negative) "-$digits" else digits
    }

    fun parse(text: String): Int? {
        val digits = text.removePrefix("-")
        if (digits.isEmpty() || !digits.all { it in '0'..'9' }) return null
        return text.toIntOrNull()
    }
}

fun teamName(name: String, fallback: String): String =
    name.trim().ifEmpty { fallback }
