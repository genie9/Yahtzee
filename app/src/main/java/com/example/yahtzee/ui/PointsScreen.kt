package com.example.yahtzee.ui.theme

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.yahtzee.R
import com.example.yahtzee.TableCell
import com.example.yahtzee.data.ScoreNames


@Composable
fun PointsScreen(
    rounds: MutableState<Int>,
    pointsFilled: MutableState<Boolean>,
    rerolls: Int,
    openDialog: MutableState<Boolean>,
    results: List<Int>,
    rollScores: SnapshotStateList<Int>,
    rollScoresLocked: MutableList<Boolean>
) {

    val column1Weight = .6f // 60%
    val column2Weight = .4f // 40%
    var enableAccept = remember { mutableStateOf(false) }
    val rollNames = ScoreNames()

    var lastIndex by remember { mutableStateOf(-1) }

    fun upperStats(index: Int): Int {
        val points = results.sumOf { if (it == index + 1) it else 0 }
        rollScores[index] = points
        val upperScores = rollScores.slice(0..5).toMutableList()
        val upperTotal = upperScores.sumOf { if (it != -1) it else 0 }
        rollScores[6] = upperTotal
        if (upperTotal >= 63) {
            rollScores[7] = 35
        }
        return points
    }

    fun fillPoints(index: Int): Int {
        val score: Int
        if (rollScores[index] == -1 && rerolls < 3) {
            score = when (index) {
                in 0..5 -> upperStats(index)
                8 -> threeSame(results)
                9 -> fourSame(results)
                10 -> fullHouse(results)
                11 -> straight(1, results)
                12 -> straight(2, results)
                13 -> results.sum()
                14 -> {
                    if (results.all { results[0] == it }) 50 else 0
                }
                else -> -1
            }
            if (score != -1) {
                lastIndex = index
                enableAccept.value = true
            }
            rollScores[index] = score
            rollScores[15] =
                rollScores.slice(6..14).toMutableList()
                    .sumOf { if (it != -1) it else 0 }
            return score
        }
        return -1
    }

    @VisibleForTesting
    fun acceptRound() {
        if (lastIndex > -1 && rollScores[lastIndex] != -1
            && rounds.value > 0
        ) {
            rollScoresLocked[lastIndex] = true
            pointsFilled.value = true
            lastIndex = -1
            rounds.value -= 1
            openDialog.value = !openDialog.value
            enableAccept.value = false
        }
    }

    @Composable
    fun RowScope.TableCellClickable(
        index: Int,
        weight: Float,
        text: String
    ) {
        fun checkIfFillable(): Boolean {
            if (!rollScoresLocked[index]) {
                if (lastIndex > -1) {
                    if (lastIndex in 0..5) {
                        rollScores[6] = rollScores[6].minus(rollScores[lastIndex])
                    }
                    rollScores[lastIndex] = -1
                }
                return true
            }
            return false
        }

        Text(
            text = text,
            Modifier
                .border(1.dp, colorResource(id = R.color.dark_brown))
                .weight(weight)
                .height(32.dp)
                .padding(4.dp)
                .background(
                    if (index in rollScoresLocked
                            .withIndex()
                            .filter { it.value }
                            .map { it.index }
                    ) colorResource(id = R.color.gray_green) else
                        colorResource(id = R.color.light_brown)
                )
                .testTag("points_$index")
                .clickable(onClick = {
                    if (checkIfFillable()) {
                        fillPoints(index)
                    }
                })
        )
    }

    @Composable
    fun ScoreTableLayout() {
        val height = 50
        val width = 130
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(colorResource(id = R.color.light_brown))
        ) {
            item {
                Row(Modifier.background(colorResource(id = R.color.gray_green))) {
                    TableCell(
                        text = stringResource(id = R.string.column_rolls),
                        weight = column1Weight
                    )
                    TableCell(
                        text = stringResource(id = R.string.column_points),
                        weight = column2Weight
                    )
                }
            }

            // Points-view Table
            items(rollNames) { rollName ->
                Row(Modifier.fillMaxWidth()) {
                    val rollNameIndex = rollNames.indexOf(rollName)
                    val rollScore = rollScores[rollNameIndex]
                    TableCell(text = rollName, weight = column1Weight)
                    TableCellClickable(
                        index = rollNameIndex,
                        weight = column2Weight,
                        text = if (rollScore != -1) rollScore.toString() else ""
                    )
                }
            }

            // Points-view Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Accept-button
                    ButtonDisableable(
                        height = height,
                        width = width,
                        pad = 20,
                        enable = enableAccept.value,
                        onButtonClicked = { acceptRound() },
                        buttonText = stringResource(id = R.string.button_accept)
                    )
                    // Back-button
                    ButtonDisableable(
                        height = height,
                        width = width,
                        pad = 20,
                        onButtonClicked = { openDialog.value = !openDialog.value },
                        buttonText = stringResource(id = R.string.button_back)
                    )
                }
            }
        }
    }

    if (openDialog.value) {
        Popup(
            properties = PopupProperties()
        ) {
            ScoreTableLayout()
        }
    }
}

fun threeSame(results: List<Int>): Int {
    val sorted = results.sorted()
    for (i in 0..2) {
        if (sorted[i] == sorted[i + 1] && sorted[i + 1] == sorted[i + 2]) {
            return sorted[i] + sorted[i + 1] + sorted[i + 2]
        }
    }
    return 0
}

fun fourSame(results: List<Int>): Int {
    val sorted = results.sorted()
    for (i in 0..1) {
        if (sorted[i] == sorted[i + 1] && sorted[i + 1] == sorted[i + 2]
            && sorted[i + 2] == sorted[i + 3]
        ) {
            return sorted[i] + sorted[i + 1] + sorted[i + 2] + sorted[i + 3]
        }
    }
    return 0
}

fun fullHouse(results: List<Int>): Int {
    val sorted = results.sorted()
    if ((sorted[0] == sorted[1] && sorted[1] == sorted[2] && sorted[3] == sorted[4]) ||
        (sorted[0] == sorted[1] && sorted[2] == sorted[3] && sorted[3] == sorted[4])
    ) {
        return sorted.sum()
    }
    return 0
}

fun straight(first: Int, results: List<Int>): Int {
    val sorted = results.sorted()
    if (sorted[0] == first
        && sorted[1] == sorted[0] + 1
        && sorted[2] == sorted[1] + 1
        && sorted[3] == sorted[2] + 1
        && sorted[4] == sorted[3] + 1
    ) {
        return sorted.sum()
    }
    return 0
}