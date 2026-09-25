import XCTest
@testable import TrancaScoreCore

final class TrancaGameTests: XCTestCase {
    func testTotalsAccumulateAllRounds() {
        var game = TrancaGame()

        game.addRound(teamOne: 120, teamTwo: 80)
        game.addRound(teamOne: 45, teamTwo: 110)

        XCTAssertEqual(game.teamOneTotal, 165)
        XCTAssertEqual(game.teamTwoTotal, 190)
    }

    func testNegativeScoresAreIncluded() {
        var game = TrancaGame()

        game.addRound(teamOne: -20, teamTwo: 50)

        XCTAssertEqual(game.teamOneTotal, -20)
        XCTAssertEqual(game.teamTwoTotal, 50)
    }

    func testRemovingARoundUpdatesTotals() {
        var game = TrancaGame()
        game.addRound(teamOne: 100, teamTwo: 90)
        game.addRound(teamOne: 30, teamTwo: 60)
        game.addRound(teamOne: 5, teamTwo: 10)

        game.removeRound(at: 1)

        XCTAssertEqual(game.rounds.map(\.teamOne), [100, 5])
        XCTAssertEqual(game.teamOneTotal, 105)
        XCTAssertEqual(game.teamTwoTotal, 100)
    }

    func testRemovingAnInvalidIndexKeepsRounds() {
        var game = TrancaGame()
        game.addRound(teamOne: 100, teamTwo: 90)

        game.removeRound(at: 3)

        XCTAssertEqual(game.rounds.count, 1)
    }

    func testResetClearsRoundsAndScores() {
        var game = TrancaGame()
        game.addRound(teamOne: 100, teamTwo: 90)

        game.reset()

        XCTAssertTrue(game.rounds.isEmpty)
        XCTAssertEqual(game.teamOneTotal, 0)
        XCTAssertEqual(game.teamTwoTotal, 0)
    }

    func testScoreInputKeepsDigitsAndLeadingMinus() {
        XCTAssertEqual(ScoreInput.sanitize("-10abc"), "-10")
        XCTAssertEqual(ScoreInput.sanitize("+8"), "8")
        XCTAssertEqual(ScoreInput.sanitize("1.2"), "12")
        XCTAssertEqual(ScoreInput.sanitize("-"), "-")
        XCTAssertEqual(ScoreInput.sanitize("abc"), "")
    }

    func testScoreInputParsesWholeNumbersOnly() {
        XCTAssertEqual(ScoreInput.parse("-15"), -15)
        XCTAssertEqual(ScoreInput.parse("40"), 40)
        XCTAssertNil(ScoreInput.parse("-"))
        XCTAssertNil(ScoreInput.parse(""))
    }

    func testTeamNameFallsBackWhenEmpty() {
        XCTAssertEqual(teamName("  Ana e Bia ", fallback: "Dupla 1"), "Ana e Bia")
        XCTAssertEqual(teamName("   ", fallback: "Dupla 2"), "Dupla 2")
    }
}
