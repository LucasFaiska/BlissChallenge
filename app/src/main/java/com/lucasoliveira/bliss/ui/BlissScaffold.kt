package com.lucasoliveira.bliss.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lucasoliveira.bliss.presentation.R
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarController

@Composable
fun BlissScaffold(
    snackbarController: SnackbarController,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val genericError = stringResource(R.string.error_generic)

    LaunchedEffect(snackbarController) {
        snackbarController.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.message ?: genericError,
                actionLabel = message.actionLabel,
                withDismissAction = true,
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        content = content,
    )
}
