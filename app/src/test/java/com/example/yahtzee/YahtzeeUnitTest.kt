package com.example.yahtzee

import androidx.compose.runtime.mutableStateListOf
import org.junit.Test
import org.junit.Assert.*

class YahtzeeUnitTests {
    @Test
    fun rollingIfRerollsCountNotZero() {
        val rerollsTestList = listOf(1,2,3)
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf<Boolean>()
            .apply { addAll(List(5) { false }) }
        for (rerolls in rerollsTestList) {
            var copyInitResults = initResults.map{it}.toMutableList()
            val newResults = roll(rerolls, lockedDices, copyInitResults)
            assertTrue(newResults != initResults)
        }
    }

    @Test
    fun rollingIfRerollsCountZero() {
        val rerolls = 0
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf<Boolean>()
            .apply { addAll(List(5) { false }) }
        var copyInitResults = initResults.map{it}.toMutableList()
        val newResults = roll(rerolls, lockedDices, copyInitResults)
        assertTrue(newResults == initResults)
    }

    @Test
    fun rollingIfDiceIsLocked() {
        val rerolls = 3
        val initResults = MutableList(6) {0}
        val lockedDices = mutableStateListOf(true, true, false, false, false)
        var copyInitResults = initResults.map{it}.toMutableList()
        val newResults = roll(rerolls, lockedDices, copyInitResults)
        assertTrue(newResults.slice(0..1) == initResults.slice(0..1))
        assertTrue(newResults.slice(2..4) != initResults.slice(2..4))
    }
}