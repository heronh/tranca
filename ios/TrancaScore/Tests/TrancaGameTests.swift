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

    func testRemovingLastRoundUpdatesTotals() {
        var game = TrancaGame()
        game.addRound(teamOne: 100, teamTwo: 90)
        game.addRound(teamOne: 30, teamTwo: 60)

        game.removeLastRound()

        XCTAssertEqual(game.rounds.count, 1)
        XCTAssertEqual(game.teamOneTotal, 100)
        XCTAssertEqual(game.teamTwoTotal, 90)
    }

    func testResetClearsRoundsAndScores() {
        var game = TrancaGame()
        game.addRound(teamOne: 100, teamTwo: 90)

        game.reset()

        XCTAssertTrue(game.rounds.isEmpty)
        XCTAssertEqual(game.teamOneTotal, 0)
        XCTAssertEqual(game.teamTwoTotal, 0)
    }
}
