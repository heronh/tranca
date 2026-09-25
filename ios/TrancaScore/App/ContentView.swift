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

    private var canAdd: Bool {
        ScoreInput.parse(teamOneScore) != nil && ScoreInput.parse(teamTwoScore) != nil
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
                LazyVStack(spacing: 12) {
                    HeroView(canReset: !game.rounds.isEmpty) {
                        showingResetConfirmation = true
                    }

                    HStack(alignment: .top, spacing: 12) {
                        TeamCard(
                            team: .one,
                            name: $teamOneDraft,
                            score: $teamOneScore,
                            focus: $focusedField,
                            nameField: .teamOneName,
                            scoreField: .teamOneScore,
                            onSubmitScore: addRound
                        )

                        TeamCard(
                            team: .two,
                            name: $teamTwoDraft,
                            score: $teamTwoScore,
                            focus: $focusedField,
                            nameField: .teamTwoName,
                            scoreField: .teamTwoScore,
                            onSubmitScore: addRound
                        )
                    }

                    addButton

                    HStack(spacing: 12) {
                        TotalTile(
                            team: .one,
                            name: teamOneTitle,
                            total: game.teamOneTotal,
                            leading: !game.rounds.isEmpty && game.teamOneTotal > game.teamTwoTotal
                        )
                        TotalTile(
                            team: .two,
                            name: teamTwoTitle,
                            total: game.teamTwoTotal,
                            leading: !game.rounds.isEmpty && game.teamTwoTotal > game.teamOneTotal
                        )
                    }

                    RoundsHeader(
                        count: game.rounds.count,
                        teamOneName: teamOneTitle,
                        teamTwoName: teamTwoTitle
                    )

                    if game.rounds.isEmpty {
                        EmptyRoundsView()
                    } else {
                        ForEach(Array(game.rounds.enumerated()), id: \.element.id) { index, round in
                            RoundRow(number: index + 1, round: round) {
                                pendingDelete = index
                            }
                            .transition(.opacity.combined(with: .move(edge: .top)))
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 8)
                .animation(.snappy, value: game.rounds)
            }
            .scrollDismissesKeyboard(.interactively)
            .background(Palette.background.ignoresSafeArea())
            .safeAreaInset(edge: .bottom) {
                clearButton
            }
            .toolbar(.hidden, for: .navigationBar)
            .toolbar {
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

    private var addButton: some View {
        Button(action: addRound) {
            Label("Adicionar rodada", systemImage: "plus")
                .font(.body.weight(.semibold))
                .frame(maxWidth: .infinity, minHeight: 36)
        }
        .buttonStyle(.borderedProminent)
        .buttonBorderShape(.capsule)
        .controlSize(.large)
        .tint(Palette.green)
        .disabled(!canAdd)
    }

    private var clearButton: some View {
        Button(role: .destructive) {
            showingClearConfirmation = true
        } label: {
            Label("Limpar placar", systemImage: "trash")
                .font(.body.weight(.semibold))
                .frame(maxWidth: .infinity, minHeight: 28)
        }
        .buttonStyle(.bordered)
        .buttonBorderShape(.capsule)
        .controlSize(.large)
        .tint(.red)
        .disabled(game.rounds.isEmpty)
        .padding(.horizontal, 16)
        .padding(.top, 8)
        .padding(.bottom, 4)
        .background(Palette.background)
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
        guard let firstScore = ScoreInput.parse(teamOneScore),
              let secondScore = ScoreInput.parse(teamTwoScore) else {
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

private enum Team {
    case one
    case two

    var label: String {
        self == .one ? "Dupla 1" : "Dupla 2"
    }
}

private enum Palette {
    static let green = Color(red: 0.07, green: 0.54, blue: 0.31)
    static let deepGreen = Color(red: 0.04, green: 0.37, blue: 0.22)
    static let background = Color(.systemGroupedBackground)
    static let card = Color(.secondarySystemGroupedBackground)
    static let field = Color(.tertiarySystemFill)
}

private struct TeamStyle {
    let accent: Color
    let container: Color
    let onContainer: Color

    static func of(_ team: Team, in scheme: ColorScheme) -> TeamStyle {
        let dark = scheme == .dark
        switch team {
        case .one:
            return TeamStyle(
                accent: dark ? Color(red: 1.0, green: 0.72, blue: 0.48) : Color(red: 0.91, green: 0.44, blue: 0.04),
                container: dark ? Color(red: 0.31, green: 0.18, blue: 0.06) : Color(red: 1.0, green: 0.92, blue: 0.86),
                onContainer: dark ? Color(red: 1.0, green: 0.86, blue: 0.76) : Color(red: 0.35, green: 0.16, blue: 0.0)
            )
        case .two:
            return TeamStyle(
                accent: dark ? Color(red: 0.61, green: 0.76, blue: 1.0) : Color(red: 0.10, green: 0.44, blue: 0.88),
                container: dark ? Color(red: 0.09, green: 0.20, blue: 0.37) : Color(red: 0.88, green: 0.92, blue: 0.99),
                onContainer: dark ? Color(red: 0.85, green: 0.90, blue: 1.0) : Color(red: 0.04, green: 0.23, blue: 0.51)
            )
        }
    }
}

private struct HeroView: View {
    let canReset: Bool
    let onReset: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "suit.club.fill")
                .font(.title3)
                .foregroundStyle(.white)
                .frame(width: 44, height: 44)
                .background(.white.opacity(0.18), in: Circle())
                .accessibilityHidden(true)

            VStack(alignment: .leading, spacing: 2) {
                Text("Tranca")
                    .font(.title2.bold())
                    .foregroundStyle(.white)
                Text("Anote os pontos de cada rodada e deixe a soma com a gente.")
                    .font(.footnote)
                    .foregroundStyle(.white.opacity(0.82))
                    .fixedSize(horizontal: false, vertical: true)
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            Button(action: onReset) {
                Image(systemName: "arrow.counterclockwise")
                    .font(.body.weight(.semibold))
                    .foregroundStyle(.white.opacity(canReset ? 1 : 0.4))
                    .frame(width: 40, height: 40)
                    .background(.white.opacity(canReset ? 0.18 : 0.08), in: Circle())
            }
            .buttonStyle(.plain)
            .disabled(!canReset)
            .accessibilityLabel("Nova partida")
        }
        .padding(.leading, 16)
        .padding(.trailing, 10)
        .padding(.vertical, 14)
        .background(
            LinearGradient(
                colors: [Palette.green, Palette.deepGreen],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ),
            in: RoundedRectangle(cornerRadius: 24, style: .continuous)
        )
    }
}

private struct TeamCard<Field: Hashable>: View {
    @Environment(\.colorScheme) private var colorScheme
    let team: Team
    @Binding var name: String
    @Binding var score: String
    var focus: FocusState<Field?>.Binding
    let nameField: Field
    let scoreField: Field
    let onSubmitScore: () -> Void

    var body: some View {
        let style = TeamStyle.of(team, in: colorScheme)

        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 6) {
                Circle()
                    .fill(style.accent)
                    .frame(width: 8, height: 8)
                Text(team.label.uppercased())
                    .font(.caption2.bold())
                    .tracking(0.8)
                    .foregroundStyle(style.accent)
            }

            TextField("Nomes", text: $name)
                .textContentType(.name)
                .submitLabel(.done)
                .focused(focus, equals: nameField)
                .onSubmit { focus.wrappedValue = nil }
                .padding(.horizontal, 12)
                .frame(minHeight: 48)
                .background(Palette.field, in: RoundedRectangle(cornerRadius: 12, style: .continuous))
                .accessibilityLabel("Nomes da \(team.label.lowercased())")

            TextField("0", text: $score)
                .keyboardType(.numbersAndPunctuation)
                .submitLabel(.done)
                .focused(focus, equals: scoreField)
                .onSubmit(onSubmitScore)
                .font(.system(size: 22, weight: .bold, design: .rounded))
                .monospacedDigit()
                .foregroundStyle(style.onContainer)
                .multilineTextAlignment(.trailing)
                .padding(.horizontal, 12)
                .frame(minHeight: 48)
                .background(style.container, in: RoundedRectangle(cornerRadius: 12, style: .continuous))
                .accessibilityLabel("Pontos da \(team.label.lowercased())")
        }
        .padding(12)
        .frame(maxWidth: .infinity)
        .background(Palette.card, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
        .shadow(color: .black.opacity(0.06), radius: 3, y: 1)
    }
}

private struct TotalTile: View {
    @Environment(\.colorScheme) private var colorScheme
    let team: Team
    let name: String
    let total: Int
    let leading: Bool

    var body: some View {
        let style = TeamStyle.of(team, in: colorScheme)

        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text(name)
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(style.onContainer)
                    .lineLimit(1)
                Spacer(minLength: 4)
                if leading {
                    Image(systemName: "trophy.fill")
                        .font(.footnote)
                        .foregroundStyle(style.accent)
                        .transition(.scale.combined(with: .opacity))
                }
            }
            Text(total, format: .number)
                .font(.system(size: 34, weight: .bold, design: .rounded))
                .monospacedDigit()
                .foregroundStyle(style.accent)
                .contentTransition(.numericText())
                .lineLimit(1)
                .minimumScaleFactor(0.6)
            Text(leading ? "Liderando" : "Total")
                .font(.caption2)
                .foregroundStyle(style.onContainer.opacity(0.7))
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(style.container, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
        .animation(.snappy, value: total)
        .animation(.snappy, value: leading)
        .accessibilityElement(children: .combine)
    }
}

private struct RoundsHeader: View {
    @Environment(\.colorScheme) private var colorScheme
    let count: Int
    let teamOneName: String
    let teamTwoName: String

    var body: some View {
        VStack(spacing: 10) {
            HStack(spacing: 6) {
                Image(systemName: "list.number")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
                Text("Rodadas")
                    .font(.subheadline.bold())
                if count > 0 {
                    Text("\(count)")
                        .font(.caption.weight(.medium))
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 1)
                        .background(Palette.field, in: Capsule())
                }
                Spacer()
            }

            if count > 0 {
                HStack(spacing: 0) {
                    Spacer()
                    columnLabel(teamOneName, color: TeamStyle.of(.one, in: colorScheme).accent)
                    columnLabel(teamTwoName, color: TeamStyle.of(.two, in: colorScheme).accent)
                    Color.clear.frame(width: RoundRow.deleteWidth, height: 1)
                }
            }
        }
        .padding(.top, 4)
    }

    private func columnLabel(_ name: String, color: Color) -> some View {
        Text(name)
            .font(.caption2.weight(.semibold))
            .foregroundStyle(color)
            .lineLimit(1)
            .frame(width: RoundRow.scoreWidth, alignment: .trailing)
    }
}

private struct RoundRow: View {
    static let scoreWidth: CGFloat = 80
    static let deleteWidth: CGFloat = 40

    let number: Int
    let round: RoundScore
    let onDelete: () -> Void

    var body: some View {
        HStack(spacing: 0) {
            Text("\(number)")
                .font(.caption.weight(.semibold))
                .foregroundStyle(.secondary)
                .frame(width: 28, height: 28)
                .background(Palette.field, in: Circle())
            Spacer()
            scoreText(round.teamOne)
            scoreText(round.teamTwo)
            Button(action: onDelete) {
                Image(systemName: "xmark")
                    .font(.footnote.weight(.semibold))
                    .foregroundStyle(.secondary)
                    .frame(width: Self.deleteWidth, height: 36)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Apagar rodada \(number)")
        }
        .padding(.leading, 10)
        .padding(.vertical, 4)
        .background(Palette.card, in: RoundedRectangle(cornerRadius: 16, style: .continuous))
    }

    private func scoreText(_ score: Int) -> some View {
        Text(score, format: .number)
            .font(.body.weight(.medium))
            .monospacedDigit()
            .foregroundStyle(score < 0 ? Color.red : Color.primary)
            .lineLimit(1)
            .frame(width: Self.scoreWidth, alignment: .trailing)
    }
}

private struct EmptyRoundsView: View {
    var body: some View {
        VStack(spacing: 4) {
            Text("Nenhuma rodada ainda")
                .font(.subheadline.weight(.semibold))
            Text("Informe os pontos das duas duplas e toque em Adicionar rodada.")
                .font(.footnote)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding(.vertical, 24)
        .padding(.horizontal, 16)
        .frame(maxWidth: .infinity)
        .background(Palette.card, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
    }
}

#Preview {
    ContentView()
}
