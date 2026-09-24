import Foundation

struct RoundScore: Identifiable, Equatable {
    let id: UUID
    let teamOne: Int
    let teamTwo: Int

    init(id: UUID = UUID(), teamOne: Int, teamTwo: Int) {
        self.id = id
        self.teamOne = teamOne
        self.teamTwo = teamTwo
    }
}

struct TrancaGame: Equatable {
    var rounds: [RoundScore] = []

    var teamOneTotal: Int {
        rounds.reduce(0) { $0 + $1.teamOne }
    }

    var teamTwoTotal: Int {
        rounds.reduce(0) { $0 + $1.teamTwo }
    }

    mutating func addRound(teamOne: Int, teamTwo: Int) {
        rounds.append(RoundScore(teamOne: teamOne, teamTwo: teamTwo))
    }

    mutating func removeLastRound() {
        guard !rounds.isEmpty else { return }
        rounds.removeLast()
    }

    mutating func reset() {
        rounds.removeAll()
    }
}
