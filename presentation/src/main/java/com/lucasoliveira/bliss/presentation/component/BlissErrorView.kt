package com.lucasoliveira.bliss.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.theme.BlissTheme

@Composable
fun BlissErrorView(
    title: String,
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        BlissActionButton(text = retryLabel, onClick = onRetry)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3F51B5)
@Composable
private fun BlissErrorViewPreview() {
    BlissTheme {
        BlissErrorView(
            title = stringResource(R.string.error_title),
            message = "API rate limit reached. Try again at 14:32",
            retryLabel = stringResource(R.string.error_retry),
            onRetry = {},
        )
    }
}
