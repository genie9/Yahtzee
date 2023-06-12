package com.yahtzee

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.yahtzee.ui.theme.YahtzeeTheme
import com.yahtzee.YahtzeeApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class YahtzeeUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    val roll = context.resources.getString(R.string.roll)
    val total_points_info = context.resources.getString(R.string.total_points_info).dropLast(4)
    val rolls_info = context.resources.getString(R.string.rolls_info).dropLast(4)
    val points_sheet = context.resources.getString(R.string.points_sheet)
    val new_game = context.resources.getString(R.string.new_game)
    val new_round = context.resources.getString(R.string.button_new_round)
    val button_accept = context.resources.getString(R.string.button_accept)
    val button_back = context.resources.getString(R.string.button_back)

    @Before
    fun init() {
        composeTestRule.setContent {
            YahtzeeTheme {
                YahtzeeApp()
            }
        }
    }

    @Test
    fun canRollAndRollInfo() {
        val rerolls = listOf(3, 2, 1)
        for (i in rerolls) {
            composeTestRule.onNodeWithText("$rolls_info${i}").assertIsDisplayed()
            composeTestRule.onNodeWithText(roll).performClick()
        }
        composeTestRule.onNodeWithText("${rolls_info}0").assertIsDisplayed()
    }

    @Test
    fun newRound() {
        composeTestRule.onNodeWithText(roll).performClick()
        composeTestRule.onNodeWithText(points_sheet).performClick()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithText(button_accept).performClick()
        composeTestRule.onNodeWithText(new_round).performClick()
    }

    @Test
    fun mustFillRoundPointsOrAcceptDisabled() {
        composeTestRule.onNodeWithText(roll).performClick()
        composeTestRule.onNodeWithText(points_sheet).performClick()
        composeTestRule.onNodeWithText(button_accept).assertIsNotEnabled()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithText(button_accept).assertIsEnabled()
    }

    @Test
    fun onLastRoundRollDisabled() {
        for (i in 0..2) {
            composeTestRule.onNodeWithText(roll).performClick()
        }
        composeTestRule.onNodeWithText(roll).assertIsNotEnabled()

        // Visiting "points sheet" not affecting "Roll" button
        composeTestRule.onNodeWithText(points_sheet).performClick()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithText(button_back).performClick()
        composeTestRule.onNodeWithText(roll).assertIsNotEnabled()
    }

    @Test
    fun newGame() {
        var ind: Int
        var total = ""

        for (index in 0..12) {
            ind = if (index > 5) index + 2 else index
            composeTestRule.onNodeWithText(roll).performClick()
            composeTestRule.onNodeWithText(points_sheet).performClick()
            composeTestRule.onNodeWithTag("points_$ind").performClick()
            total = composeTestRule.onNodeWithTag("points_15").fetchSemanticsNode().config
                .getOrNull(Text)?.get(0).toString()
            composeTestRule.onNodeWithText(button_accept).performClick()
            if (ind < 14) {
                composeTestRule.onNodeWithText(new_round).performClick()
            }
        }

        composeTestRule.onNodeWithText("$total_points_info${total}").assertIsDisplayed()
        composeTestRule.onNodeWithText(new_game).assertIsDisplayed()
        composeTestRule.onNodeWithText(new_game).performClick()

        // Assert points table reinitialized after starting new game
        composeTestRule.onNodeWithText(points_sheet).performClick()
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
}