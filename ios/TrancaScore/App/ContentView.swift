import SwiftUI

struct ContentView: View {
    @State private var game = TrancaGame()
    @State private var teamOneName = ""
    @State private var teamTwoName = ""
    @State private var teamOneDraft = ""
    @State private var teamTwoDraft = ""
    @State private var teamOneScore = ""
    @State private var teamTwoScore = ""
    @State private var pendingName: PendingName?
    @State private var pendingDelete: Int?
    @State private var showingClearConfirmation = false
    @State private var showingResetConfirmation = false
    @FocusState private var focusedField: Field?

    private enum Team {
        case one
        case two

        var label: String {
            self == .one ? "Dupla 1" : "Dupla 2"
        }
    }

    private enum Field: Hashable {
        case teamOneName
        case teamTwoName
        case teamOneScore
        case teamTwoScore
    }

    private struct PendingName {
        let team: Team
        let current: String
        let next: String
    }

    private var parsedTeamOneScore: Int? {
        ScoreInput.parse(teamOneScore)
    }

    private var parsedTeamTwoScore: Int? {
        ScoreInput.parse(teamTwoScore)
    }

    private var teamOneTitle: String {
        teamName(teamOneName, fallback: Team.one.label)
    }

    private var teamTwoTitle: String {
        teamName(teamTwoName, fallback: Team.two.label)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 8) {
                    header

                    HStack(alignment: .top, spacing: 8) {
                        TeamCard(
                            placeholder: Team.one.label,
                            name: $teamOneDraft,
                            score: $teamOneScore,
                            nameFocus: $focusedField,
                            nameField: .teamOneName,
                            scoreField: .teamOneScore,
                            onSubmitScore: addRound
                        )

                        TeamCard(
                            placeholder: Team.two.label,
                            name: $teamTwoDraft,
                            score: $teamTwoScore,
                            nameFocus: $focusedField,
                            nameField: .teamTwoName,
                            scoreField: .teamTwoScore,
                            onSubmitScore: addRound
                        )
                    }

                    addButton

                    historyCard
                }
                .padding(.horizontal, 12)
                .padding(.bottom, 8)
            }
            .background(Color(.systemGroupedBackground))
            .safeAreaInset(edge: .bottom) {
                clearButton
            }
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
            .onChange(of: focusedField) { previous, _ in
                switch previous {
                case .teamOneName:
                    requestNameChange(for: .one)
                case .teamTwoName:
                    requestNameChange(for: .two)
                default:
                    break
                }
            }
            .onChange(of: teamOneScore) { _, value in
                let cleaned = ScoreInput.sanitize(value)
                if cleaned != value { teamOneScore = cleaned }
            }
            .onChange(of: teamTwoScore) { _, value in
                let cleaned = ScoreInput.sanitize(value)
                if cleaned != value { teamTwoScore = cleaned }
            }
            .confirmationDialog(
                pendingName.map { "Alterar o nome da \($0.team.label)?" } ?? "",
                isPresented: showingNameConfirmation,
                titleVisibility: .visible,
                presenting: pendingName
            ) { _ in
                Button("Alterar", action: confirmNameChange)
                Button("Cancelar", role: .cancel, action: cancelNameChange)
            } message: { pending in
                if pending.next.isEmpty {
                    Text("O nome \"\(pending.current)\" será removido.")
                } else {
                    Text("\(pending.current) passará a ser \(pending.next).")
                }
            }
            .confirmationDialog(
                pendingDelete.map { "Apagar a rodada \($0 + 1)?" } ?? "",
                isPresented: showingDeleteConfirmation,
                titleVisibility: .visible,
                presenting: pendingDelete
            ) { index in
                Button("Apagar", role: .destructive) {
                    game.removeRound(at: index)
                    pendingDelete = nil
                }
                Button("Cancelar", role: .cancel) {
                    pendingDelete = nil
                }
            } message: { _ in
                Text("Os totais serão recalculados.")
            }
            .confirmationDialog(
                "Limpar o placar?",
                isPresented: $showingClearConfirmation,
                titleVisibility: .visible
            ) {
                Button("Limpar placar", role: .destructive, action: clearScoreboard)
                Button("Cancelar", role: .cancel) {}
            } message: {
                Text("Os nomes das duplas serão mantidos.")
            }
            .confirmationDialog(
                "Começar uma nova partida?",
                isPresented: $showingResetConfirmation,
                titleVisibility: .visible
            ) {
                Button("Zerar placar", role: .destructive, action: clearScoreboard)
                Button("Cancelar", role: .cancel) {}
            } message: {
                Text("Os nomes das duplas serão mantidos.")
            }
        }
    }

    private var header: some View {
        VStack(spacing: 2) {
            HStack(spacing: 6) {
                Image(systemName: "suit.club.fill")
                    .font(.title3)
                    .foregroundStyle(.green)
                    .accessibilityHidden(true)
                Text("Tranca")
                    .font(.title2.bold())
            }
            Text("Anote os pontos de cada rodada e deixe a soma com a gente.")
                .font(.footnote)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.bottom, 8)
            Rectangle()
                .fill(Color(.separator))
                .frame(height: 2)
        }
        .padding(.horizontal, 8)
    }

    private var addButton: some View {
        Button(action: addRound) {
            Label("Adicionar rodada", systemImage: "checkmark.circle.fill")
                .font(.subheadline.weight(.semibold))
                .frame(maxWidth: .infinity)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.large)
        .tint(.green)
        .disabled(parsedTeamOneScore == nil || parsedTeamTwoScore == nil)
    }

    private var historyCard: some View {
        VStack(spacing: 0) {
            HStack {
                Text("Rodadas")
                    .font(.subheadline.bold())
                Spacer()
                TotalText(value: game.teamOneTotal, color: .orange, label: teamOneTitle)
                TotalText(value: game.teamTwoTotal, color: .blue, label: teamTwoTitle)
                Color.clear
                    .frame(width: 24, height: 1)
            }
            .padding(.bottom, 8)

            HStack {
                Text("Rodada")
                Spacer()
                Text(teamOneTitle)
                    .lineLimit(1)
                    .frame(width: 86, alignment: .trailing)
                Text(teamTwoTitle)
                    .lineLimit(1)
                    .frame(width: 86, alignment: .trailing)
                Color.clear
                    .frame(width: 24, height: 1)
            }
            .font(.caption2)
            .foregroundStyle(.secondary)
            .padding(.bottom, 4)

            ForEach(Array(game.rounds.enumerated()), id: \.element.id) { index, round in
                Divider()
                HStack {
                    Text("\(index + 1)")
                        .foregroundStyle(.secondary)
                    Spacer()
                    Text(round.teamOne, format: .number)
                        .frame(width: 86, alignment: .trailing)
                    Text(round.teamTwo, format: .number)
                        .frame(width: 86, alignment: .trailing)
                    Button {
                        pendingDelete = index
                    } label: {
                        Image(systemName: "xmark")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                            .frame(width: 24, height: 24, alignment: .trailing)
                    }
                    .buttonStyle(.plain)
                    .accessibilityLabel("Apagar rodada \(index + 1)")
                }
                .font(.subheadline.monospacedDigit())
                .padding(.vertical, 6)
            }
        }
        .cardStyle()
    }

    private var clearButton: some View {
        Button(role: .destructive) {
            showingClearConfirmation = true
        } label: {
            Text("Limpar placar")
                .font(.subheadline.weight(.semibold))
                .frame(maxWidth: .infinity)
        }
        .buttonStyle(.bordered)
        .controlSize(.large)
        .disabled(game.rounds.isEmpty)
        .padding(.horizontal, 12)
        .padding(.top, 8)
        .padding(.bottom, 4)
        .background(Color(.systemGroupedBackground))
    }

    private var showingNameConfirmation: Binding<Bool> {
        Binding(
            get: { pendingName != nil },
            set: { if !$0 { cancelNameChange() } }
        )
    }

    private var showingDeleteConfirmation: Binding<Bool> {
        Binding(
            get: { pendingDelete != nil },
            set: { if !$0 { pendingDelete = nil } }
        )
    }

    private func requestNameChange(for team: Team) {
        let draft = (team == .one ? teamOneDraft : teamTwoDraft)
            .trimmingCharacters(in: .whitespacesAndNewlines)
        let current = team == .one ? teamOneName : teamTwoName
        setDraft(draft, for: team)
        guard draft != current else { return }

        if current.isEmpty {
            setName(draft, for: team)
        } else {
            pendingName = PendingName(team: team, current: current, next: draft)
        }
    }

    private func confirmNameChange() {
        guard let pending = pendingName else { return }
        pendingName = nil
        setName(pending.next, for: pending.team)
    }

    private func cancelNameChange() {
        guard let pending = pendingName else { return }
        pendingName = nil
        setDraft(pending.current, for: pending.team)
    }

    private func setName(_ name: String, for team: Team) {
        switch team {
        case .one:
            teamOneName = name
            teamOneDraft = name
        case .two:
            teamTwoName = name
            teamTwoDraft = name
        }
    }

    private func setDraft(_ draft: String, for team: Team) {
        switch team {
        case .one: teamOneDraft = draft
        case .two: teamTwoDraft = draft
        }
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

    private func clearScoreboard() {
        game.reset()
        pendingDelete = nil
        clearScoreFields()
    }

    private func clearScoreFields() {
        teamOneScore = ""
        teamTwoScore = ""
    }
}

private struct TeamCard<Field: Hashable>: View {
    let placeholder: String
    @Binding var name: String
    @Binding var score: String
    var nameFocus: FocusState<Field?>.Binding
    let nameField: Field
    let scoreField: Field
    let onSubmitScore: () -> Void

    var body: some View {
        VStack(spacing: 8) {
            TextField(placeholder, text: $name)
                .textContentType(.name)
                .submitLabel(.done)
                .focused(nameFocus, equals: nameField)
                .onSubmit { nameFocus.wrappedValue = nil }
                .fieldStyle()
                .accessibilityLabel("Nomes da \(placeholder.lowercased())")

            TextField("0", text: $score)
                .keyboardType(.numbersAndPunctuation)
                .submitLabel(.done)
                .focused(nameFocus, equals: scoreField)
                .onSubmit(onSubmitScore)
                .font(.title3.bold())
                .monospacedDigit()
                .multilineTextAlignment(.trailing)
                .fieldStyle()
                .accessibilityLabel("Pontos da \(placeholder.lowercased())")
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }
}

private struct TotalText: View {
    let value: Int
    let color: Color
    let label: String

    var body: some View {
        Text(value, format: .number)
            .font(.title3.bold())
            .monospacedDigit()
            .foregroundStyle(color)
            .contentTransition(.numericText())
            .frame(width: 86, alignment: .trailing)
            .accessibilityLabel("Total de \(label)")
    }
}

private extension View {
    func cardStyle() -> some View {
        padding(10)
            .background(Color(.secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }

    func fieldStyle() -> some View {
        padding(.horizontal, 10)
            .frame(minHeight: 32)
            .background(Color(.tertiarySystemFill))
            .clipShape(RoundedRectangle(cornerRadius: 6, style: .continuous))
    }
}

#Preview {
    ContentView()
}
