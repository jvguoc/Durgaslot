package com.durgasoft.slot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.durgasoft.slot.history.HistoryScreen
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.durgasoft.slot.NotificationUtils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
// Si HelpScreen está en com.durgasoft.slot:
import com.durgasoft.slot.HelpScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SlotSoundManager.init(applicationContext)
        NotificationUtils.createChannel(applicationContext)

        requestNotificationPermissionIfNeeded()
        requestCalendarPermissionsIfNeeded()
        requestLocationPermissionIfNeeded()


        setContent {
            MaterialTheme {
                val nav = rememberNavController()

                androidx.compose.material3.Scaffold(
                    topBar = {
                        androidx.compose.material3.CenterAlignedTopAppBar(
                            title = { androidx.compose.material3.Text("") },
                            actions = {
                                // BOTÓN AYUDA
                                androidx.compose.material3.IconButton(
                                    onClick = { nav.navigate("help") }
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Filled.Help,
                                        contentDescription = "Ayuda"
                                    )
                                }

                                // BOTÓN MÚSICA
                                MusicToggleButton()
                            }
                        )
                    }
                ) { pad ->
                    Surface(modifier = Modifier.padding(pad)) {
                        NavHost(
                            navController = nav,
                            startDestination = "menu"
                        ) {
                            composable("menu") {
                                MainMenu(
                                    onPlay = { nav.navigate("slot") },
                                    onTop = { nav.navigate("top") },
                                    onTabla = { nav.navigate("tabla") }
                                )
                            }

                            composable("slot") {
                                SlotApp(
                                    onBack = {
                                        val popped = nav.popBackStack(
                                            route = "menu",
                                            inclusive = false
                                        )
                                        if (!popped) {
                                            nav.navigate("menu") {
                                                launchSingleTop = true
                                                popUpTo("menu") { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }

                            composable("top") {
                                HistoryScreen(
                                    onBack = {
                                        val popped = nav.popBackStack(
                                            route = "menu",
                                            inclusive = false
                                        )
                                        if (!popped) {
                                            nav.navigate("menu") {
                                                launchSingleTop = true
                                                popUpTo("menu") { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }

                            composable("tabla") {
                                TableScreen(
                                    onBack = {
                                        val popped = nav.popBackStack(
                                            route = "menu",
                                            inclusive = false
                                        )
                                        if (!popped) {
                                            nav.navigate("menu") {
                                                launchSingleTop = true
                                                popUpTo("menu") { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }

                            // NUEVO: pantalla de ayuda (WebView)
                            composable("help") {
                                HelpScreen(
                                    onBack = {
                                        val popped = nav.popBackStack(
                                            route = "menu",
                                            inclusive = false
                                        )
                                        if (!popped) {
                                            nav.navigate("menu") {
                                                launchSingleTop = true
                                                popUpTo("menu") { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    // PERMISSOS
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    private fun requestCalendarPermissionsIfNeeded() {
        val perms = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )

        val missing = perms.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing) {
            ActivityCompat.requestPermissions(this, perms, 200)
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val missing = perms.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing) {
            ActivityCompat.requestPermissions(this, perms, 300)
        }
    }

}
