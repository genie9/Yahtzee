package com.example.yahtzee

import androidx.compose.runtime.mutableStateListOf
import com.example.yahtzee.ui.GameViewModel
import org.junit.Test
import org.junit.Assert.*

class YahtzeeUnitTests {
    private val viewModel = GameViewModel()

    @Test
    fun rollingWorksIfRerollsCountNotZero() {
        var gameUiState = viewModel.uiState.value

        var initResults: MutableList<Int>
        var updatedResults: MutableList<Int>
        var updatedRerolls: Int

        for (expectedRerolls in listOf(3,2,1)) {
            initResults = gameUiState.results.map{it}.toMutableList()

            assertEquals(expectedRerolls, gameUiState.rerolls)

            viewModel.roll()

            gameUiState = viewModel.uiState.value
            updatedResults = gameUiState.results.map{it}.toMutableList()
            updatedRerolls = gameUiState.rerolls

            assertEquals(expectedRerolls-1, updatedRerolls)
            assertNotEquals(updatedResults, initResults)

        }
    }

    @Test
    // When rerolls count is 0 roll function must not have an effect on results or rerolls count
    fun rollingNotWorkingIfRerollsCountZero() {
        var gameUiState = viewModel.uiState.value
        gameUiState.rerolls.minus(3)
        var initResults = gameUiState.results.map{it}.toMutableList()
        var updatedResults: MutableList<Int>


        viewModel.roll()

        updatedResults = gameUiState.results
        val rerolls = gameUiState.rerolls

        //assertTrue(updatedResults == initResults)
        assertEquals(0, rerolls)
    }

    @Test
    fun rollingIfDiceIsLocked() {
        val rerolls = 3
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf(true, true, false, false, false)
        var copyInitResults = initResults.map{it}.toMutableList()
        viewModel.roll()
        assertTrue(copyInitResults.slice(0..1) == initResults.slice(0..1))
        assertTrue(copyInitResults.slice(2..4) != initResults.slice(2..4))
    }
}