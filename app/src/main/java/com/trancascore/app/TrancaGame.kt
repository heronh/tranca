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

    fun removeLastRound(): TrancaGame =
        copy(rounds = rounds.dropLast(1))

    fun reset(): TrancaGame =
        copy(rounds = emptyList())
}
