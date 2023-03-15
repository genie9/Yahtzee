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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    var results = remember { mutableStateListOf(1,1,1,1,1)}
    var rerolls by remember { mutableStateOf(3) }
    var rerollsMessage: String by remember { mutableStateOf("rolls left $rerolls") }
    val diceImage = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6)

    var locked = mutableListOf<Int>()

    fun roll() {
        if(rerolls > 0) {
            results.replaceAll() { Random.nextInt(1, 6) }
            rerolls -= 1
            println("rerolls left $rerolls")
        }
    }

    Column(modifier = modifier.width(105.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = rerollsMessage, fontSize = 20.sp)

        for (i in results){
            var dice = diceImage[i-1]
            Box() {
                Image(painter = painterResource(id = dice),
                    contentDescription = diceImage.indexOf(dice).toString())
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {roll()})
        {
            Text(text = stringResource(R.string.roll), fontSize = 24.sp)
        }
    }
}