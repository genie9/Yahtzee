package com.example.yahtzee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yahtzee.R
import com.example.yahtzee.ui.theme.YahtzeeTheme


@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onNewGameClicked: () -> Unit = {},
    onResumeClicked: () -> Unit = {},
    onExitButtonClicked: () -> Unit = {},
) {
    Column(
        modifier
            .background(colorResource(id = R.color.light_brown))
            .fillMaxSize()
            .padding(20.dp, 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            fontSize = 40.sp,
            style = MaterialTheme.typography.body1,
            color = colorResource(id = R.color.dark_brown),
            text = "ZEN YAHTZEE"
        )
        Image(
            painter = painterResource(id = R.drawable.stylized_flower_silhouette),
            contentDescription = "lotus"
        )
        Column(
            modifier
                .background(colorResource(id = R.color.light_brown))
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val height = 60
            val width = 240
            ButtonDisableable(
                height = height,
                width = width,
                fontSize = 22,
                onButtonClicked = onNewGameClicked,
                buttonText = stringResource(
                    id = R.string.start_new_game
                )
            )
            Spacer(modifier.height(20.dp))
            ButtonDisableable(
                height = height,
                width = width,
                fontSize = 22,
                onButtonClicked = onResumeClicked,
                buttonText = stringResource(
                    id = R.string.resume_game
                )
            )
            Spacer(modifier.height(20.dp))
            ButtonDisableable(
                height = height,
                width = width,
                onButtonClicked = onExitButtonClicked,
                buttonText = stringResource(
                    id = R.string.exit
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    YahtzeeTheme {
        MainMenuScreen()
    }
}