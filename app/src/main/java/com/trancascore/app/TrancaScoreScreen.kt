package com.trancascore.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrancaScoreScreen() {
    var game by rememberSaveable(stateSaver = TrancaGameSaver) {
        mutableStateOf(TrancaGame())
    }
    var playerOne by rememberSaveable { mutableStateOf("") }
    var playerTwo by rememberSaveable { mutableStateOf("") }
    var playerThree by rememberSaveable { mutableStateOf("") }
    var playerFour by rememberSaveable { mutableStateOf("") }
    var teamOneScore by rememberSaveable { mutableStateOf("") }
    var teamTwoScore by rememberSaveable { mutableStateOf("") }
    var showSummary by rememberSaveable { mutableStateOf(false) }
    var showResetConfirmation by rememberSaveable { mutableStateOf(false) }

    val teamOneName = teamName(playerOne, playerTwo, "Dupla 1")
    val teamTwoName = teamName(playerThree, playerFour, "Dupla 2")
    val parsedTeamOne = teamOneScore.trim().toIntOrNull()
    val parsedTeamTwo = teamTwoScore.trim().toIntOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placar") },
                actions = {
                    IconButton(
                        onClick = { showResetConfirmation = true },
                        enabled = game.rounds.isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Nova partida",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Header()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    TeamCard(
                        title = "Dupla 1",
                        color = MaterialTheme.colorScheme.secondary,
                        firstPlayer = playerOne,
                        onFirstPlayerChange = { playerOne = it },
                        secondPlayer = playerTwo,
                        onSecondPlayerChange = { playerTwo = it },
                        total = game.teamOneTotal,
                        modifier = Modifier.weight(1f),
                    )
                    TeamCard(
                        title = "Dupla 2",
                        color = MaterialTheme.colorScheme.tertiary,
                        firstPlayer = playerThree,
                        onFirstPlayerChange = { playerThree = it },
                        secondPlayer = playerFour,
                        onSecondPlayerChange = { playerFour = it },
                        total = game.teamTwoTotal,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                NewRoundCard(
                    teamOneName = teamOneName,
                    teamTwoName = teamTwoName,
                    teamOneScore = teamOneScore,
                    onTeamOneScoreChange = { teamOneScore = it },
                    teamTwoScore = teamTwoScore,
                    onTeamTwoScoreChange = { teamTwoScore = it },
                    canAdd = parsedTeamOne != null && parsedTeamTwo != null,
                    onAdd = {
                        game = game.addRound(
                            teamOne = requireNotNull(parsedTeamOne),
                            teamTwo = requireNotNull(parsedTeamTwo),
                        )
                        teamOneScore = ""
                        teamTwoScore = ""
                    },
                )
            }

            if (game.rounds.isNotEmpty()) {
                item {
                    HistoryCard(
                        rounds = game.rounds,
                        teamOneName = teamOneName,
                        teamTwoName = teamTwoName,
                        onUndo = { game = game.removeLastRound() },
                    )
                }

                item {
                    Button(
                        onClick = { showSummary = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver resultado final")
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Começar uma nova partida?") },
            text = { Text("O placar será zerado e os nomes serão mantidos.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        game = game.reset()
                        teamOneScore = ""
                        teamTwoScore = ""
                        showResetConfirmation = false
                    },
                ) {
                    Text("Zerar placar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    if (showSummary) {
        ResultDialog(
            teamOneName = teamOneName,
            teamTwoName = teamTwoName,
            teamOneTotal = game.teamOneTotal,
            teamTwoTotal = game.teamTwoTotal,
            roundCount = game.rounds.size,
            onDismiss = { showSummary = false },
        )
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "♣",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 48.sp,
            lineHeight = 48.sp,
        )
        Text(
            text = "Tranca",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Anote os pontos de cada rodada e deixe a soma com a gente.",
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun TeamCard(
    title: String,
    color: Color,
    firstPlayer: String,
    onFirstPlayerChange: (String) -> Unit,
    secondPlayer: String,
    onSecondPlayerChange: (String) -> Unit,
    total: Int,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(title, color = color, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = firstPlayer,
                onValueChange = onFirstPlayerChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Jogador 1") },
                singleLine = true,
            )
            OutlinedTextField(
                value = secondPlayer,
                onValueChange = onSecondPlayerChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Jogador 2") },
                singleLine = true,
            )
            HorizontalDivider()
            Text(
                text = "TOTAL",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = total.toString(),
                color = color,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NewRoundCard(
    teamOneName: String,
    teamTwoName: String,
    teamOneScore: String,
    onTeamOneScoreChange: (String) -> Unit,
    teamTwoScore: String,
    onTeamTwoScoreChange: (String) -> Unit,
    canAdd: Boolean,
    onAdd: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Text("Nova rodada", fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScoreField(
                    title = teamOneName,
                    score = teamOneScore,
                    onScoreChange = onTeamOneScoreChange,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
                ScoreField(
                    title = teamTwoName,
                    score = teamTwoScore,
                    onScoreChange = onTeamTwoScoreChange,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                onClick = onAdd,
                enabled = canAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Adicionar rodada")
            }
        }
    }
}

@Composable
private fun ScoreField(
    title: String,
    score: String,
    onScoreChange: (String) -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        OutlinedTextField(
            value = score,
            onValueChange = onScoreChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun HistoryCard(
    rounds: List<RoundScore>,
    teamOneName: String,
    teamTwoName: String,
    onUndo: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rodadas",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                )
                OutlinedButton(onClick = onUndo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Desfazer")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
            ) {
                Text(
                    text = "Rodada",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                )
                TableHeader(teamOneName)
                TableHeader(teamTwoName)
            }
            HorizontalDivider()

            rounds.forEachIndexed { index, round ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TableScore(round.teamOne)
                    TableScore(round.teamTwo)
                }
                if (index != rounds.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun TableHeader(name: String) {
    Text(
        text = name,
        modifier = Modifier.width(86.dp),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun TableScore(score: Int) {
    Text(
        text = score.toString(),
        modifier = Modifier.width(86.dp),
        textAlign = TextAlign.End,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun ResultDialog(
    teamOneName: String,
    teamTwoName: String,
    teamOneTotal: Int,
    teamTwoTotal: Int,
    roundCount: Int,
    onDismiss: () -> Unit,
) {
    val title = when {
        teamOneTotal == teamTwoTotal -> "Empate!"
        teamOneTotal > teamTwoTotal -> "$teamOneName venceu!"
        else -> "$teamTwoName venceu!"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFB300),
            )
        },
        title = {
            Text(title, textAlign = TextAlign.Center)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "$roundCount ${if (roundCount == 1) "rodada" else "rodadas"}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FinalScore(
                        name = teamOneName,
                        score = teamOneTotal,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                    )
                    FinalScore(
                        name = teamTwoName,
                        score = teamTwoTotal,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Voltar ao placar")
            }
        },
    )
}

@Composable
private fun FinalScore(
    name: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = score.toString(),
                modifier = Modifier.padding(top = 8.dp),
                color = color,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun teamName(first: String, second: String, fallback: String): String {
    val names = listOf(first, second)
        .map(String::trim)
        .filter(String::isNotEmpty)
    return names.joinToString(" & ").ifEmpty { fallback }
}

@Preview(showBackground = true, name = "Placar vazio", widthDp = 390, heightDp = 844)
@Composable
private fun TrancaScoreScreenPreview() {
    TrancaTheme(darkTheme = false) {
        TrancaScoreScreen()
    }
}
