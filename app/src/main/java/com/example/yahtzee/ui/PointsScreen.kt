package com.example.yahtzee.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yahtzee.R
import com.example.yahtzee.YahtzeeScreen
import com.example.yahtzee.data.ScoreNames


private const val TAG = "PointsScreen"

@Composable
fun PointsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    rollScores: MutableList<Int>,
    rollScoresLocked: MutableList<Boolean>,
    enableAccept: Boolean,
    pointsAccepted: Boolean,
    onPointCellClicked: (Int) -> Unit = {},
    onAcceptButtonClicked: () -> Unit = {},
) {

    val column1Weight = .6f // 60%
    val column2Weight = .4f // 40%
    val buttonHeight = 50
    val buttonWidth = 130
    val rollNames: List<String> = ScoreNames()

    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorResource(id = R.color.light_brown))
    ) {
        // Table titles
        item {
            Row(modifier.background(colorResource(id = R.color.gray_green))) {
                TableCell(
                    text = stringResource(id = R.string.column_rolls),
                    weight = column1Weight
                )
                TableCell(
                    text = stringResource(id = R.string.column_points),
                    weight = column2Weight
                )
            }
        }

        // Points-view Table
        items(rollNames) { rollName ->
            Row(Modifier.fillMaxWidth()) {
                val rollNameIndex = rollNames.indexOf(rollName)
                val rollScore = rollScores[rollNameIndex]
                TableCell(text = rollName, weight = column1Weight)
                TableCellClickable(
                    index = rollNameIndex,
                    weight = column2Weight,
                    rollScoreLocked = rollScoresLocked[rollNameIndex],
                    pointsAccepted = pointsAccepted,
                    onPointCellClicked = { onPointCellClicked(rollNameIndex) },
                    text = if (rollScore > -1) rollScore.toString() else ""
                )
            }
        }

        // Points-view buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Accept-button
                ButtonDisableable(
                    height = buttonHeight,
                    width = buttonWidth,
                    pad = 20,
                    enable = enableAccept,
                    onButtonClicked = onAcceptButtonClicked,
                    buttonText = stringResource(id = R.string.button_accept)
                )
                // Back-button
                ButtonDisableable(
                    height = buttonHeight,
                    width = buttonWidth,
                    pad = 20,
                    onButtonClicked = {
                        navController.navigate(YahtzeeScreen.Dices.name)
                    },
                    buttonText = stringResource(id = R.string.button_back)
                )
            }
        }
    }
}

@Composable
fun RowScope.TableCellClickable(
    index: Int,
    weight: Float,
    rollScoreLocked: Boolean,
    pointsAccepted: Boolean,
    onPointCellClicked: (Int) -> Unit,
    text: String,

    ) {
    Text(
        text = text,
        Modifier
            .border(1.dp, colorResource(id = R.color.dark_brown))
            .weight(weight)
            .height(32.dp)
            .padding(4.dp)
            .background(
                if (rollScoreLocked) colorResource(id = R.color.gray_green) else
                    colorResource(id = R.color.light_brown)
            )
            .testTag("points_$index")
            .clickable {
                if (!pointsAccepted) onPointCellClicked(index)
            },
        color = colorResource(id = R.color.dark_brown)
    )
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, colorResource(id = R.color.dark_brown))
            .weight(weight)
            .height(32.dp)
            .padding(4.dp)
            .background(colorResource(id = R.color.light_green)),
        color = colorResource(id = R.color.dark_brown)
    )
}