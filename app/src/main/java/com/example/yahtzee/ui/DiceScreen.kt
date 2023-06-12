package com.example.yahtzee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.yahtzee.R
import com.example.yahtzee.data.DiceImages
import com.example.yahtzee.ui.theme.YahtzeeTheme


private const val TAG = "DiceScreen"

@Composable
fun DiceScreen(
    modifier: Modifier = Modifier,
    results: MutableList<Int>,
    lockedDices: MutableList<Boolean>,
    rerolls: Int,
    rounds: Int,
    enableRoll: Boolean,
    pointsAccepted: Boolean,
    rollScores: MutableList<Int>,
    onNavButtonClicked: (String) -> Unit = {},
    onDiceClick: (Int) -> Unit = {},
    onNextButtonClick: () -> Unit = {},
    onRollClicked: () -> Unit = {}
) {
    Column(
        modifier
            .background(colorResource(id = R.color.light_brown))
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        )
        {
            Button(
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .align(Alignment.TopStart),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.light_gray_green)
                ),
                onClick = { onNavButtonClicked("menu") }
            )
            {
                Icon(
                    Icons.Filled.Menu,
                    modifier = Modifier.size(36.dp),
                    contentDescription = "menu icons",
                )
            }
            Text(
                modifier = Modifier.align(Alignment.BottomCenter),
                fontSize = 24.sp,
                style = MaterialTheme.typography.body1,
                color = colorResource(id = R.color.dark_brown),
                text = stringResource(R.string.rolls_info, rerolls),
            )
        }
        Spacer(modifier.height(30.dp))

        val upperFour = results.slice(0..3)
        val fifth = results[4]

        // First four dices
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {

            itemsIndexed(upperFour) { index: Int, item: Int ->
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable(onClick = { if (!pointsAccepted) onDiceClick(index) })
                ) {
                    Image(
                        painter = painterResource(id = DiceImages[item - 1]),
                        contentDescription = item.toString(),
                        modifier
                            .size(140.dp)
                            .background(
                                color =
                                if (lockedDices[index]) colorResource(id = R.color.gray_green)
                                else colorResource(id = R.color.light_brown),
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                    Spacer(modifier.height(15.dp))
                }
            }
        }
        // Fifth dice
        Row {
            Box(
                modifier
                    .size(140.dp)
                    .clickable(onClick = { if (!pointsAccepted) onDiceClick(4) })
            ) {
                Image(
                    painter = painterResource(id = DiceImages[fifth - 1]),
                    contentDescription = fifth.toString(),
                    modifier
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
        Spacer(modifier.height(15.dp))
        // Main layout buttons
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val height = 50
            val width = 120

            // Points-button
            ButtonDisableable(
                height = height,
                width = width,
                onButtonClicked = { onNavButtonClicked("points") },
                buttonText = stringResource(id = R.string.points_sheet)
            )
            // Roll (Next) button
            RollingButton(
                height = height,
                width = width,
                pointsAccepted = pointsAccepted,
                enableRoll = enableRoll,
                onNextButtonClick = onNextButtonClick,
                onRollClicked = onRollClicked
            )
        }
    }
    if (rounds == 0) {
        Popup(alignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
                    .background(
                        color = colorResource(id = R.color.light_gray_green),
                        shape = RoundedCornerShape(10.dp)
                    ),
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(5.dp, 30.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 35.sp,
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id = R.color.dark_brown),
                    text = stringResource(id = R.string.game_over)
                )
                Spacer(modifier.height(5.dp))
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id = R.color.dark_brown),
                    text = stringResource(id = R.string.total_points_info)
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(5.dp, 5.dp, 5.dp, 30.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp,
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id = R.color.dark_brown),
                    text = rollScores[15].toString()
                )

            }
        }
    }
}

@Composable
fun RollingButton(
    height: Int,
    width: Int,
    pointsAccepted: Boolean,
    enableRoll: Boolean,
    onNextButtonClick: () -> Unit = {},
    onRollClicked: () -> Unit = {}
) {
    if (pointsAccepted) {
        // Next-button
        ButtonDisableable(
            height = height,
            width = width,
            onButtonClicked = { onNextButtonClick() },
            buttonText = stringResource(R.string.button_new_round)
        )
    } else {
        // Roll-button
        ButtonDisableable(
            height = height,
            width = width,
            enable = enableRoll,
            onButtonClicked = {
                onRollClicked()
            },
            buttonText = stringResource(id = R.string.roll)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiceScreenPreview() {
    YahtzeeTheme {
        DiceScreen(
            results = mutableListOf(1, 2, 3, 4, 5),
            lockedDices = mutableListOf(false, false, false, false, false),
            rerolls = 3,
            rounds = 13,
            enableRoll = true,
            pointsAccepted = false,
            rollScores = mutableListOf<Int>()
                .apply { addAll(List(6) { -1 }) }
                .apply { addAll(6, listOf(0, 0)) }
                .apply { addAll(List(7) { -1 }) }
                .apply { addAll(15, listOf(0)) })
    }
}