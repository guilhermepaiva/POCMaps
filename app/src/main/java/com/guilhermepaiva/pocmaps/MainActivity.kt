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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

                            var originLat by remember { mutableStateOf("") }
                            var originLng by remember { mutableStateOf("") }
                            var waypointLat by remember { mutableStateOf("") }
                            var waypointLng by remember { mutableStateOf("") }
                            var destinationLat by remember { mutableStateOf("") }
                            var destinationLng by remember { mutableStateOf("") }

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                LocationInput(
                                    title = "Origin",
                                    latitude = originLat,
                                    onLatitudeChange = { originLat = it },
                                    longitude = originLng,
                                    onLongitudeChange = { originLng = it }
                                )

                                LocationInput(
                                    title = "Waypoint",
                                    latitude = waypointLat,
                                    onLatitudeChange = { waypointLat = it },
                                    longitude = waypointLng,
                                    onLongitudeChange = { waypointLng = it }
                                )

                                LocationInput(
                                    title = "Destination",
                                    latitude = destinationLat,
                                    onLatitudeChange = { destinationLat = it },
                                    longitude = destinationLng,
                                    onLongitudeChange = { destinationLng = it }
                                )

                                Button(onClick = {
                                    openGoogleMaps(
                                        originLat.ifEmpty { "-8.05605673523189" },
                                        originLng.ifEmpty { "-34.87147976615561" },
                                        waypointLat.ifEmpty { "-8.055744411106742" },
                                        waypointLng.ifEmpty { "-34.87183456642819" },
                                        destinationLat.ifEmpty { "-8.058123948450481" },
                                        destinationLng.ifEmpty { "-34.872231533348504" }

                                    )
                                }) {
                                    Text(text = "Open Google Maps")
                                }

                                Button(
                                    onClick = {
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
    }

    private fun openGoogleMaps(originLat: String = "-8.05605673523189", originLng: String = "-34.87147976615561", waypointLat: String = "-8.055744411106742", waypointLng: String = "-34.87183456642819", destinationLat: String = "-8.058123948450481", destinationLng: String = "-34.872231533348504") {
        val origin = "$originLat,$originLng"
        val waypoint = "$waypointLat,$waypointLng"
        val destination = "$destinationLat,$destinationLng"
        val uri = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&waypoints=$waypoint"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(intent)
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
}


