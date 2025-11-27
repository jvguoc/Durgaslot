@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.durgasoft.slot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.math.roundToInt
import androidx.core.view.drawToBitmap
import com.durgasoft.slot.GalleryUtils
import com.durgasoft.slot.NotificationUtils

@Composable
fun SlotApp(
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val view = LocalView.current
    val vm: SlotViewModel = viewModel(factory = SlotVMFactory(ctx))
    val ui = vm.ui

    val n = vm.symbols.size
    val reelA = remember { Animatable(ui.currentReels[0].toFloat(), Float.VectorConverter) }
    val reelB = remember { Animatable(ui.currentReels[1].toFloat(), Float.VectorConverter) }
    val reelC = remember { Animatable(ui.currentReels[2].toFloat(), Float.VectorConverter) }
    val scope = rememberCoroutineScope()

    fun idx(a: Animatable<Float, *>) = ((floor(a.value) % n + n) % n).toInt()

    suspend fun spinOne(a: Animatable<Float, *>, target: Int, rounds: Int, dur: Int) {
        val start = a.value
        val aligned = start - (start % 1f)
        val end = aligned + rounds * n + target
        a.animateTo(end, tween(dur, easing = FastOutSlowInEasing))
        a.snapTo(end.roundToInt().toFloat())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.slot_title),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al menú"
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReelBox(
                        symbol = vm.symbols[idx(reelA)],
                        spinning = ui.spinning,
                        modifier = Modifier.weight(1f)
                    )
                    ReelBox(
                        symbol = vm.symbols[idx(reelB)],
                        spinning = ui.spinning,
                        modifier = Modifier.weight(1f)
                    )
                    ReelBox(
                        symbol = vm.symbols[idx(reelC)],
                        spinning = ui.spinning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "${stringResource(R.string.chips)}: ${ui.chips}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text("${stringResource(R.string.max)}: ${ui.maxChips}", fontSize = 14.sp)
            Text("${stringResource(R.string.last_prize)}: ${ui.lastWin}", fontSize = 14.sp)
            Text("Resultado: ${ui.lastCombo}")

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = vm.canSpin(),
                    onClick = {
                        val t = vm.requestSpin()
                        if (t.isEmpty()) return@Button

                        SlotSoundManager.playSpin()

                        scope.launch {
                            spinOne(reelA, t[0], 20, 800)
                            spinOne(reelB, t[1], 24, 1050)
                            spinOne(reelC, t[2], 28, 1300)

                            val finalTargets = listOf(idx(reelA), idx(reelB), idx(reelC))
                            vm.finalizeSpin(finalTargets)

                            val prize = vm.ui.lastWin
                            if (prize > 0) {
                                NotificationUtils.showWinNotification(
                                    context = ctx,
                                    prize = prize,
                                    chips = vm.ui.chips,
                                    maxChips = vm.ui.maxChips
                                )

                                // capturar pantalla
                                val bitmap = view.drawToBitmap()

                                // notif
                                withContext(Dispatchers.IO) {
                                    GalleryUtils.saveVictoryScreenshot(ctx, bitmap)
                                }

                                // calendar
                                CalendarUtils.insertVictoryEvent(ctx, prize)

                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.spin))
                }

                OutlinedButton(
                    enabled = !ui.spinning,
                    onClick = {
                        vm.cashOut(
                            onSaved = { },
                            onError = { }
                        )
                    }
                ) {
                    Text(stringResource(R.string.cash_out))
                }
            }
        }
    }
}

@Composable
private fun ReelBox(symbol: String, spinning: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = if (spinning) 44.sp else 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}
