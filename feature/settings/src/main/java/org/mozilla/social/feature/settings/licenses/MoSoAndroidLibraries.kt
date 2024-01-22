package org.mozilla.social.feature.settings.licenses

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.util.StableLibrary
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.mikepenz.aboutlibraries.ui.compose.util.stable
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Displays all provided libraries in a simple list.
 */
@Composable
fun MoSoLibrariesContainer(
    modifier: Modifier = Modifier,
    librariesBlock: (Context) -> Libs = { context ->
        Libs.Builder().withContext(context).build()
    },
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    colors: MoSoLibraryColors = MoSoLibraryDefaults.moSoLibraryColors(),
    padding: MoSoLibraryPadding = MoSoLibraryDefaults.moSoLibraryPadding(),
    itemContentPadding: PaddingValues = LibraryDefaults.ContentPadding,
    itemSpacing: Dp = MoSoLibraryDefaults.LibraryItemSpacing,
    header: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((StableLibrary) -> Unit)? = null,
) {
    val context = LocalContext.current

    val libraries = produceState<Libs?>(null) {
        value = withContext(Dispatchers.IO) {
            librariesBlock(context)
        }
    }
    MoSoLibrariesContainer(
        libraries.value?.stable,
        modifier,
        lazyListState,
        contentPadding,
        showAuthor,
        showVersion,
        showLicenseBadges,
        colors,
        padding,
        itemContentPadding,
        itemSpacing,
        header,
        onLibraryClick,
        licenseDialogBody = { library ->
            HtmlText(
                html = library.library.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                color = colors.contentColor,
            )
        }
    )
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    color: Color = LibraryDefaults.libraryColors().contentColor,
) {
    AndroidView(modifier = modifier, factory = { context ->
        TextView(context).apply {
            setTextColor(color.toArgb())
        }
    }, update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) })
}

//@Preview("Library items (Default)")
//@Composable
//fun PreviewLibraries() {
//    MaterialTheme {
//        Surface {
//            com.mikepenz.aboutlibraries.ui.compose.Libraries()
//        }
//    }
//}
//
//
//@Preview("Library items (Off)")
//@Composable
//fun PreviewLibrariesOff() {
//    MaterialTheme {
//        Surface {
//            com.mikepenz.aboutlibraries.ui.compose.Libraries(
//                fakeData.libraries.stable,
//                showAuthor = false,
//                showLicenseBadges = false
//            )
//        }
//    }
//}
//
//@Preview("Library item")
//@Composable
//fun PreviewLibrary() {
//    MaterialTheme {
//        Surface {
//            com.mikepenz.aboutlibraries.ui.compose.Library(
//                fakeData.libraries.first().stable
//            ) {
//                // on-click
//            }
//        }
//    }
//}
