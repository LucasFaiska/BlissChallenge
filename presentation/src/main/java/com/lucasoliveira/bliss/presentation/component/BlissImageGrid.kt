package com.lucasoliveira.bliss.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import com.lucasoliveira.bliss.presentation.theme.White

private const val GRID_COLUMNS = 4

@Composable
fun <T> BlissImageGrid(
    items: List<T>,
    key: (T) -> Any,
    imageUrl: (T) -> String,
    contentDescription: (T) -> String,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    bordered: Boolean = false,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = items, key = key) { item ->
            val shape = RoundedCornerShape(if (bordered) 6.dp else 0.dp)
            BlissImage(
                url = imageUrl(item),
                contentDescription = contentDescription(item),
                contentScale = if (bordered) ContentScale.Fit else ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(shape)
                    .then(if (bordered) Modifier.border(BorderStroke(2.dp, White), shape) else Modifier)
                    .clickable { onItemClick(item) }
                    .padding(if (bordered) 4.dp else 0.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3F51B5, heightDp = 260)
@Composable
private fun BlissImageGridBorderedPreview() {
    BlissTheme {
        BlissImageGrid(
            items = (1..8).map { "emoji $it" },
            key = { it },
            imageUrl = { "" },
            contentDescription = { it },
            onItemClick = {},
            bordered = true,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3F51B5, heightDp = 260)
@Composable
private fun BlissImageGridPreview() {
    BlissTheme {
        BlissImageGrid(
            items = (1..6).map { "avatar $it" },
            key = { it },
            imageUrl = { "" },
            contentDescription = { it },
            onItemClick = {},
        )
    }
}
