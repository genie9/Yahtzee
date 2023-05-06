package com.example.yahtzee

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.ui.DiceScreen
import com.example.yahtzee.ui.GameViewModel
import com.example.yahtzee.ui.MainMenuScreen
import com.example.yahtzee.ui.PointsScreen

private const val TAG = "YahtzeeApp"

enum class YahtzeeScreen {
    MainMenu,
    Dices,
    Points,
}

@SuppressLint("VisibleForTests")
@Composable
fun YahtzeeApp(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    // TODO: state recovery
/*    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = YahtzeeScreen.valueOf(
        backStackEntry?.destination?.route ?: YahtzeeScreen.Dices.name
    )*/

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Green)
    ) {
        val uiState by gameViewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = YahtzeeScreen.MainMenu.name,
            modifier = modifier,
        ) {
            composable(route = YahtzeeScreen.MainMenu.name) {
                Log.i(TAG, "Navigate to MainMenuScreen")
                val activity = (LocalContext.current as? Activity)
                MainMenuScreen(
                    onNewGameClicked = {
                        gameViewModel.newGame()
                        navigate(navController, "dices")
                    },
                    onResumeClicked = { navController.navigate(YahtzeeScreen.Dices.name) },
                    onExitButtonClicked = { activity?.finish() }
                )
            }
            composable(route = YahtzeeScreen.Dices.name) {
                Log.i(TAG, "Navigate to DiceScreen")
                DiceScreen(
                    results = uiState.results,
                    lockedDices = uiState.lockedDices,
                    rerolls = uiState.rerolls,
                    rounds = uiState.rounds,
                    enableRoll = uiState.enableRoll,
                    pointsAccepted = uiState.pointsAccepted,
                    rollScores = uiState.rollScores,
                    onNavButtonClicked = { navigate(navController, it) },
                    onDiceClick = { gameViewModel.updateLockedDices(it) },
                    onNextButtonClick = { gameViewModel.newRoundActions() },
                    onRollClicked = { gameViewModel.roll() }
                )
            }
            composable(route = YahtzeeScreen.Points.name) {
                Log.i(TAG, "Navigate to PointsScreen")
                PointsScreen(
                    rollScoresLocked = uiState.rollScoresLocked,
                    enableAccept = uiState.enableAccept,
                    pointsAccepted = uiState.pointsAccepted,
                    rollScores = uiState.rollScores,
                    onPointCellClicked = { index: Int ->
                        if (gameViewModel.checkIfFillable(index))
                            gameViewModel.fillPoints(index)
                    },
                    onAcceptButtonClicked = {
                        gameViewModel.acceptRound()
                        navigate(navController, "dices")
                    },
                    onNavButtonClicked = { navigate(navController, it) })
            }
        }
    }
}

fun navigate(navController: NavController, routeName: String) {
    when (routeName) {
        "dices" -> navController.navigate(YahtzeeScreen.Dices.name)
        "points" -> navController.navigate(YahtzeeScreen.Points.name)
        "menu" -> navController.navigate(YahtzeeScreen.MainMenu.name)
    }
}