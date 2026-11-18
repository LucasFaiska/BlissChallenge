package com.lucasoliveira.bliss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.lucasoliveira.bliss.navigation.BlissNavDisplay
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarController
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import com.lucasoliveira.bliss.ui.BlissScaffold
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var snackbarController: SnackbarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlissTheme {
                BlissScaffold(snackbarController = snackbarController) { padding ->
                    BlissNavDisplay(
                        modifier = Modifier
                            .padding(padding)
                            .consumeWindowInsets(padding),
                    )
                }
            }
        }
    }
}
