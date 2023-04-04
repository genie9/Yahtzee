package com.example.yahtzee.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.yahtzee.data.DiceImages

data class GameUiState(
   // Handling dices
    val results: MutableList<Int> = mutableStateListOf(1, 1, 1, 1, 1),
    val lockedDices: MutableList<Boolean> = mutableStateListOf(false, false, false, false, false),

    // Game play variables
    val rounds: Int = 13,
    val rerolls: Int = 3,
    val lastIndex: Int = -1,

    // Handling layout
    val enableRoll: Boolean = true,
    val enableAccept: Boolean = false,
    val pointsFilled: Boolean = false,
    val openDialog: Boolean = false,

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