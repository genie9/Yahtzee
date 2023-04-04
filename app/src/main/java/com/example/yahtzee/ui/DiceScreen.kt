package com.example.yahtzee

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yahtzee.data.DiceImages
import com.example.yahtzee.ui.GameUiState
import com.example.yahtzee.ui.GameViewModel
import com.example.yahtzee.ui.theme.ButtonDisableable
import com.example.yahtzee.ui.theme.PointsScreen
import com.example.yahtzee.ui.theme.YahtzeeTheme



private const val TAG = "DiceScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiceScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    //tästä alas

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
            text = if (gameUiState.rounds == 0) stringResource(
                id = R.string.total_points_info,
                gameUiState.rollScores[15]
            )
            else stringResource(R.string.rolls_info, gameUiState.rerolls),
        )
        Spacer(modifier.height(12.dp))

        val results = gameUiState.results
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
                        .clickable(onClick = { gameViewModel.updateLockedDices(index) })
                ) {
                    Image(
                        painter = painterResource(id = DiceImages[item - 1]),
                        contentDescription = item.toString(),
                        modifier
                            .size(150.dp)
                            .background(
                                color =
                                if (gameUiState.lockedDices[index]) colorResource(id = R.color.gray_green)
                                else colorResource(id = R.color.light_brown),
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                    Spacer(modifier.height(15.dp))
                }
            }
        }
        // Fifth dice
        Row() {
            Box(
                modifier
                    .size(150.dp)
                    .clickable(onClick = { gameViewModel.updateLockedDices(4) })
            ) {
                Image(
                    painter = painterResource(id = DiceImages[fifth - 1]),
                    contentDescription = fifth.toString(),
                    modifier
                        .size(150.dp)
                        .background(
                            color =
                            if (gameUiState.lockedDices[4]) colorResource(id = R.color.gray_green)
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
                onButtonClicked = {
                    Log.d(TAG, "onButtonClicked openDialog")

                    //gameViewModel.openDialog = !gameViewModel.openDialog
                    gameViewModel.updateDialogState(!gameUiState.openDialog)
                },
                buttonText = stringResource(id = R.string.points_sheet)
            )
            // Roll (Next) button
            RollingButton(height, width, gameUiState, gameViewModel, results)
        }

        // Pop up fillable points screen
        PointsScreen(
            gameViewModel = GameViewModel(),
        )
    }
}

@Composable
fun RollingButton(height: Int, width: Int, gameUiState: GameUiState, gameViewModel: GameViewModel, results: List<Int>) {
    Log.d(TAG, "Enter roll button")
    if (gameUiState.rerolls <= 3 && gameUiState.pointsFilled) {
        // Next-button
        ButtonDisableable(
            height = height,
            width = width,
            onButtonClicked = { gameViewModel.newRoundActions() },
            buttonText = if (gameUiState.rounds == 0) stringResource(R.string.new_game)
            else stringResource(R.string.button_new_round)
        )
    } else {
        // Roll-button
        ButtonDisableable(
            height = height,
            width = width,
            enable = gameUiState.enableRoll,
            onButtonClicked = {
                Log.d(TAG, "Click roll")
                gameViewModel.roll()
                Log.d(TAG, "Exit roll, results ${results.toList()}") },
            buttonText = stringResource(id = R.string.roll)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun YahtzeeScreenPreview() {
    YahtzeeTheme() {
        DiceScreen()
    }
}
