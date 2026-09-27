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
    /// Hyphen, minus sign, and the dashes keyboards insert in place of "-".
    private static let minusSigns: Set<Character> = ["-", "−", "–", "—", "‐", "‑"]

    /// Keeps digits and one leading sign. The last "+" or minus in the text wins,
    /// so both "-10" and "10-" are negative, and "+" forces the number positive.
    static func sanitize(_ text: String) -> String {
        let trimmed = text.trimmingCharacters(in: .whitespaces)
        var negative = false
        for character in trimmed {
            if minusSigns.contains(character) {
                negative = true
            } else if character == "+" {
                negative = false
            }
        }
        let digits = trimmed.filter(\.isASCIIDigit)
        if digits.isEmpty {
            return negative ? "-" : ""
        }
        return negative ? "-\(digits)" : digits
    }

    static func toggleSign(_ text: String) -> String {
        let cleaned = sanitize(text)
        return cleaned.hasPrefix("-") ? String(cleaned.dropFirst()) : "-\(cleaned)"
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
