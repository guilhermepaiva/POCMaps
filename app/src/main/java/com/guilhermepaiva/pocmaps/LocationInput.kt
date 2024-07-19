package com.guilhermepaiva.pocmaps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LocationInput(
    title: String,
    latitude: String,
    onLatitudeChange: (String) -> Unit,
    longitude: String,
    onLongitudeChange: (String) -> Unit,
    editable: Boolean = true
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column {
        OutlinedTextField(
            value = latitude,
            onValueChange = onLatitudeChange,
            label = { Text("$title Latitude") },
            modifier = Modifier.padding(4.dp),
            readOnly = !editable
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = onLongitudeChange,
            label = { Text("$title Longitude") },
            modifier = Modifier.padding(4.dp),
            readOnly = !editable
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}