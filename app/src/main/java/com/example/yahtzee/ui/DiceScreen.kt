package com.example.yahtzee.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.YahtzeeScreen
import com.example.yahtzee.data.DiceImages


private const val TAG = "DiceScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiceScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    results: MutableList<Int>,
    lockedDices: MutableList<Boolean>,
    rerolls: Int,
    rounds: Int,
    enableRoll: Boolean,
    pointsAccepted: Boolean,
    rollScores: MutableList<Int>,
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
        Spacer(modifier.height(7.dp))

        Text(
            fontSize = 24.sp,
            style = MaterialTheme.typography.body1,
            color = colorResource(id = R.color.dark_brown),
            text = if (rounds == 0) stringResource(
                id = R.string.total_points_info,
                rollScores[15]
            )
            else stringResource(R.string.rolls_info, rerolls),
        )
        Spacer(modifier.height(12.dp))

        //val results = gameUiState.results
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
                        // gameViewModel.updateLockedDices(index)
                        .clickable(onClick = { if (!pointsAccepted) onDiceClick(index) })
                ) {
                    Image(
                        painter = painterResource(id = DiceImages[item - 1]),
                        contentDescription = item.toString(),
                        modifier
                            .size(150.dp)
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
                    .size(150.dp)
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
                onButtonClicked = { navController.navigate(YahtzeeScreen.Points.name) },
                buttonText = stringResource(id = R.string.points_sheet)
            )
            // Roll (Next) button
            RollingButton(
                height = height,
                width = width,
                pointsAccepted = pointsAccepted,
                rounds = rounds,
                enableRoll = enableRoll,
                onNextButtonClick = onNextButtonClick,
                onRollClicked = onRollClicked
            )
        }
    }
}

@Composable
fun RollingButton(
    height: Int,
    width: Int,
    pointsAccepted: Boolean,
    rounds: Int,
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
            buttonText = if (rounds == 0) stringResource(R.string.new_game)
            else stringResource(R.string.button_new_round)
        )
    } else {
        // Roll-button
        ButtonDisableable(
            height = height,
            width = width,
            enable = enableRoll, //gameViewModel.uiState.value.enableRoll,
            onButtonClicked = {
                onRollClicked()
            },
            buttonText = stringResource(id = R.string.roll)
        )
    }
}

