@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.durgasoft.slot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reward Table", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            PrizeRow("Triple 7⃣", "100 chips")
            PrizeRow("Triple 🍓", "50 chips")
            PrizeRow("Triple 🍇", "40 chips")
            PrizeRow("Triple 🍒", "30 chips")
            PrizeRow("Triple 🍋", "20 chips")
            PrizeRow("Triple 🎰", "10 chips")

            Spacer(Modifier.height(10.dp))

            PrizeRow("2x 7⃣ + 🎰", "40 chips")
            PrizeRow("2x 🍓 + 🎰", "30 chips")
            PrizeRow("2x 🍇 + 🎰", "20 chips")
            PrizeRow("2x 🍒 + 🎰", "10 chips")
            PrizeRow("2x 🍋 + 🎰", "5 chips")
            PrizeRow("2x 🎰 + any", "1 chip")
        }
    }
}

@Composable
fun PrizeRow(combination: String, reward: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            combination,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            reward,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
