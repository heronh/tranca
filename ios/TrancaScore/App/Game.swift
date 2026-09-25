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

    mutating func removeRound(at index: Int) {
        guard rounds.indices.contains(index) else { return }
        rounds.remove(at: index)
    }

    mutating func reset() {
        rounds.removeAll()
    }
}

enum ScoreInput {
    /// Keeps only digits and a leading minus, so "-10abc" becomes "-10".
    static func sanitize(_ text: String) -> String {
        let negative = text.trimmingCharacters(in: .whitespaces).hasPrefix("-")
        let digits = text.filter(\.isASCIIDigit)
        if digits.isEmpty {
            return negative ? "-" : ""
        }
        return negative ? "-\(digits)" : digits
    }

    static func parse(_ text: String) -> Int? {
        let digits = text.hasPrefix("-") ? text.dropFirst() : Substring(text)
        guard !digits.isEmpty, digits.allSatisfy(\.isASCIIDigit) else { return nil }
        return Int(text)
    }
}

func teamName(_ name: String, fallback: String) -> String {
    let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
    return trimmed.isEmpty ? fallback : trimmed
}

private extension Character {
    var isASCIIDigit: Bool {
        isASCII && isNumber
    }
}
