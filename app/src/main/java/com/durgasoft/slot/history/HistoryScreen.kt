@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.durgasoft.slot.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val vm: HistoryVM = viewModel(factory = HistoryVMFactory(ctx))
    val ui = vm.ui

    LaunchedEffect(Unit) { vm.loadTop7() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Top puntuaciones", fontWeight = FontWeight.ExtraBold) },
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
                .padding(16.dp)
        ) {
            when {
                ui.loading -> Text("Cargando...")
                ui.error != null -> Text("Error: ${ui.error}")
                else -> {
                    val fmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(ui.top) { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${index + 1}. ${item.maxChips}", fontWeight = FontWeight.Bold)
                                Text(fmt.format(Date(item.savedAtMillis)))
                            }
                        }
                    }
                    if (ui.top.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Sin puntuaciones todavía.")
                    }
                }
            }
        }
    }
}
