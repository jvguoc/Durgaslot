@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.durgasoft.slot.global

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GlobalTopScreen(
    onBack: () -> Unit
) {
    val vm: GlobalTopVM = viewModel()
    val ui = vm.ui

    LaunchedEffect(Unit) {
        vm.load()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Top Global", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (ui.loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (ui.items.isEmpty()) {
                Text("Aún no hay puntuaciones.")
            } else {
                ui.items.forEachIndexed { idx, it ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#${idx + 1}", fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(56.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(it.name ?: "Desconocido", fontWeight = FontWeight.Bold)
                                Text("Max fichas: ${it.maxChips ?: 0}")
                            }
                        }
                    }
                }
            }

            OutlinedButton(onClick = { vm.load() }) {
                Text("Recargar")
            }
        }
    }
}
