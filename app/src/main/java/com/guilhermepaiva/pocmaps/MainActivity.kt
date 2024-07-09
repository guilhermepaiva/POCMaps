package com.guilhermepaiva.pocmaps


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.guilhermepaiva.pocmaps.ui.theme.POCMapsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POCMapsTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Button(onClick = {
                                openGoogleMaps()
                            }) {
                                Text(text = "Open Google Maps")
                            }

                            Button(onClick = {
                                openAccessibilitySettings()
                            },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(text = "Enable Accessibility Service")
                            }
                        }

                    }

                }
            }
        }
    }

    private fun openGoogleMaps() {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=restaurants"))
//        intent.setPackage("com.google.android.apps.maps")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.google.android.apps.maps")
        }

        startActivity(intent)
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
}


