package com.yahtzee.ui

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.yahtzee.data.DiceImages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random


private const val TAG = "GameViewModel"

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    val diceImage = DiceImages

    fun newGame() {
        _uiState.value = GameUiState()
        Log.i(TAG, "New Game started")
    }

    fun newRoundActions() {
        val lockedDices = _uiState.value.lockedDices
        if (_uiState.value.rounds == 0) {
            return newGame()
        }
        lockedDices.replaceAll { false }
        _uiState.update { currentState ->
            currentState.copy(
                lockedDices = lockedDices,
                rerolls = 3,
                lastIndex = -1,
                pointsAccepted = false,
                enableRoll = true
            )
        }
        roll()
        Log.i(TAG, "New Round started. Rounds left ${_uiState.value.rounds}")
    }

    @VisibleForTesting
    fun roll() {
        val randomGenerator = Random(System.currentTimeMillis())
        val results = _uiState.value.results
        var rerolls = _uiState.value.rerolls
        if (rerolls > 0) {
            if (!_uiState.value.lockedDices.contains(true)) {
                results.replaceAll { randomGenerator.nextInt(1, 7) }
            } else {
                for (i in 0..4) {
                    results[i] = if (_uiState.value.lockedDices[i]) results[i] else (1..6).random()
                }
            }
            rerolls = rerolls.minus(1)
            _uiState.update { currentState ->
                currentState.copy(
                    results = results,
                    rerolls = rerolls
                )
            }
        }
        if (rerolls == 0) {
            _uiState.update { currentState ->
                currentState.copy(enableRoll = false)
            }
        }
        Log.i(
            TAG,
            "Roll done, results: ${uiState.value.results.toList()}, " +
                    "rerolls: ${uiState.value.rerolls}"
        )
    }

    fun updateLockedDices(index: Int) {
        val lockedDices = _uiState.value.lockedDices
        if (_uiState.value.rerolls < 3) {
            lockedDices[index] = !lockedDices[index]
            _uiState.update { currentState ->
                currentState.copy(
                    lockedDices = lockedDices
                )
            }
        }
        Log.i(TAG, "Locked dices updated: ${_uiState.value.lockedDices.toList()}")
    }

    fun upperStats(index: Int): Int {
        val rollScores = _uiState.value.rollScores
        val points = _uiState.value.results.sumOf { if (it == index + 1) it else 0 }
        rollScores[index] = points
        val upperScores = rollScores.slice(0..5).toMutableList()
        val upperTotal = upperScores.sumOf { if (it != -1) it else 0 }
        rollScores[6] = upperTotal
        // check if bonus
        if (upperTotal >= 63) {
            rollScores[7] = 35
            Log.i(TAG, "Bonus added")
        }
        _uiState.update { currentState ->
            currentState.copy(
                rollScores = rollScores
            )
        }
        Log.i(TAG, "Upper Total updated: ${_uiState.value.rollScores[6]}")
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
            rollScores[index] = score
            rollScores[15] =
                rollScores.slice(6..14).toMutableList()
                    .sumOf { if (it != -1) it else 0 }

            if (score != -1) {
                _uiState.update { currentState ->
                    currentState.copy(
                        enableAccept = true,
                        lastIndex = index,
                        rollScores = rollScores
                    )
                }
                Log.i(TAG, "ACCEPT enabled")
            }
        } else {
            score = -1
        }
        Log.i(TAG, "Points filled: $score")
        return score
    }

    @VisibleForTesting
    fun acceptRound() {
        val rounds = _uiState.value.rounds
        val rollScoresLocked = _uiState.value.rollScoresLocked
        val lastIndex = _uiState.value.lastIndex
        if (lastIndex > -1 && _uiState.value.rollScores[lastIndex] != -1
            && rounds > 0
        ) {
            rollScoresLocked[lastIndex] = true
            _uiState.update { currentState ->
                currentState.copy(
                    rounds = rounds.minus(1),
                    rollScoresLocked = rollScoresLocked,
                    pointsAccepted = true,
                    lastIndex = -1,
                    enableAccept = false
                )
            }

        }
        Log.i(TAG, "Round accepted")
    }

    // TODO: Investigate why this works without updating state of rollScores
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
            Log.i(TAG, "Fillable: TRUE")
            return true
        }
        Log.i(TAG, "Fillable: FALSE")
        return false
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
