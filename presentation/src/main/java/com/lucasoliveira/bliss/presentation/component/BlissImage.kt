package com.lucasoliveira.bliss.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import com.lucasoliveira.bliss.presentation.theme.ButtonGray

@Composable
fun BlissImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val placeholder = ColorPainter(ButtonGray)
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        contentScale = contentScale,
        modifier = modifier,
    )
}

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
