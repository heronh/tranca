import SwiftUI

struct ContentView: View {
    @State private var game = TrancaGame()
    @State private var playerOne = ""
    @State private var playerTwo = ""
    @State private var playerThree = ""
    @State private var playerFour = ""
    @State private var teamOneScore = ""
    @State private var teamTwoScore = ""
    @State private var showingSummary = false
    @State private var showingResetConfirmation = false
    @FocusState private var focusedField: ScoreField?

    private enum ScoreField: Hashable {
        case teamOne
        case teamTwo
    }

    private var parsedTeamOneScore: Int? {
        Int(teamOneScore.trimmingCharacters(in: .whitespaces))
    }

    private var parsedTeamTwoScore: Int? {
        Int(teamTwoScore.trimmingCharacters(in: .whitespaces))
    }

    private var teamOneName: String {
        teamName(playerOne, playerTwo, fallback: "Dupla 1")
    }

    private var teamTwoName: String {
        teamName(playerThree, playerFour, fallback: "Dupla 2")
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    header

                    HStack(alignment: .top, spacing: 12) {
                        TeamCard(
                            title: "Dupla 1",
                            color: .orange,
                            firstPlayer: $playerOne,
                            secondPlayer: $playerTwo,
                            total: game.teamOneTotal
                        )

                        TeamCard(
                            title: "Dupla 2",
                            color: .blue,
                            firstPlayer: $playerThree,
                            secondPlayer: $playerFour,
                            total: game.teamTwoTotal
                        )
                    }

                    newRoundCard

                    if !game.rounds.isEmpty {
                        historyCard
                        finishButton
                    }
                }
                .padding()
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("Placar")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Nova partida", systemImage: "arrow.counterclockwise") {
                        showingResetConfirmation = true
                    }
                    .disabled(game.rounds.isEmpty)
                }

                ToolbarItemGroup(placement: .keyboard) {
                    Spacer()
                    Button("OK") {
                        focusedField = nil
                    }
                }
            }
            .confirmationDialog(
                "Começar uma nova partida?",
                isPresented: $showingResetConfirmation,
                titleVisibility: .visible
            ) {
                Button("Zerar placar", role: .destructive) {
                    game.reset()
                    clearScoreFields()
                }
                Button("Cancelar", role: .cancel) {}
            } message: {
                Text("Os nomes das duplas serão mantidos.")
            }
            .sheet(isPresented: $showingSummary) {
                MatchSummaryView(
                    teamOneName: teamOneName,
                    teamTwoName: teamTwoName,
                    teamOneTotal: game.teamOneTotal,
                    teamTwoTotal: game.teamTwoTotal,
                    roundCount: game.rounds.count
                )
            }
        }
    }

    private var header: some View {
        VStack(spacing: 6) {
            Image(systemName: "suit.club.fill")
                .font(.system(size: 36))
                .foregroundStyle(.green)
                .accessibilityHidden(true)
            Text("Tranca")
                .font(.largeTitle.bold())
            Text("Anote os pontos de cada rodada e deixe a soma com a gente.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding(.vertical, 8)
    }

    private var newRoundCard: some View {
        VStack(alignment: .leading, spacing: 16) {
            Label("Nova rodada", systemImage: "plus.circle.fill")
                .font(.headline)

            HStack(spacing: 12) {
                ScoreFieldView(
                    title: teamOneName,
                    color: .orange,
                    text: $teamOneScore
                )
                .focused($focusedField, equals: .teamOne)

                ScoreFieldView(
                    title: teamTwoName,
                    color: .blue,
                    text: $teamTwoScore
                )
                .focused($focusedField, equals: .teamTwo)
            }

            Button(action: addRound) {
                Label("Adicionar rodada", systemImage: "checkmark.circle.fill")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 4)
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
            .tint(.green)
            .disabled(parsedTeamOneScore == nil || parsedTeamTwoScore == nil)
        }
        .cardStyle()
    }

    private var historyCard: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Label("Rodadas", systemImage: "list.number")
                    .font(.headline)
                Spacer()
                Button("Desfazer", systemImage: "arrow.uturn.backward") {
                    game.removeLastRound()
                }
                .font(.subheadline)
            }
            .padding(.bottom, 12)

            HStack {
                Text("Rodada")
                Spacer()
                Text(teamOneName)
                    .frame(width: 86, alignment: .trailing)
                Text(teamTwoName)
                    .frame(width: 86, alignment: .trailing)
            }
            .font(.caption)
            .foregroundStyle(.secondary)

            Divider()
                .padding(.top, 8)

            ForEach(Array(game.rounds.enumerated()), id: \.element.id) { index, round in
                HStack {
                    Text("\(index + 1)")
                        .foregroundStyle(.secondary)
                    Spacer()
                    Text(round.teamOne, format: .number)
                        .frame(width: 86, alignment: .trailing)
                    Text(round.teamTwo, format: .number)
                        .frame(width: 86, alignment: .trailing)
                }
                .font(.body.monospacedDigit())
                .padding(.vertical, 10)

                if round.id != game.rounds.last?.id {
                    Divider()
                }
            }
        }
        .cardStyle()
    }

    private var finishButton: some View {
        Button {
            showingSummary = true
        } label: {
            Label("Ver resultado final", systemImage: "flag.checkered")
                .frame(maxWidth: .infinity)
                .padding(.vertical, 6)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.large)
        .tint(.primary)
    }

    private func addRound() {
        guard let firstScore = parsedTeamOneScore,
              let secondScore = parsedTeamTwoScore else {
            return
        }

        game.addRound(teamOne: firstScore, teamTwo: secondScore)
        clearScoreFields()
        focusedField = nil
    }

    private func clearScoreFields() {
        teamOneScore = ""
        teamTwoScore = ""
    }

    private func teamName(_ first: String, _ second: String, fallback: String) -> String {
        let names = [first, second]
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
            .filter { !$0.isEmpty }
        return names.isEmpty ? fallback : names.joined(separator: " & ")
    }
}

private struct TeamCard: View {
    let title: String
    let color: Color
    @Binding var firstPlayer: String
    @Binding var secondPlayer: String
    let total: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(color)

            TextField("Jogador 1", text: $firstPlayer)
                .textContentType(.name)
                .submitLabel(.next)
                .textFieldStyle(.roundedBorder)

            TextField("Jogador 2", text: $secondPlayer)
                .textContentType(.name)
                .submitLabel(.done)
                .textFieldStyle(.roundedBorder)

            Divider()

            Text("TOTAL")
                .font(.caption2.bold())
                .foregroundStyle(.secondary)
            Text(total, format: .number)
                .font(.system(size: 32, weight: .bold, design: .rounded))
                .monospacedDigit()
                .foregroundStyle(color)
                .contentTransition(.numericText())
                .accessibilityLabel("Total da \(title)")
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .cardStyle()
    }
}

private struct ScoreFieldView: View {
    let title: String
    let color: Color
    @Binding var text: String

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption.bold())
                .foregroundStyle(color)
                .lineLimit(1)
            TextField("0", text: $text)
                .keyboardType(.numbersAndPunctuation)
                .font(.title2.bold())
                .monospacedDigit()
                .multilineTextAlignment(.trailing)
                .textFieldStyle(.roundedBorder)
                .accessibilityLabel("Pontos de \(title)")
        }
        .frame(maxWidth: .infinity)
    }
}

private struct MatchSummaryView: View {
    @Environment(\.dismiss) private var dismiss

    let teamOneName: String
    let teamTwoName: String
    let teamOneTotal: Int
    let teamTwoTotal: Int
    let roundCount: Int

    private var resultTitle: String {
        if teamOneTotal == teamTwoTotal {
            return "Empate!"
        }
        return "\(teamOneTotal > teamTwoTotal ? teamOneName : teamTwoName) venceu!"
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 28) {
                Spacer()

                Image(systemName: "trophy.fill")
                    .font(.system(size: 54))
                    .foregroundStyle(.yellow)
                    .accessibilityHidden(true)

                VStack(spacing: 8) {
                    Text(resultTitle)
                        .font(.title.bold())
                        .multilineTextAlignment(.center)
                    Text("\(roundCount) \(roundCount == 1 ? "rodada" : "rodadas")")
                        .foregroundStyle(.secondary)
                }

                HStack(spacing: 16) {
                    SummaryScore(name: teamOneName, score: teamOneTotal, color: .orange)
                    SummaryScore(name: teamTwoName, score: teamTwoTotal, color: .blue)
                }

                Spacer()

                Button("Voltar ao placar") {
                    dismiss()
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
                .frame(maxWidth: .infinity)
            }
            .padding()
            .navigationTitle("Resultado final")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

private struct SummaryScore: View {
    let name: String
    let score: Int
    let color: Color

    var body: some View {
        VStack(spacing: 10) {
            Text(name)
                .font(.headline)
                .lineLimit(2)
                .multilineTextAlignment(.center)
            Text(score, format: .number)
                .font(.system(size: 40, weight: .bold, design: .rounded))
                .monospacedDigit()
                .foregroundStyle(color)
        }
        .frame(maxWidth: .infinity, minHeight: 130)
        .cardStyle()
    }
}

private extension View {
    func cardStyle() -> some View {
        padding(16)
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
    }
}

#Preview {
    ContentView()
}
