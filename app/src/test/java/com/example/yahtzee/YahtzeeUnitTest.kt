package com.example.yahtzee

import androidx.compose.runtime.mutableStateListOf
import com.example.yahtzee.ui.GameViewModel
import org.junit.Test
import org.junit.Assert.*

class YahtzeeUnitTests {
    private val gameViewModel = GameViewModel()

    @Test
    fun rollingIfRerollsCountNotZero() {
        val rerollsTestList = listOf(1,2,3)
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf<Boolean>()
            .apply { addAll(List(5) { false }) }
        for (rerolls in rerollsTestList) {
            var copyInitResults = initResults.map{it}.toMutableList()
            val actualRerolls = gameViewModel.roll()
            assertTrue(copyInitResults != initResults)
            assertEquals(rerolls-1, actualRerolls)
        }
    }

    @Test
    // When rerolls count is 0 roll function must not have an effect on results or rerolls count
    fun rollingIfRerollsCountZero() {
        val rerolls = 0
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf<Boolean>()
            .apply { addAll(List(5) { false }) }
        var copyInitResults = initResults.map{it}.toMutableList()
        val actualRerolls = gameViewModel.roll()
        assertTrue(copyInitResults == initResults)
        assertEquals(0, actualRerolls)
    }

    @Test
    fun rollingIfDiceIsLocked() {
        val rerolls = 3
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf(true, true, false, false, false)
        var copyInitResults = initResults.map{it}.toMutableList()
        gameViewModel.roll()
        assertTrue(copyInitResults.slice(0..1) == initResults.slice(0..1))
        assertTrue(copyInitResults.slice(2..4) != initResults.slice(2..4))
    }
}