package com.yahtzee.ui

import androidx.compose.runtime.mutableStateListOf

data class GameUiState(
    // Handling dices
    val results: MutableList<Int> = mutableListOf(1, 1, 1, 1, 1),
    val lockedDices: MutableList<Boolean> = mutableStateListOf(false, false, false, false, false),

    // Game play variables
    val rounds: Int = 13,
    val rerolls: Int = 3,
    val lastIndex: Int = -1,

    // Handling layout
    val pointsAccepted: Boolean = false,
    val enableRoll: Boolean = true,
    val enableAccept: Boolean = false,

    // Handling points
    val rollScores: MutableList<Int> =
        mutableStateListOf<Int>()
            .apply { addAll(List(6) { -1 }) }
            .apply { addAll(6, listOf(0, 0)) }
            .apply { addAll(List(7) { -1 }) }
            .apply { addAll(15, listOf(0)) },
    val rollScoresLocked: MutableList<Boolean> =
        mutableListOf<Boolean>()
            .apply { addAll(List(6) { false }) }
            .apply { addAll(6, listOf(true, true)) }
            .apply { addAll(List(7) { false }) }
            .apply { addAll(15, listOf(true)) },
)