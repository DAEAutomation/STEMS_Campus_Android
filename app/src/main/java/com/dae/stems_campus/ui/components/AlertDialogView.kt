package com.dae.stems_campus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun textTNoButtonAlert(
    onDismissRequest: () -> Unit,
    dialogTitle: String
) {
    AlertDialog(
        icon = {
        },
        title = {
            Text(text = dialogTitle, textAlign = TextAlign.Center, color = Color.White, style = MaterialTheme.typography.titleMedium)
        },

        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {

        },
        dismissButton = {

        },
        containerColor = Color(0xFF2D859D)
    )
}

@Composable
fun LoadingView(onDismiss:() -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
        ) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(12.dp)
            ) {

                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingView(title: String,onDismiss:() -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
        ) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    Modifier
                        .padding(8.dp), textAlign = TextAlign.Center, color = Color.Black
                )

                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
            }
        }
    }
}
