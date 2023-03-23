package com.example.yahtzee

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.yahtzee.ui.theme.YahtzeeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class YahtzeeUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun init(){
        composeTestRule.setContent {
            YahtzeeTheme {
                YahtzeeApp()
            }
        }
    }

    @Test
    fun canRoll() {
        for (i in 0..2){
            composeTestRule.onNodeWithText("Roll").performClick()
            composeTestRule.onNodeWithText("Rolls Left ${i+1}")
        }
        composeTestRule.onNodeWithText("Rolls Left 0")
    }

    @Test
    fun newRound(){
        composeTestRule.onNodeWithText("Roll").performClick()
        composeTestRule.onNodeWithText("Points Sheet").performClick()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithText("Accept").performClick()
        composeTestRule.onNodeWithText("New Round").performClick()
    }

    @Test
    fun mustFillPoints(){
        composeTestRule.onNodeWithText("Roll").performClick()
        composeTestRule.onNodeWithText("Points Sheet").performClick()
        composeTestRule.onNodeWithText("Accept").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("points_0").performClick()
        composeTestRule.onNodeWithText("Accept").assertIsEnabled()
    }

    @Test
    fun newGame(){
        var ind: Int
        for (index in 0..12) {
            ind = if (index > 5) index + 2 else index
            composeTestRule.onNodeWithText("Roll").performClick()
            composeTestRule.onNodeWithText("Points Sheet").performClick()
            composeTestRule.onNodeWithTag("points_$ind").performClick()
            composeTestRule.onNodeWithText("Accept").performClick()
            if (ind < 14) {
                composeTestRule.onNodeWithText("New Round").performClick()
            }
        }
        composeTestRule.onNodeWithText("New Game").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Game").performClick()
        composeTestRule.onNodeWithText("Points Sheet").performClick()
        for (i in 0..14){
            composeTestRule.onNodeWithTag("points_$i").assertTextEquals("","")
        }
    }

}