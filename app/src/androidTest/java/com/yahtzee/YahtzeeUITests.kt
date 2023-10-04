package com.yahtzee

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.yahtzee.ui.theme.YahtzeeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class YahtzeeUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    val total_points_info = context.resources.getString(R.string.total_points_info)
    val rolls_info = context.resources.getString(R.string.rolls_info).dropLast(4)

    // helpers functions
    private fun startGame() {
        composeTestRule.onNodeWithTag("new_game_button").performClick()
    }

    private fun playOneRound(): String {
        composeTestRule.onNodeWithTag("roll_button").performClick()
        composeTestRule.onNodeWithTag("points_sheet_button").performClick()
        composeTestRule.onNodeWithTag("points_0").performClick()
        val points = composeTestRule.onNodeWithTag("points_0").fetchSemanticsNode().config
            .getOrNull(Text)?.get(0).toString()
        composeTestRule.onNodeWithTag("accept_button").performClick()

        return points
    }

    private fun playAllRounds(): String {
        var rounds: Int
        var total = ""

        for (ind in 0..12) {
            rounds = if (ind > 5) ind + 2 else ind
            composeTestRule.onNodeWithTag("roll_button").performClick()
            composeTestRule.onNodeWithTag("points_sheet_button").performClick()
            composeTestRule.onNodeWithTag("points_$rounds").performClick()
            total = composeTestRule.onNodeWithTag("points_15").fetchSemanticsNode().config
                .getOrNull(Text)?.get(0).toString()
            composeTestRule.onNodeWithTag("accept_button").performClick()
            if (rounds < 14) {
                composeTestRule.onNodeWithTag("next_round_button").performClick()
            }
        }
        return total
    }

    @Before
    fun init() {
        composeTestRule.setContent {
            YahtzeeTheme {
                YahtzeeApp()
            }
        }
    }

    @Test
    fun mainScreenHasAllElements() {
        composeTestRule.onNodeWithText("ZEN YAHTZEE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("new_game_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("resume_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exit_button").assertIsDisplayed()
    }

    @Test
    fun startingGameWorks() {
        composeTestRule.onNodeWithTag("new_game_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("new_game_button").performClick()
        composeTestRule.onNodeWithText("${rolls_info}3").assertIsDisplayed()
    }

    @Test
    fun canRollAndRollInfo() {
        val rerolls = listOf(3, 2, 1)

        startGame()

        for (i in rerolls) {
            composeTestRule.onNodeWithText("$rolls_info${i}").assertIsDisplayed()
            composeTestRule.onNodeWithTag("roll_button").performClick()
        }
        composeTestRule.onNodeWithText("${rolls_info}0").assertIsDisplayed()
    }

    @Test
    fun nextRoundWorks() {
        startGame()
        playOneRound()
        composeTestRule.onNodeWithTag("next_round_button").performClick()
        composeTestRule.onNodeWithText("${rolls_info}2").assertIsDisplayed()
    }

    @Test
    fun mustFillRoundPointsOrAcceptButtonDisabled() {
        startGame()
        composeTestRule.onNodeWithTag("roll_button").performClick()
        composeTestRule.onNodeWithTag("points_sheet_button").performClick()
        composeTestRule.onNodeWithTag("accept_button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithTag("accept_button").assertIsEnabled()
    }

    @Test
    fun onLastRoundRollDisabled() {
        startGame()
        for (i in 0..2) {
            composeTestRule.onNodeWithTag("roll_button").performClick()
        }
        composeTestRule.onNodeWithTag("roll_button").assertIsNotEnabled()

        // Visiting "points sheet" not affecting "Roll" button
        composeTestRule.onNodeWithTag("points_sheet_button").performClick()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.onNodeWithTag("roll_button").assertIsNotEnabled()
    }

    @Test
    fun gameTotalIsCorrect() {
        startGame()
        val total = playAllRounds()
        composeTestRule.onNodeWithText("$total_points_info").assertIsDisplayed()
        composeTestRule.onNodeWithText(total).assertIsDisplayed()
    }

    @Test
    fun newGameHasCleanPointsSheet() {
        startGame()
        playAllRounds()

        composeTestRule.onNodeWithTag("menu_burger").performClick()
        composeTestRule.onNodeWithTag("new_game_button").performClick()

        // Assert points table reinitialized after starting new game
        composeTestRule.onNodeWithTag("points_sheet_button").performClick()
        for (i in 0..5) {
            composeTestRule.onNodeWithTag("points_$i").assertTextEquals("", "")
        }
        for (i in 8..14) {
            composeTestRule.onNodeWithTag("points_$i").assertTextEquals("", "")
        }
        for (i in 6..7) {
            composeTestRule.onNodeWithTag("points_$i").assertTextEquals("0", "0")
        }
        composeTestRule.onNodeWithTag("points_15").assertTextEquals("0", "0")
    }

    @Test
    fun resumeGameWorks() {
        startGame()
        val points = playOneRound()

        composeTestRule.onNodeWithTag("menu_burger").performClick()
        composeTestRule.onNodeWithTag("resume_button").performClick()
        composeTestRule.onNodeWithTag("points_sheet_button").performClick()
        composeTestRule.onNodeWithTag("points_0").assertTextEquals(points, points)
    }

}