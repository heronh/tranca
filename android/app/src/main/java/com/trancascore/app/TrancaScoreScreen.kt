package com.trancascore.app

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trancascore.app.ui.theme.TrancaTheme

private val TrancaGameSaver = listSaver<TrancaGame, Int>(
    save = { game ->
        game.rounds.flatMap { round -> listOf(round.teamOne, round.teamTwo) }
    },
    restore = { scores ->
        TrancaGame(
            rounds = scores.chunked(2).map { pair ->
                RoundScore(teamOne = pair[0], teamTwo = pair[1])
            },
        )
    },
)

private const val TEAM_ONE = 1
private const val TEAM_TWO = 2
private val ScoreColumn = 80.dp
private val DeleteColumn = 36.dp
private val Tabular = TextStyle(fontFeatureSettings = "tnum")

private fun teamLabel(team: Int) = if (team == TEAM_ONE) "Dupla 1" else "Dupla 2"

private data class TeamColors(val accent: Color, val container: Color, val onContainer: Color)

@Composable
private fun teamColors(team: Int): TeamColors {
    val scheme = MaterialTheme.colorScheme
    return if (team == TEAM_ONE) {
        TeamColors(scheme.secondary, scheme.secondaryContainer, scheme.onSecondaryContainer)
    } else {
        TeamColors(scheme.tertiary, scheme.tertiaryContainer, scheme.onTertiaryContainer)
    }
}

@Composable
fun TrancaScoreScreen() {
    var game by rememberSaveable(stateSaver = TrancaGameSaver) {
        mutableStateOf(TrancaGame())
    }
    var teamOneName by rememberSaveable { mutableStateOf("") }
    var teamTwoName by rememberSaveable { mutableStateOf("") }
    var teamOneDraft by rememberSaveable { mutableStateOf("") }
    var teamTwoDraft by rememberSaveable { mutableStateOf("") }
    var teamOneScore by rememberSaveable { mutableStateOf("") }
    var teamTwoScore by rememberSaveable { mutableStateOf("") }
    var pendingNameTeam by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingNameNext by rememberSaveable { mutableStateOf("") }
    var pendingDelete by rememberSaveable { mutableStateOf<Int?>(null) }
    var showClearConfirmation by rememberSaveable { mutableStateOf(false) }
    var showResetConfirmation by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val teamOneTitle = teamName(teamOneName, teamLabel(TEAM_ONE))
    val teamTwoTitle = teamName(teamTwoName, teamLabel(TEAM_TWO))
    val canAdd = ScoreInput.parse(teamOneScore) != null && ScoreInput.parse(teamTwoScore) != null
    val hasRounds = game.rounds.isNotEmpty()

    fun currentName(team: Int) = if (team == TEAM_ONE) teamOneName else teamTwoName

    fun setDraft(team: Int, value: String) {
        if (team == TEAM_ONE) teamOneDraft = value else teamTwoDraft = value
    }

    fun setName(team: Int, value: String) {
        if (team == TEAM_ONE) teamOneName = value else teamTwoName = value
        setDraft(team, value)
    }

    fun requestNameChange(team: Int) {
        val next = (if (team == TEAM_ONE) teamOneDraft else teamTwoDraft).trim()
        val current = currentName(team)
        setDraft(team, next)
        if (next == current) return
        if (current.isEmpty()) {
            setName(team, next)
        } else {
            pendingNameTeam = team
            pendingNameNext = next
        }
    }

    fun cancelNameChange() {
        val team = pendingNameTeam ?: return
        setDraft(team, currentName(team))
        pendingNameTeam = null
    }

    fun clearScoreboard() {
        game = game.reset()
        pendingDelete = null
        teamOneScore = ""
        teamTwoScore = ""
    }

    fun addRound() {
        val first = ScoreInput.parse(teamOneScore) ?: return
        val second = ScoreInput.parse(teamTwoScore) ?: return
        game = game.addRound(teamOne = first, teamTwo = second)
        teamOneScore = ""
        teamTwoScore = ""
        focusManager.clearFocus()
    }

    Scaffold(
        bottomBar = {
            OutlinedButton(
                onClick = { showClearConfirmation = true },
                enabled = hasRounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 12.dp)
                    .height(48.dp),
                shape = CircleShape,
                border = BorderStroke(
                    1.dp,
                    if (hasRounds) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Limpar placar", fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = contentPadding.calculateTopPadding() + 8.dp,
                end = 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Hero(
                    canReset = hasRounds,
                    onReset = { showResetConfirmation = true },
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TeamCard(
                        team = TEAM_ONE,
                        name = teamOneDraft,
                        onNameChange = { teamOneDraft = it },
                        onNameCommit = { requestNameChange(TEAM_ONE) },
                        score = teamOneScore,
                        onScoreChange = { teamOneScore = ScoreInput.sanitize(it) },
                        onScoreDone = { addRound() },
                        modifier = Modifier.weight(1f),
                    )
                    TeamCard(
                        team = TEAM_TWO,
                        name = teamTwoDraft,
                        onNameChange = { teamTwoDraft = it },
                        onNameCommit = { requestNameChange(TEAM_TWO) },
                        score = teamTwoScore,
                        onScoreChange = { teamTwoScore = ScoreInput.sanitize(it) },
                        onScoreDone = { addRound() },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Button(
                    onClick = { addRound() },
                    enabled = canAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = CircleShape,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Adicionar rodada", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            item {
                Scoreboard(
                    teamOneName = teamOneTitle,
                    teamTwoName = teamTwoTitle,
                    teamOneTotal = game.teamOneTotal,
                    teamTwoTotal = game.teamTwoTotal,
                    hasRounds = hasRounds,
                )
            }

            item {
                RoundsHeader(
                    count = game.rounds.size,
                    teamOneName = teamOneTitle,
                    teamTwoName = teamTwoTitle,
                )
            }

            if (hasRounds) {
                itemsIndexed(game.rounds) { index, round ->
                    RoundRow(
                        number = index + 1,
                        round = round,
                        onDelete = { pendingDelete = index },
                    )
                }
            } else {
                item {
                    EmptyRounds()
                }
            }
        }
    }

    pendingNameTeam?.let { team ->
        val current = currentName(team)
        AlertDialog(
            onDismissRequest = ::cancelNameChange,
            icon = { Icon(Icons.Outlined.EditNote, contentDescription = null) },
            title = { Text("Alterar o nome da ${teamLabel(team)}?", textAlign = TextAlign.Center) },
            text = {
                Text(
                    if (pendingNameNext.isEmpty()) {
                        "O nome \"$current\" será removido."
                    } else {
                        "$current passará a ser $pendingNameNext."
                    },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        setName(team, pendingNameNext)
                        pendingNameTeam = null
                    },
                ) {
                    Text("Alterar")
                }
            },
            dismissButton = {
                TextButton(onClick = ::cancelNameChange) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            iconContentColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(28.dp),
        )
    }

    pendingDelete?.let { index ->
        ConfirmDialog(
            icon = Icons.Outlined.DeleteOutline,
            title = "Apagar a rodada ${index + 1}?",
            message = "Os totais serão recalculados.",
            confirmLabel = "Apagar",
            onConfirm = {
                game = game.removeRound(index)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null },
        )
    }

    if (showClearConfirmation) {
        ConfirmDialog(
            icon = Icons.Outlined.DeleteOutline,
            title = "Limpar o placar?",
            message = "Os nomes das duplas serão mantidos.",
            confirmLabel = "Limpar placar",
            onConfirm = {
                clearScoreboard()
                showClearConfirmation = false
            },
            onDismiss = { showClearConfirmation = false },
        )
    }

    if (showResetConfirmation) {
        ConfirmDialog(
            icon = Icons.Outlined.RestartAlt,
            title = "Começar uma nova partida?",
            message = "Os nomes das duplas serão mantidos.",
            confirmLabel = "Zerar placar",
            onConfirm = {
                clearScoreboard()
                showResetConfirmation = false
            },
            onDismiss = { showResetConfirmation = false },
        )
    }
}

@Composable
private fun Hero(canReset: Boolean, onReset: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(primary, Color(0xFF0B5E37)),
                ),
            )
            .padding(start = 16.dp, top = 14.dp, end = 8.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "\u2663\uFE0E", color = Color.White, fontSize = 24.sp)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = "Tranca",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Anote os pontos de cada rodada e deixe a soma com a gente.",
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(
            onClick = onReset,
            enabled = canReset,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White.copy(alpha = 0.18f),
                contentColor = Color.White,
                disabledContainerColor = Color.White.copy(alpha = 0.08f),
                disabledContentColor = Color.White.copy(alpha = 0.4f),
            ),
        ) {
            Icon(Icons.Outlined.Refresh, contentDescription = "Nova partida")
        }
    }
}

@Composable
private fun TeamCard(
    team: Int,
    name: String,
    onNameChange: (String) -> Unit,
    onNameCommit: () -> Unit,
    score: String,
    onScoreChange: (String) -> Unit,
    onScoreDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val colors = teamColors(team)
    val label = teamLabel(team)
    var nameFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors.accent),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label.uppercase(),
                    color = colors.accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                )
            }
            TextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        if (nameFocused && !state.isFocused) onNameCommit()
                        nameFocused = state.isFocused
                    }
                    .semantics { contentDescription = "Nomes da ${label.lowercase()}" },
                placeholder = { Text("Nomes", maxLines = 1) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors(MaterialTheme.colorScheme.surfaceContainer, colors.accent),
            )
            TextField(
                value = score,
                onValueChange = onScoreChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Pontos da ${label.lowercase()}" },
                placeholder = {
                    Text(
                        "0",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onScoreDone() }),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = Tabular.copy(
                    textAlign = TextAlign.End,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onContainer,
                ),
                colors = fieldColors(colors.container, colors.accent),
            )
        }
    }
}

@Composable
private fun fieldColors(container: Color, accent: Color) = TextFieldDefaults.colors(
    focusedContainerColor = container,
    unfocusedContainerColor = container,
    disabledContainerColor = container,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    cursorColor = accent,
)

@Composable
private fun Scoreboard(
    teamOneName: String,
    teamTwoName: String,
    teamOneTotal: Int,
    teamTwoTotal: Int,
    hasRounds: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TotalTile(
            team = TEAM_ONE,
            name = teamOneName,
            total = teamOneTotal,
            leading = hasRounds && teamOneTotal > teamTwoTotal,
            modifier = Modifier.weight(1f),
        )
        TotalTile(
            team = TEAM_TWO,
            name = teamTwoName,
            total = teamTwoTotal,
            leading = hasRounds && teamTwoTotal > teamOneTotal,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TotalTile(
    team: Int,
    name: String,
    total: Int,
    leading: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = teamColors(team)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.container)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .animateContentSize(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                color = colors.onContainer,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (leading) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = "Liderando",
                    modifier = Modifier.size(18.dp),
                    tint = colors.accent,
                )
            }
        }
        Text(
            text = total.toString(),
            color = colors.accent,
            style = Tabular,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
        )
        Text(
            text = if (leading) "Liderando" else "Total",
            color = colors.onContainer.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun RoundsHeader(count: Int, teamOneName: String, teamTwoName: String) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Style,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Rodadas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (count > 0) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = count.toString(),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 8.dp, vertical = 1.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (count > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 10.dp),
            ) {
                Spacer(Modifier.weight(1f))
                ColumnLabel(teamOneName, teamColors(TEAM_ONE).accent)
                ColumnLabel(teamTwoName, teamColors(TEAM_TWO).accent)
                Spacer(Modifier.width(DeleteColumn))
            }
        }
    }
}

@Composable
private fun ColumnLabel(name: String, color: Color) {
    Text(
        text = name,
        modifier = Modifier.width(ScoreColumn),
        color = color,
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun RoundRow(number: Int, round: RoundScore, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(start = 10.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.weight(1f))
        RoundScoreText(round.teamOne)
        RoundScoreText(round.teamTwo)
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(DeleteColumn)
                .semantics { contentDescription = "Apagar rodada $number" },
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RoundScoreText(score: Int) {
    Text(
        text = score.toString(),
        modifier = Modifier.width(ScoreColumn),
        color = if (score < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.End,
        style = Tabular,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        softWrap = false,
    )
}

@Composable
private fun EmptyRounds() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Nenhuma rodada ainda",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Informe os pontos das duas duplas e toque em Adicionar rodada.",
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ConfirmDialog(
    icon: ImageVector,
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(title, textAlign = TextAlign.Center) },
        text = { Text(message) },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        iconContentColor = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(28.dp),
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

@Preview(showBackground = true, name = "Placar vazio", widthDp = 390, heightDp = 844)
@Composable
private fun TrancaScoreScreenPreview() {
    TrancaTheme(darkTheme = false) {
        TrancaScoreScreen()
    }
}
