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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface {
                    val nav = rememberNavController()

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
