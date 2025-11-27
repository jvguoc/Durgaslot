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


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SlotSoundManager.init(applicationContext)   // test init de so - potser afegir onDestroy
        NotificationUtils.createChannel(applicationContext) // test notificacio

        requestNotificationPermissionIfNeeded() // demanar permis per notifs abans de setContent

        setContent {
            MaterialTheme {
                val nav = rememberNavController()

                androidx.compose.material3.Scaffold(
                    topBar = {
                        androidx.compose.material3.CenterAlignedTopAppBar(
                            title = { androidx.compose.material3.Text("") }, // necessari?
                            actions = {
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
                                        val popped = nav.popBackStack(route = "menu", inclusive = false)
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
                                        val popped = nav.popBackStack(route = "menu", inclusive = false)
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
                                        val popped = nav.popBackStack(route = "menu", inclusive = false)
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

    // test demanar permis notificacio (API >=33)
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

}
