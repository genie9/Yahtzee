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
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YahtzeeTheme {
                YahtzeeApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview
@Composable
fun YahtzeeApp() {
    YahtzeeMain()
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun YahtzeeMain() {
    val openDialog = remember { mutableStateOf(false) }
    val pointsFilled = remember { mutableStateOf(false) }
    var results: MutableList<Int> = remember { mutableListOf(1,1,1,1,1) }
    var rerolls by remember { mutableStateOf(3) }
    var rollScores = remember { mutableStateListOf<Int?>()
        .apply { addAll(List<Int?>(16) {null}) } }

    val diceImage = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6)

    var lockedDices = remember { mutableStateListOf(false, false, false, false, false) }

    fun roll() {
        if(rerolls > 0) {
            if (!lockedDices.contains(true)) {
                results.replaceAll { Random.nextInt(1, 7) }
            } else {
                for (i in 0..4){
                    results[i] = if (lockedDices[i]) results[i]  else (1..6).random()
                }
            }
            rerolls -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Rolls left $rerolls", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(70.dp),
        ) {
            itemsIndexed(results) { index: Int, item: Int ->
                Box(modifier = Modifier.clickable(onClick = {
                    if (rerolls < 3) { lockedDices[index] = !lockedDices[index] }
                    })
                ) {
                    Image(
                        painter = painterResource(id = diceImage[item-1]),
                        contentDescription = diceImage.indexOf(item).toString(),
                        modifier = Modifier
                            .size(170.dp)
                            .background(
                                color = if (lockedDices[index]) Color.Blue else Color.Gray,
                                shape = CircleShape
                            )
                    )
                }
            }
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {
                        openDialog.value = !openDialog.value
                    }
                ) {
                    Text(text = "Points Sheet", modifier = Modifier.padding(0.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        if (rerolls < 3 && pointsFilled.value) {
            Button(onClick = {
                rerolls = 3
                results.replaceAll {1}
                lockedDices.replaceAll {false}
                pointsFilled.value = false
            })
            {
                Text(text = "New Round", fontSize = 24.sp)
            }
        } else {
            Button(onClick = { roll() })
            {
                Text(text = stringResource(R.string.roll), fontSize = 24.sp)
            }
        }
        TableScreen(pointsFilled = pointsFilled, openDialog = openDialog, results = results,
            rollScores = rollScores, rerolls = rerolls)
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .background(Color.White)
    )
}

@Composable
fun TableScreen(pointsFilled: MutableState<Boolean>, rerolls:Int, openDialog: MutableState<Boolean>,
                results: List<Int>, rollScores: SnapshotStateList<Int?>) {
    val rollNames: List<String> = listOf(
        "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Upper Total",
        "Bonus", "Same of Three", "Same of Four", "Full House", "Small Straight", "Big Straight",
        "Chance", "YAHTZEE", "TOTAL"
    )
    var lastIndex by remember { mutableStateOf(-1) }

    fun upperStats(index: Int): Int {
        var points = results.sumOf {if (it == index+1) it else 0}
        rollScores[index] = points
        var upperScores = rollScores.slice(0..5).toMutableList()
        var upperTotal = upperScores.sumOf {it ?: 0}
        rollScores[6] = upperTotal
        if (upperTotal >= 63 ) {
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
        for ( i in 0..1 ) {
            if ( sorted[i] == sorted[i + 1] && sorted[i + 1] == sorted[i + 2]
                && sorted[i + 2] == sorted[i + 3] ) {
                return sorted[i] + sorted[i + 1] + sorted[i + 2] + sorted[i + 3]
            }
        }
        return 0
    }

    fun fullHouse(): Int {
        val sorted = results.sorted()
        if ( ( sorted[0] == sorted[1] && sorted[1] == sorted[2] && sorted[3] == sorted[4] ) ||
            ( sorted[0] == sorted[1] && sorted[2] == sorted[3] && sorted[3] == sorted[4] ) ) {
            return sorted.sum()
        }
        return 0
    }

    fun straight(first: Int): Int {
        val sorted = results.sorted()
        if ( sorted[0] == first
            && sorted[1] == sorted[0]+1
            && sorted[2] == sorted[1]+1
            && sorted[3] == sorted[2]+1
            && sorted[4] == sorted[3]+1 ) {
            return sorted.sum()
        }
        return 0
    }

    fun fillPoints(index: Int): Int? {
        var score: Int?
        if (rollScores[index] == null && rerolls < 3) {
            score = when (index) {
                in 0..5 -> upperStats(index)
                8 -> threeSame()
                9 -> fourSame()
                10 -> fullHouse()
                11 -> straight(1)
                12 -> straight(2)
                13 -> results.sum()
                14 -> {if (results.all { results[0] == it}) 50 else 0 }
                else -> null
            }
            if ( score != null ) {
                lastIndex = index
            }
            rollScores[index] = score
            rollScores[15] = rollScores.slice(6..14).toMutableList().sumOf {it ?: 0}
            return score
        }
        return null
    }

    @Composable
    fun RowScope.TableCellClickable(
        index: Int,
        weight: Float,
        text: String
    ) {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .clickable(onClick = {
                    if ( lastIndex > -1 ){
                        if ( lastIndex in 0..5 ) {
                            rollScores[6] =- rollScores[lastIndex]!!
                        }
                        rollScores[lastIndex] = null
                    }
                    fillPoints(index)
                })
        )
    }

    val column1Weight = .6f // 60%
    val column2Weight = .4f // 40%

    if (openDialog.value) {
        Popup(
            properties = PopupProperties()
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.Gray)
            ){
                item {
                    Row(Modifier.background(Color.Gray)) {
                        TableCell(text = "ROLLS", weight = column1Weight)
                        TableCell(text = "POINTS", weight = column2Weight)
                    }
                }
                items(rollNames) { rollName ->
                    Row(Modifier.fillMaxWidth()) {
                        var rollNameIndex = rollNames.indexOf(rollName)
                        TableCell(text = rollName, weight = column1Weight)
                        TableCellClickable(
                            index = rollNameIndex,
                            weight = column2Weight,
                            text = rollScores[rollNameIndex]?.toString() ?: ""
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        Button(
                            modifier = Modifier.padding(20.dp),
                            onClick = {
                                if ( lastIndex > -1 && rollScores[lastIndex] != null ) {
                                    pointsFilled.value = true
                                    lastIndex = -1
                                    openDialog.value = !openDialog.value
                                }
                            })
                        {
                            Text(text = "Accept", fontSize = 24.sp)
                        }
                        Button(
                            modifier = Modifier.padding(20.dp),
                            onClick = { openDialog.value = !openDialog.value })
                        {
                            Text(text = "Back", fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}



