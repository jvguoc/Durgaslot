package com.durgasoft.slot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.durgasoft.slot.auth.AuthManager
import com.durgasoft.slot.auth.LoginScreen
import com.durgasoft.slot.history.HistoryScreen
import com.durgasoft.slot.global.GlobalTopScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

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

        Log.d("FIREBASE", "apps=${FirebaseApp.getApps(this).size}")
        Log.d("FIREBASE", "uid=${FirebaseAuth.getInstance().currentUser?.uid}")

        val auth = AuthManager(this)

        // veure on peta
        Log.d("BOOT", "ANTES setContent")

        setContent {

            // veure on peta
            android.util.Log.d("BOOT", "DENTRO setContent")

            MaterialTheme {
                val nav = rememberNavController()
                val route = nav.currentBackStackEntryAsState().value?.destination?.route
                val startDestination = if (auth.isSignedIn()) "menu" else "login"

                Scaffold(
                    topBar = {
                        if (route != "login") {
                            CenterAlignedTopAppBar(
                                title = { Text("") },
                                actions = {
                                    IconButton(onClick = { nav.navigate("global") }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Public,
                                            contentDescription = "Top Global"
                                        )
                                    }

                                    IconButton(onClick = { nav.navigate("help") }) {
                                        Icon(
                                            imageVector = Icons.Filled.Help,
                                            contentDescription = "Ayuda"
                                        )
                                    }

                                    MusicToggleButton()
                                }
                            )
                        }
                    }
                ) { pad ->
                    Surface(modifier = Modifier.padding(pad)) {
                        NavHost(
                            navController = nav,
                            startDestination = startDestination
                        ) {
                            composable("login") {
                                LoginScreen(
                                    auth = auth,
                                    onLoggedIn = {
                                        nav.navigate("menu") {
                                            popUpTo("login") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }

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
                                        nav.popBackStack("menu", false)
                                    }
                                )
                            }

                            composable("top") {
                                HistoryScreen(
                                    onBack = { nav.popBackStack("menu", false) }
                                )
                            }

                            composable("global") {
                                GlobalTopScreen(
                                    onBack = { nav.popBackStack("menu", false) }
                                )
                            }

                            composable("tabla") {
                                TableScreen(
                                    onBack = { nav.popBackStack("menu", false) }
                                )
                            }

                            composable("help") {
                                HelpScreen(
                                    onBack = { nav.popBackStack("menu", false) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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

        if (perms.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
        ) {
            ActivityCompat.requestPermissions(this, perms, 200)
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                300
            )
        }
    }
}
