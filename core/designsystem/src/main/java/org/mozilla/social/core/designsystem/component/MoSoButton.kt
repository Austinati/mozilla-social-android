package org.mozilla.social.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.social.core.designsystem.theme.MoSoTheme

@Composable
fun MoSoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = MoSoButtonPrimaryDefaults.colors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MoSoButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = MoSoButtonSecondaryDefaults.colors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = BorderStroke(
        width = 1.dp,
        brush = SolidColor(MoSoTheme.colors.borderPrimary)
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    MoSoButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

object MoSoButtonPrimaryDefaults {
    @Composable
    fun colors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MoSoTheme.colors.layerAccent,
        contentColor = MoSoTheme.colors.textActionPrimary,
        disabledContainerColor = MoSoTheme.colors.layer2,
        disabledContentColor = MoSoTheme.colors.textActionSecondary,
    )
}

object MoSoButtonSecondaryDefaults {
    @Composable
    fun colors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MoSoTheme.colors.layer1,
        contentColor = MoSoTheme.colors.textActionSecondary,
        disabledContainerColor = MoSoTheme.colors.layer2,
        disabledContentColor = MoSoTheme.colors.textActionSecondary,
    )
}

@Preview
@Composable
private fun ButtonPreview() {
    MoSoTheme {
        Column {
            MoSoButton(onClick = { /*TODO*/ }) {
                Text(text = "Primary")
            }
            MoSoButtonSecondary(onClick = { /*TODO*/ }) {
                Text(text = "Secondary")
            }
        }
    }
}