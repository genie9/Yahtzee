package com.example.yahtzee

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yahtzee.data.DiceImages
import com.example.yahtzee.ui.theme.ButtonDisableable
import com.example.yahtzee.ui.theme.PointsScreen
import kotlin.random.Random


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiceScreen(
    modifier: Modifier = Modifier
) {
    val diceImage = DiceImages
    // Handling dices
    val results: MutableList<Int> = remember { mutableListOf(1, 1, 1, 1, 1) }
    val lockedDices = remember { mutableStateListOf(false, false, false, false, false) }

    // Game play variables
    val rounds: MutableState<Int> = remember { mutableStateOf(13) }
    var rerolls by remember { mutableStateOf(3) }

    // Handling points
    val rollScores = remember {
        mutableStateListOf<Int>()
            .apply { addAll(List(6) { -1 }) }
            .apply { addAll(6, listOf(0, 0)) }
            .apply { addAll(List(7) { -1 }) }
            .apply { addAll(15, listOf(0)) }
    }
    val rollScoresLocked = remember {
        mutableListOf<Boolean>()
            .apply { addAll(List(6) { false }) }
            .apply { addAll(6, listOf(true, true)) }
            .apply { addAll(List(7) { false }) }
            .apply { addAll(15, listOf(true)) }
    }

    // Handling layout
    val openDialog = remember { mutableStateOf(false) }
    val pointsFilled = remember { mutableStateOf(false) }
    var enableRoll = remember { mutableStateOf(true) }

    @VisibleForTesting
    fun newRoundActions() {
        rerolls = 3
        enableRoll.value = true
        lockedDices.replaceAll { false }
        pointsFilled.value = false
        rerolls = roll(rerolls, lockedDices, results)
        // New Game actions
        if (rounds.value == 0) {
            rounds.value = 13
            rollScores.apply { clear() }
                .apply { addAll(List(6) { -1 }) }
                .apply { addAll(6, listOf(0, 0)) }
                .apply { addAll(List(7) { -1 }) }
                .apply { addAll(15, listOf(0)) }

            rollScoresLocked.apply { clear() }
                .apply { addAll(List(6) { false }) }
                .apply { addAll(6, listOf(true, true)) }
                .apply { addAll(List(7) { false }) }
                .apply { addAll(15, listOf(true)) }
        }
    }

    @Composable
    fun RollingButton(height: Int, width: Int) {
        if (rerolls <= 3 && pointsFilled.value) {
            // Next-button
            ButtonDisableable(
                height = height,
                width = width,
                onButtonClicked = { newRoundActions() },
                buttonText = if (rounds.value == 0) stringResource(R.string.new_game)
                else stringResource(R.string.button_new_round)
            )
        } else {
            // Roll-button
            ButtonDisableable(
                height = height,
                width = width,
                enable = enableRoll.value,
                onButtonClicked = {
                    rerolls = roll(rerolls, lockedDices, results)
                    if (!pointsFilled.value && rerolls == 0) {
                        enableRoll.value = false
                    }
                },
                buttonText = stringResource(id = R.string.roll)
            )
        }
    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.light_brown))
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(7.dp))

        Text(
            fontSize = 24.sp,
            style = MaterialTheme.typography.body1,
            color = colorResource(id = R.color.dark_brown),
            text = if (rounds.value == 0) stringResource(
                id = R.string.total_points_info,
                rollScores[15]
            )
            else stringResource(R.string.rolls_info, rerolls),
        )
        Spacer(modifier = Modifier.height(12.dp))

        val upperFour = results.slice(0..3)
        val fifth = results[4]

        // First four dices
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {

            itemsIndexed(upperFour) { index: Int, item: Int ->
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clickable(onClick = {
                            if (rerolls < 3) {
                                lockedDices[index] = !lockedDices[index]
                            }
                        })
                ) {
                    Image(
                        painter = painterResource(id = diceImage[item - 1]),
                        contentDescription = item.toString(),
                        modifier = Modifier
                            .size(150.dp)
                            .background(
                                color =
                                if (lockedDices[index]) colorResource(id = R.color.gray_green)
                                else colorResource(id = R.color.light_brown),
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
        // Fifth dice
        Row() {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clickable(onClick = {
                        if (rerolls < 3) {
                            lockedDices[4] = !lockedDices[4]
                        }
                    })
            ) {
                Image(
                    painter = painterResource(id = diceImage[fifth - 1]),
                    contentDescription = fifth.toString(),
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            color =
                            if (lockedDices[4]) colorResource(id = R.color.gray_green)
                            else colorResource(id = R.color.light_brown),
                            shape = RoundedCornerShape(10.dp),
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        // Main layout buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val height = 50
            val width = 120

            // Points-button
            ButtonDisableable(
                height = height,
                width = width,
                onButtonClicked = {
                    openDialog.value = !openDialog.value
                },
                buttonText = stringResource(id = R.string.points_sheet)
            )
            // Roll (Next) button
            RollingButton(height, width)
        }

        // Pop up fillable points screen
        PointsScreen(
            rounds = rounds,
            pointsFilled = pointsFilled,
            openDialog = openDialog,
            results = results,
            rollScores = rollScores,
            rollScoresLocked = rollScoresLocked,
            rerolls = rerolls
        )
    }
}

@VisibleForTesting
fun roll(rerolls: Int, lockedDices: SnapshotStateList<Boolean>, results: MutableList<Int>): Int {
    var tempRerolls = rerolls
    if (tempRerolls > 0) {
        if (!lockedDices.contains(true)) {
            results.replaceAll { Random.nextInt(1, 7) }
        } else {
            for (i in 0..4) {
                results[i] = if (lockedDices[i]) results[i] else (1..6).random()
            }
        }
    }
    if (tempRerolls > 0) tempRerolls -= 1
    return tempRerolls
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, colorResource(id = R.color.dark_brown))
            .weight(weight)
            .height(32.dp)
            .padding(4.dp)
            .background(colorResource(id = R.color.light_green))
    )
}
