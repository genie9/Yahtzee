package com.example.yahtzee.ui

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.yahtzee.data.DiceImages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random


private const val TAG = "GameViewModel"

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    val diceImage = DiceImages

    init {
        _uiState.value.rollScores.apply { clear() }
            .apply { addAll(List(6) { -1 }) }
            .apply { addAll(6, listOf(0, 0)) }
            .apply { addAll(List(7) { -1 }) }
            .apply { addAll(15, listOf(0)) }
        Log.d(TAG, "rollScores ${_uiState.value.rollScores.toList()}")
        _uiState.value.rollScoresLocked.apply { clear() }
            .apply { addAll(List(6) { false }) }
            .apply { addAll(6, listOf(true, true)) }
            .apply { addAll(List(7) { false }) }
            .apply { addAll(15, listOf(true)) }
    }

    fun newRoundActions() {
        var rounds = _uiState.value.rounds.minus(1)
        roll()
        _uiState.value.lockedDices.replaceAll { false }
        // New Game actions
        if (rounds == 0) {
            rounds = 13
            _uiState.value.rollScores.apply { clear() }
                .apply { addAll(List(6) { -1 }) }
                .apply { addAll(6, listOf(0, 0)) }
                .apply { addAll(List(7) { -1 }) }
                .apply { addAll(15, listOf(0)) }

            _uiState.value.rollScoresLocked.apply { clear() }
                .apply { addAll(List(6) { false }) }
                .apply { addAll(6, listOf(true, true)) }
                .apply { addAll(List(7) { false }) }
                .apply { addAll(15, listOf(true)) }
        }
        updateGameState(
            enableRoll = true,
            rounds = rounds,
            pointsFilled = false,

            )
    }
    @VisibleForTesting
    fun roll() {
        Log.d(TAG, "Start Roll")
        val rerolls = _uiState.value.rerolls
        val results = _uiState.value.results
        if (rerolls > 0) {
            if (!_uiState.value.lockedDices.contains(true)) {
                results.replaceAll { Random.nextInt(1, 7) }
                Log.d(TAG, "Roll replace results ${results.toList()}")
            } else {
                for (i in 0..4) {
                    results[i] = if (_uiState.value.lockedDices[i]) results[i] else (1..6).random()
                }
            }
        }

        if (rerolls > 0) updateGameState(rerolls = rerolls.minus(1) )
        if (!_uiState.value.pointsFilled && _uiState.value.rerolls == 0) {
            updateGameState(enableRoll = false)
        }
        Log.d(TAG, "End Roll")
    }

    fun updateLockedDices(index: Int) {
        if (_uiState.value.rerolls < 3) {
            _uiState.value.lockedDices[index] = !_uiState.value.lockedDices[index]
        }
    }

    fun upperStats(index: Int): Int {
        val rollScores = _uiState.value.rollScores
        val points = _uiState.value.results.sumOf { if (it == index + 1) it else 0 }
        rollScores[index] = points
        val upperScores = rollScores.slice(0..5).toMutableList()
        val upperTotal = upperScores.sumOf { if (it != -1) it else 0 }
        rollScores[6] = upperTotal
        if (upperTotal >= 63) {
            rollScores[7] = 35
        }
        return points
    }

    fun fillPoints(index: Int): Int {
        val results = _uiState.value.results
        val rollScores = _uiState.value.rollScores
        val score: Int
        if (rollScores[index] == -1 && _uiState.value.rerolls < 3) {
            score = when (index) {
                in 0..5 -> upperStats(index)
                8 -> threeSame(results)
                9 -> fourSame(results)
                10 -> fullHouse(results)
                11 -> straight(1, results)
                12 -> straight(2, results)
                13 -> results.sum()
                14 -> {
                    if (results.all { results[0] == it }) 50 else 0
                }
                else -> -1
            }
            if (score != -1) {
                updateGameState(lastIndex = index, enableAccept = true)
            }
            rollScores[index] = score
            rollScores[15] =
                rollScores.slice(6..14).toMutableList()
                    .sumOf { if (it != -1) it else 0 }
            return score
        }
        return -1
    }

    @VisibleForTesting
    fun acceptRound() {
        val lastIndex = _uiState.value.lastIndex
        if (lastIndex > -1 && _uiState.value.rollScores[lastIndex] != -1
            && _uiState.value.rounds > 0
        ) {
            _uiState.value.rollScoresLocked[lastIndex] = true
            updateGameState(
                pointsFilled = true,
                lastIndex = -1,
                rounds = _uiState.value.rounds.minus(1),
                enableAccept = false)
            updateDialogState(!_uiState.value.openDialog)
        }
    }
    fun checkIfFillable(index: Int): Boolean {
        val rollScores = _uiState.value.rollScores
        val lastIndex = _uiState.value.lastIndex
        if (!_uiState.value.rollScoresLocked[index]) {
            if (lastIndex > -1) {
                if (lastIndex in 0..5) {
                    rollScores[6] = rollScores[6].minus(rollScores[lastIndex])
                }
                rollScores[lastIndex] = -1
            }
            return true
        }
        return false
    }

    @VisibleForTesting
    fun updateGameState(
        rounds: Int = _uiState.value.rounds,
        rerolls: Int = _uiState.value.rerolls,
        enableRoll: Boolean = _uiState.value.enableRoll,
        pointsFilled: Boolean = _uiState.value.pointsFilled,
        lastIndex: Int = _uiState.value.lastIndex,
        enableAccept: Boolean = _uiState.value.enableAccept,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                rerolls = rerolls,
                rounds = rounds,
                enableRoll = enableRoll,
                pointsFilled = pointsFilled,
                lastIndex = lastIndex,
                enableAccept = enableAccept
            )
        }
    }

    fun updateDialogState(openDialog: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(openDialog = !_uiState.value.openDialog)
        }
    }

    fun threeSame(results: List<Int>): Int {
        val sorted = results.sorted()
        for (i in 0..2) {
            if (sorted[i] == sorted[i + 1] && sorted[i + 1] == sorted[i + 2]) {
                return sorted[i] + sorted[i + 1] + sorted[i + 2]
            }
        }
        return 0
    }

    fun fourSame(results: List<Int>): Int {
        val sorted = results.sorted()
        for (i in 0..1) {
            if (sorted[i] == sorted[i + 1] && sorted[i + 1] == sorted[i + 2]
                && sorted[i + 2] == sorted[i + 3]
            ) {
                return sorted[i] + sorted[i + 1] + sorted[i + 2] + sorted[i + 3]
            }
        }
        return 0
    }

    fun fullHouse(results: List<Int>): Int {
        val sorted = results.sorted()
        if ((sorted[0] == sorted[1] && sorted[1] == sorted[2] && sorted[3] == sorted[4]) ||
            (sorted[0] == sorted[1] && sorted[2] == sorted[3] && sorted[3] == sorted[4])
        ) {
            return sorted.sum()
        }
        return 0
    }

    fun straight(first: Int, results: List<Int>): Int {
        val sorted = results.sorted()
        if (sorted[0] == first
            && sorted[1] == sorted[0] + 1
            && sorted[2] == sorted[1] + 1
            && sorted[3] == sorted[2] + 1
            && sorted[4] == sorted[3] + 1
        ) {
            return sorted.sum()
        }
        return 0
    }
}
