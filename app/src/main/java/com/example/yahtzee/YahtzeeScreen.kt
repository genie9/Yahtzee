package com.example.yahtzee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yahtzee.ui.GameViewModel
import com.example.yahtzee.ui.theme.PointsScreen

private const val TAG = "YahtzeeApp"

enum class YahtzeeScreen() {
    Dices,
    Points,
}

@Composable
fun YahtzeeApp(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Green)
    ) {
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = YahtzeeScreen.Points.name,
            modifier = modifier,
        ) {
            composable(route = YahtzeeScreen.Dices.name) {
                DiceScreen(navController = navController)
            }
            composable(route = YahtzeeScreen.Points.name) {
                PointsScreen(navController = navController)
            }
        }

    }
}