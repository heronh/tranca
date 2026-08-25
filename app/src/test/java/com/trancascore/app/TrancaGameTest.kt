package com.trancascore.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrancaGameTest {
    @Test
    fun `totals accumulate every round`() {
        val game = TrancaGame()
            .addRound(teamOne = 120, teamTwo = 80)
            .addRound(teamOne = 45, teamTwo = 110)

        assertEquals(165, game.teamOneTotal)
        assertEquals(190, game.teamTwoTotal)
    }

    @Test
    fun `negative scores are included`() {
        val game = TrancaGame()
            .addRound(teamOne = -20, teamTwo = 50)

        assertEquals(-20, game.teamOneTotal)
        assertEquals(50, game.teamTwoTotal)
    }

    @Test
    fun `removing last round updates totals`() {
        val game = TrancaGame()
            .addRound(teamOne = 100, teamTwo = 90)
            .addRound(teamOne = 30, teamTwo = 60)
            .removeLastRound()

        assertEquals(1, game.rounds.size)
        assertEquals(100, game.teamOneTotal)
        assertEquals(90, game.teamTwoTotal)
    }

    @Test
    fun `reset clears rounds and scores`() {
        val game = TrancaGame()
            .addRound(teamOne = 100, teamTwo = 90)
            .reset()

        assertTrue(game.rounds.isEmpty())
        assertEquals(0, game.teamOneTotal)
        assertEquals(0, game.teamTwoTotal)
    }
}
