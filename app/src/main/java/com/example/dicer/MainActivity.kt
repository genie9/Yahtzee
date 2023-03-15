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
package com.example.dicer
import android.os.Build
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.widget.TableLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.dicer.ui.theme.DicerTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DicerTheme {
                DiceRollerApp()
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.N)
@Preview
@Composable
fun DiceRollerApp() {
    DiceWithButtonAndImage(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    )
}

/*
@RequiresApi(Build.VERSION_CODES.N)
@Preview
@Composable
fun DiceRollerApp() {
    TableScreen()
}
*/

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    val openDialog = remember { mutableStateOf(false) }
    var results: MutableList<Int> = remember { mutableListOf<Int>(1,1,1,1,1) }
    var rerolls by remember { mutableStateOf(3) }

    val diceImage = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6)

    var lockedDices = remember { mutableStateListOf<Boolean>(false, false, false, false, false) }

    fun roll() {
        if(rerolls > 0) {
            if (!lockedDices.contains(true)) {
                results.replaceAll() { Random.nextInt(1, 6) }
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

        Text(text = "rolls left $rerolls", fontSize = 28.sp)

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
                            .size(180.dp)
                            .background(
                                color = if (lockedDices[index]) Color.Blue else Color.Gray,
                                shape = CircleShape
                            )
                    )
                }
            item {
                Button(

                    // on below line we are adding modifier.
                    // and padding to it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = {

                        // on below line we are updating
                        // boolean value of open dialog.
                        openDialog.value = !openDialog.value

                        // on below line we are checking if dialog is close
                        if (!openDialog.value) {

                        // on below line we are updating value
                        //buttonTitle.value = "Show Pop Up"
                        }
                    }
                ) {
                    Text(text = "Open Stats", modifier = Modifier.padding(3.dp))
                }
            }

            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        if (rerolls <= 0) {
            Button(onClick = {
                rerolls = 3
                results.replaceAll {1}
                lockedDices.replaceAll(){false}
            })
            {
                Text(text = "New Game", fontSize = 24.sp)
            }
        } else {
            Button(onClick = { roll() })
            {
                Text(text = stringResource(R.string.roll), fontSize = 24.sp)
            }
        }
        TableScreen(openDialog = openDialog, results = results)
    }
}

@Composable
fun pointsWindow() {
    Dialog(
        onDismissRequest = {
            // onDismiss()
        })
    {
        Surface(
            modifier = Modifier
                .width(150.dp)
                .height(300.dp),
            elevation = 4.dp
        ) {}
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
            .padding(8.dp).background(Color.White)
    )
}

@Composable
fun TableScreen(openDialog: MutableState<Boolean>, results: List<Int>) {
    val rollNames = listOf<String>("Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Total",
        "Bonus", "Same of Three", "Same of Four", "Full House", "Small Straight", "Big Straight",
        "Chance", "YAHTZEE")
    var rollScores = remember { MutableList<Int?>(15) {null} }
    //var results = listOf<Int>(1,3,5,6,2)

    fun fillPoints(index: Int): String {
        println("INESSÄ")
        if (rollScores[index] == null) {
            println("TYHJA")
            rollScores[index] = results.sum()
            println(rollScores)
            return rollScores[index].toString()
        }
        return ""
    }
    @Composable
    fun RowScope.TableCellClickable(
        index: Int,
        weight: Float,
    ) {
        var scoreText by remember { mutableStateOf<String>("") }.apply { this.value }
        Text(
            text = scoreText,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .clickable(onClick = {
                    scoreText = fillPoints(index)
                })
        )
    }

    val column1Weight = .6f // 30%
    val column2Weight = .4f // 70%
    if (openDialog.value) {
        // on below line we are updating button
        // title value.
        //buttonTitle.value = "Hide Pop Up"
        // on below line we are adding pop up

        Popup(
            // on below line we are adding
            // alignment and properties.
            //alignment = Alignment.TopCenter,
            properties = PopupProperties()
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.Gray)
            )
            {
                item {
                    Row(Modifier.background(Color.Gray)) {
                        TableCell(text = "Rolls", weight = column1Weight)
                        TableCell(text = "Points", weight = column2Weight)
                    }
                }
                items(rollNames) { rollName ->
                    Row(Modifier.fillMaxWidth()) {
                        TableCell(text = rollName, weight = column1Weight)
                        TableCellClickable(
                            index = rollNames.indexOf(rollName),
                            weight = column2Weight
                        )
                    }
                }
                item {
                    Button(modifier = Modifier.padding(20.dp),  onClick = { openDialog.value = !openDialog.value})
                    {
                        Text(text = "Close", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
