package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun AuditInspectionListScreen(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onBackClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Audit & Inspections")
    }
}

@Composable
fun PermitToWorkListScreen(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onBackClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Permit To Work")
    }
}

@Composable
fun ObservationListScreen(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onBackClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Observations")
    }
}

@Composable
fun IncidentListScreen(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onBackClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Incidents")
    }
}

@Composable
fun ViolationListScreen(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onBackClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Violations")
    }
}

@Composable
fun TrainingListScreen(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                onBackClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("Training")
    }
}