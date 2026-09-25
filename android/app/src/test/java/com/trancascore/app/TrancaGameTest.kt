package com.trancascore.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun `removing a round updates totals`() {
        val game = TrancaGame()
            .addRound(teamOne = 100, teamTwo = 90)
            .addRound(teamOne = 30, teamTwo = 60)
            .addRound(teamOne = 5, teamTwo = 10)
            .removeRound(1)

        assertEquals(listOf(100, 5), game.rounds.map(RoundScore::teamOne))
        assertEquals(105, game.teamOneTotal)
        assertEquals(100, game.teamTwoTotal)
    }

    @Test
    fun `removing an invalid index keeps rounds`() {
        val game = TrancaGame()
            .addRound(teamOne = 100, teamTwo = 90)
            .removeRound(3)

        assertEquals(1, game.rounds.size)
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

    @Test
    fun `score input keeps digits and a leading minus`() {
        assertEquals("-10", ScoreInput.sanitize("-10abc"))
        assertEquals("8", ScoreInput.sanitize("+8"))
        assertEquals("12", ScoreInput.sanitize("1.2"))
        assertEquals("-", ScoreInput.sanitize("-"))
        assertEquals("", ScoreInput.sanitize("abc"))
    }

    @Test
    fun `score input parses whole numbers only`() {
        assertEquals(-15, ScoreInput.parse("-15"))
        assertEquals(40, ScoreInput.parse("40"))
        assertNull(ScoreInput.parse("-"))
        assertNull(ScoreInput.parse(""))
    }

    @Test
    fun `team name falls back when empty`() {
        assertEquals("Ana e Bia", teamName("  Ana e Bia ", "Dupla 1"))
        assertEquals("Dupla 2", teamName("   ", "Dupla 2"))
    }
}
