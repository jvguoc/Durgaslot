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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
}
