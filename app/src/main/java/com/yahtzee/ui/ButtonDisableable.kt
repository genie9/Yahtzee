package com.yahtzee.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yahtzee.R

@Composable
public fun ButtonDisableable(
    pad: Int = 0,
    height: Int,
    width: Int,
    fontSize: Int = 24,
    enable: Boolean = true,
    onButtonClicked: () -> Unit,
    buttonText: String,
    test_tag: String
) {
    Button(
        modifier = Modifier
            .padding(pad.dp)
            .height(height.dp)
            .width(width.dp)
            .testTag(test_tag),
        enabled = enable,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.light_green),
            disabledBackgroundColor = colorResource(id = R.color.light_gray_green),
            contentColor = colorResource(id = R.color.dark_brown),
            disabledContentColor = colorResource(id = R.color.gray_green)
        ),
        onClick = onButtonClicked
    )
    {
        Text(
            text = buttonText,
            fontSize = fontSize.sp,
            style = MaterialTheme.typography.body1
        )
    }
}
