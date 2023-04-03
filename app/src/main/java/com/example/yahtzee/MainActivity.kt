/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.yahtzee

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.yahtzee.ui.theme.YahtzeeTheme
import kotlin.random.Random


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YahtzeeTheme {
                YahtzeeApp()
            }
        }
    }
}

@Preview
@Composable
fun YahtzeeApp() {
    YahtzeeMain()
}

@Composable
private fun ButtonDisableable(
    pad: Int = 0,
    height: Int,
    width: Int,
    enable: Boolean = true,
    onButtonClicked: () -> Unit,
    buttonText: String
) {
    Button(
        modifier = Modifier
            .padding(pad.dp)
            .height(height.dp)
            .width(width.dp),
        enabled = enable,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.light_green),
            disabledBackgroundColor = colorResource(id = R.color.light_gray_green),
            contentColor = colorResource(id = R.color.dark_brown),
            disabledContentColor = colorResource(id = R.color.gray_green)
        ),
        onClick = onButtonClicked
    )
    {
        Text(
            text = buttonText,
            fontSize = 24.sp,
            style = MaterialTheme.typography.body1
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YahtzeeMain() {
    val openDialog = remember { mutableStateOf(false) }
    val pointsFilled = remember { mutableStateOf(false) }
    val results: MutableList<Int> = remember { mutableListOf(1, 1, 1, 1, 1) }
    val rounds: MutableState<Int> = remember { mutableStateOf(13) }
    var rerolls by remember { mutableStateOf(3) }
    var enableRoll = remember { mutableStateOf(true) }
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

    val diceImage = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )

    val lockedDices = remember { mutableStateListOf(false, false, false, false, false) }

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
            text = if (rounds.value == 0) stringResource(
                id = R.string.total_points_info,
                rollScores[15]
            )
            else stringResource(R.string.rolls_info, rerolls),
            fontSize = 26.sp,
            style = MaterialTheme.typography.body1,
            color = colorResource(id = R.color.dark_brown),
        )
        Spacer(modifier = Modifier.height(12.dp))

        val upperFour = results.slice(0..3)
        val fifth = results[4]

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
            RollingButton(height, width)
        }


        TableScreen(
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

@Composable
fun TableScreen(
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

    val rollNames: List<String> = listOf(
        "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Upper Total",
        "Bonus", "Same of Three", "Same of Four", "Full House", "Small Straight", "Big Straight",
        "Chance", "YAHTZEE", "TOTAL"
    )
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

    fun threeSame(): Int {
        val sorted = results.sorted()
        for (i in 0..2) {
            if (sorted[i] == sorted[i + 1] && sorted[i + 1] == sorted[i + 2]) {
                return sorted[i] + sorted[i + 1] + sorted[i + 2]
            }
        }
        return 0
    }

    fun fourSame(): Int {
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

    fun fullHouse(): Int {
        val sorted = results.sorted()
        if ((sorted[0] == sorted[1] && sorted[1] == sorted[2] && sorted[3] == sorted[4]) ||
            (sorted[0] == sorted[1] && sorted[2] == sorted[3] && sorted[3] == sorted[4])
        ) {
            return sorted.sum()
        }
        return 0
    }

    fun straight(first: Int): Int {
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

    fun fillPoints(index: Int): Int {
        val score: Int
        if (rollScores[index] == -1 && rerolls < 3) {
            score = when (index) {
                in 0..5 -> upperStats(index)
                8 -> threeSame()
                9 -> fourSame()
                10 -> fullHouse()
                11 -> straight(1)
                12 -> straight(2)
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

    @SuppressLint("ResourceType")
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

    if (openDialog.value) {
        Popup(
            properties = PopupProperties()
        ) {
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
                        val height = 50
                        val width = 130

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
    }
}


