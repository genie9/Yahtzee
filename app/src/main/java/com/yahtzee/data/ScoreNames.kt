package com.yahtzee.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.yahtzee.R

@Composable
fun ScoreNames(): List<String> {
    val context = LocalContext.current
    return listOf(
        context.resources.getString(R.string.ones),
        context.resources.getString(R.string.twos),
        context.resources.getString(R.string.threes),
        context.resources.getString(R.string.fours),
        context.resources.getString(R.string.fives),
        context.resources.getString(R.string.sixes),
        context.resources.getString(R.string.upper_total),
        context.resources.getString(R.string.bonus),
        context.resources.getString(R.string.same_of_three),
        context.resources.getString(R.string.same_of_four),
        context.resources.getString(R.string.full_house),
        context.resources.getString(R.string.small_straight),
        context.resources.getString(R.string.big_straight),
        context.resources.getString(R.string.chance),
        context.resources.getString(R.string.yahtzee),
        context.resources.getString(R.string.total)
    )
}
