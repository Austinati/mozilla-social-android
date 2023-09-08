package org.mozilla.social.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.social.core.designsystem.icon.MoSoIcons
import org.mozilla.social.core.designsystem.theme.MozillaSocialTheme
import org.mozilla.social.model.StatusVisibility

@Composable
fun VisibilityDropDownButton(
    visibility: StatusVisibility,
    onVisibilitySelected: (StatusVisibility) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { expanded.value = true },
        border = BorderStroke(
            1.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
    ) {
        when (visibility) {
            StatusVisibility.Public -> ButtonContent(icon = MoSoIcons.Public, text = "Public")
            StatusVisibility.Unlisted -> ButtonContent(icon = MoSoIcons.LockOpen, text = "Unlisted")
            StatusVisibility.Private -> ButtonContent(icon = MoSoIcons.Lock, text = "Private")
            StatusVisibility.Direct -> ButtonContent(icon = MoSoIcons.Message, text = "Direct")
        }
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Icon(
            MoSoIcons.ArrowDropDown,
            "",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = {
            expanded.value = false
        }
    ) {
        DropDownItem(
            type = StatusVisibility.Public,
            icon = MoSoIcons.Public,
            text = "Public",
            expanded = expanded,
            onVisibilitySelected = onVisibilitySelected
        )
        DropDownItem(
            type = StatusVisibility.Unlisted,
            icon = MoSoIcons.LockOpen,
            text = "Unlisted",
            expanded = expanded,
            onVisibilitySelected = onVisibilitySelected
        )
        DropDownItem(
            type = StatusVisibility.Private,
            icon = MoSoIcons.Lock,
            text = "Private",
            expanded = expanded,
            onVisibilitySelected = onVisibilitySelected
        )
        DropDownItem(
            type = StatusVisibility.Direct,
            icon = MoSoIcons.Message,
            text = "Direct",
            expanded = expanded,
            onVisibilitySelected = onVisibilitySelected
        )
    }
}

@Composable
private fun ButtonContent(
    icon: ImageVector,
    text: String,
) {
    Icon(
        icon,
        "",
        tint = MaterialTheme.colorScheme.onSurface,
    )
    Spacer(modifier = Modifier.padding(start = 8.dp))
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun DropDownItem(
    type: StatusVisibility,
    icon: ImageVector,
    text: String,
    expanded: MutableState<Boolean>,
    onVisibilitySelected: (StatusVisibility) -> Unit,
    contentDescription: String = "",
) {
    DropdownMenuItem(
        text = {
            Row {
                Icon(icon, contentDescription)
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(text = text)
            }
        },
        onClick = {
            onVisibilitySelected(type)
            expanded.value = false
        }
    )
}

@Preview
@Composable
private fun VisibilityDropDownPreview() {
    MozillaSocialTheme(
        true
    ) {
        VisibilityDropDownButton(
            visibility = StatusVisibility.Private,
            onVisibilitySelected = {}
        )
    }
}