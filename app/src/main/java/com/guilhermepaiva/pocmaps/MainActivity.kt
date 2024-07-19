package com.guilhermepaiva.pocmaps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.guilhermepaiva.pocmaps.ui.theme.POCMapsTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                        ) {

                            val context = LocalContext.current
                            var originLat by remember { mutableStateOf("") }
                            var originLng by remember { mutableStateOf("") }
                            var destinationLat by remember { mutableStateOf("") }
                            var destinationLng by remember { mutableStateOf("") }

                            var travelMode by remember { mutableStateOf("driving") }
                            var delayMs by remember { mutableStateOf("3000") }

                            var waypoints by remember { mutableStateOf(listOf<Pair<String, String>>()) }

                            val scrollState = rememberScrollState()
                            val travelModes = listOf("driving", "walking")

                            var locationPermissionGranted by remember { mutableStateOf(false) }

                            val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.RequestMultiplePermissions(),
                                onResult = { permissions ->
                                    locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                                    if (locationPermissionGranted) {
                                        getCurrentLocation { location ->
                                            originLat = location.latitude.toString()
                                            originLng = location.longitude.toString()
                                        }
                                    } else {
                                        Toast.makeText(context, "Location permission is required to fetch current location", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                            LaunchedEffect(Unit) {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    locationPermissionGranted = true
                                    getCurrentLocation { location ->
                                        originLat = location.latitude.toString()
                                        originLng = location.longitude.toString()
                                    }
                                } else {
                                    requestLocationPermissionLauncher.launch(
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    )
                                }
                            }


                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(0.dp, 56.dp, 0.dp, 56.dp)
                                    .verticalScroll(scrollState)
                            ) {


                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 0.dp, vertical = 8.dp)

                                ) {
                                    // Setup section
                                    Text(
                                        text = "Setup",
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    HorizontalDivider()

                                    Column {
                                        Text(text = "If you didn't enable accessibility service, pls do it: ")
                                        Button(
                                            onClick = { openAccessibilitySettings() },
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            Text(text = "Enable Accessibility Service")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    HorizontalDivider()

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Column {
                                        // Travel mode selection
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(bottom = 8.dp),

                                            ) {
                                            Text(
                                                text = "Select Travel Mode",
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            var expanded by remember { mutableStateOf(false) }
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(end = 8.dp),

                                                ) {
                                                Button(onClick = { expanded = true }) {
                                                    Text(text = travelMode.capitalize())
                                                }
                                                DropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = { expanded = false },
                                                    modifier = Modifier.padding(top = 8.dp),

                                                    ) {
                                                    travelModes.forEach { mode ->
                                                        DropdownMenuItem(
                                                            text = { Text(text = mode.capitalize()) },
                                                            onClick = {
                                                                travelMode = mode
                                                                expanded = false
                                                            })
                                                    }
                                                }
                                            }
                                        }


                                        Spacer(modifier = Modifier.height(16.dp))

                                        Row {
                                            // Delay input
                                            OutlinedTextField(
                                                value = delayMs,
                                                onValueChange = { delayMs = it },
                                                label = { Text("Delay (ms)") },
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }

                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                HorizontalDivider()

                                LocationInput(
                                    title = "Origin",
                                    latitude = originLat,
                                    onLatitudeChange = { originLat = it },
                                    longitude = originLng,
                                    onLongitudeChange = { originLng = it },
                                    editable = false
                                )

                                HorizontalDivider()

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.padding(bottom = 8.dp),

                                ) {
                                    Text(text = "Waypoints")

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Button(onClick = {
                                        waypoints = waypoints + ("" to "")
                                    }) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Waypoint")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Add Waypoint")
                                    }
                                }


                                waypoints.forEachIndexed { index, waypoint ->
                                    LocationInput(
                                        title = "Waypoint ${index + 1}",
                                        latitude = waypoint.first,
                                        onLatitudeChange = { newLat ->
                                            waypoints = waypoints.toMutableList().apply {
                                                this[index] = newLat to waypoints[index].second
                                            }
                                        },
                                        longitude = waypoint.second,
                                        onLongitudeChange = { newLng ->
                                            waypoints = waypoints.toMutableList().apply {
                                                this[index] = waypoints[index].first to newLng
                                            }
                                        }
                                    )
                                }


                                HorizontalDivider()

                                Spacer(modifier = Modifier.height(16.dp))

                                LocationInput(
                                    title = "Destination",
                                    latitude = destinationLat,
                                    onLatitudeChange = { destinationLat = it },
                                    longitude = destinationLng,
                                    onLongitudeChange = { destinationLng = it }
                                )

                                Button(onClick = {
                                    saveDelayToPreferences(delayMs.toLong())
                                    openGoogleMaps(
                                        originLat.ifEmpty { "-8.05605673523189" },
                                        originLng.ifEmpty { "-34.87147976615561" },
                                        waypoints.map { it.first.ifEmpty { "-8.055744411106742" } to it.second.ifEmpty { "-34.87183456642819" } },
                                        destinationLat.ifEmpty { "-8.058123948450481" },
                                        destinationLng.ifEmpty { "-34.872231533348504" },
                                        travelMode
                                    )
                                },
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth()


                                    ) {
                                    Text(text = "Open Google Maps")
                                }


                            }
                        }

                    }

                }
            }
        }
    }

    private fun openGoogleMaps(
        originLat: String = "-8.05605673523189",
        originLng: String = "-34.87147976615561",
        waypoints: List<Pair<String, String>>,
        destinationLat: String = "-8.058123948450481",
        destinationLng: String = "-34.872231533348504",
        travelMode: String = "driving"
    ) {
        val origin = "$originLat,$originLng"
        val waypointStr = waypoints.joinToString("|") { "${it.first},${it.second}" }
        val destination = "$destinationLat,$destinationLng"
        val uri = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&waypoints=$waypointStr&travelmode=$travelMode"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(intent)
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun saveDelayToPreferences(delay: Long) {
        val sharedPref = getSharedPreferences("com.guilhermepaiva.pocmaps", MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putLong("delay_ms", delay)
            apply()
        }
    }

    private fun getCurrentLocation(onLocationReceived: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(it)
            } ?: run {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}


