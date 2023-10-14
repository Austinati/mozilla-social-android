package org.mozilla.social.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.social.core.designsystem.theme.MoSoTheme

@Composable
fun MoSoCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = MoSoCheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

object MoSoCheckboxDefaults {
    @Composable
    fun colors(): CheckboxColors = CheckboxDefaults.colors(
        checkedColor = MoSoTheme.colors.iconActionActive,
        uncheckedColor = MoSoTheme.colors.iconActionDisabled,
        checkmarkColor = MoSoTheme.colors.textActionPrimary,
        disabledCheckedColor = MoSoTheme.colors.iconActionActive,
        disabledUncheckedColor = MoSoTheme.colors.iconActionDisabled,
        disabledIndeterminateColor = MoSoTheme.colors.textActionPrimary,
    )
}

@Preview
@Composable
private fun Preview() {
    MoSoTheme {
        MoSoSurface {
            MoSoCheckBox(checked = true, onCheckedChange = {})
        }
    }
}

@Preview
@Composable
private fun PreviewDarkMode() {
    MoSoTheme(
        darkTheme = true
    ) {
        MoSoSurface {
            MoSoCheckBox(checked = true, onCheckedChange = {})
        }
    }
}