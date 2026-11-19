package com.lucasoliveira.bliss.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import com.lucasoliveira.bliss.presentation.theme.ButtonGray

@Composable
fun BlissImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    onLoadingChange: (Boolean) -> Unit = {},
) {
    var hasFailed by remember(url) { mutableStateOf(false) }

    if (hasFailed) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.testTag(BROKEN_IMAGE_TEST_TAG)) {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = contentDescription ?: stringResource(R.string.image_load_error),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxSize(BROKEN_IMAGE_FRACTION),
            )
        }
    } else {
        val previewPlaceholder = if (LocalInspectionMode.current) ColorPainter(ButtonGray) else null
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            placeholder = previewPlaceholder,
            contentScale = contentScale,
            onLoading = { onLoadingChange(true) },
            onSuccess = { onLoadingChange(false) },
            onError = {
                onLoadingChange(false)
                hasFailed = true
            },
            modifier = modifier,
        )
    }
}

const val BROKEN_IMAGE_TEST_TAG = "broken_image"

private const val BROKEN_IMAGE_FRACTION = 0.6f

@Preview(showBackground = true, backgroundColor = 0xFF3F51B5)
@Composable
private fun BlissImagePreview() {
    BlissTheme {
        BlissImage(
            url = "https://github.githubassets.com/images/icons/emoji/unicode/1f44d.png?v8",
            contentDescription = null,
            modifier = Modifier.size(96.dp),
        )
    }
}
