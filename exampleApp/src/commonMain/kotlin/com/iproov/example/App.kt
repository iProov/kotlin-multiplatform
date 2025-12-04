// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iproov.example.exampleapp.generated.resources.Res
import com.iproov.example.exampleapp.generated.resources.iproov_logo
import com.iproov.example.viewmodel.ClaimViewModel
import com.iproov.kmp.api.IproovState
import com.iproov.kmp.api_client.AssuranceType
import com.iproov.kmp.api_client.ClaimType
import org.jetbrains.compose.resources.painterResource
import kotlin.math.pow


@Composable
fun App() {
    val viewModel = remember { ClaimViewModel() }

    var userID by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // iProov logo
            Image(
                painter = painterResource(Res.drawable.iproov_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(top = 12.dp, bottom = 20.dp)
            )

            // Text field for user ID
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                text = "User ID",
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                value = userID,
                onValueChange = { userID = it },
            )

            // Message output
            AnimatedVisibility(viewModel.message.value != null) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = viewModel.message.value ?: "",
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            // IProov state
            AnimatedVisibility(viewModel.iProovState.value != null) {
                IproovStateSurface(
                    state = viewModel.iProovState.value,
                    onClosed = viewModel::resetState
                )
            }

            // Actions
            Button(
                modifier = Modifier.padding(top = 25.dp),
                enabled = userID.isNotEmpty(),
                onClick = {
                    viewModel.launchIProov(ClaimType.ENROL, userID, AssuranceType.GENUINE_PRESENCE)
                }) {
                Text("Enrol with Genuine Presence")
            }

            Button(
                enabled = userID.isNotEmpty(),
                onClick = {
                    viewModel.launchIProov(ClaimType.ENROL, userID, AssuranceType.LIVENESS)
                }) {
                Text("Enrol with Liveness")
            }

            Button(
                enabled = userID.isNotEmpty(),
                onClick = {
                    viewModel.launchIProov(ClaimType.VERIFY, userID, AssuranceType.GENUINE_PRESENCE)
                }) {
                Text("Verify with Genuine Presence")
            }

            Text("Native SDK version: ${viewModel.sdkVersion()}")
        }
    }
}

fun formatDouble(value: Double): String {
    val multiplier = 10.0.pow(2)
    val roundedValue = kotlin.math.round(value * multiplier) / multiplier
    return roundedValue.toString()
}

@Composable
private fun IproovStateSurface(state: IproovState?, onClosed: () -> Unit) {
    val stateText = when (state) {
        is IproovState.Starting -> "Starting"
        is IproovState.Connecting -> "Connecting"
        is IproovState.Connected -> "Connected"
        is IproovState.Processing -> "Processing: ${formatDouble(state.progress)}"
        is IproovState.Canceled -> "Canceled by ${state.canceler}"
        is IproovState.Error -> "Error: ${state.exception.title}"
        is IproovState.Failure -> "Failure: ${state.failureResult.reasons.map { "[${it.feedbackCode} : ${it.description}]" }}"
        is IproovState.Success -> "Success"
        else -> ""
    }

    Surface(
        modifier = Modifier.padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp,
        tonalElevation = 6.dp
    ) {
        Box(Modifier.fillMaxWidth()) {

            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 30.dp),
                text = stateText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Close button
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onClosed() }
                    .padding(4.dp),
                imageVector = Icons.Default.Close,
                tint = Color.Red,
                contentDescription = null,
            )
        }
    }
}

