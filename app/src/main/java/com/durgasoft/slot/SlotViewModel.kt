package com.durgasoft.slot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.durgasoft.slot.data.SlotRepository
import kotlin.random.Random

data class SlotUiState(
    val chips: Int = 5,
    val maxChips: Int = 5,
    val lastWin: Int = 0,
    val lastCombo: String = "",
    val spinning: Boolean = false,
    val currentReels: List<Int> = listOf(0, 0, 0)
)

class SlotViewModel(
    private val repository: SlotRepository
) : ViewModel() {

    val symbols = listOf("7", "A", "K", "Q", "J", "BAR")

    private var _uiState by mutableStateOf(SlotUiState())
    val ui: SlotUiState get() = _uiState

    fun canSpin(): Boolean = !ui.spinning && ui.chips > 0

    fun requestSpin(): List<Int> {
        if (!canSpin()) return emptyList()

        val targets = List(3) { Random.nextInt(symbols.size) }

        _uiState = ui.copy(
            chips = ui.chips - 1,
            spinning = true,
            lastWin = 0
        )

        return targets
    }

    fun finalizeSpin(finalTargets: List<Int>) {
        if (finalTargets.size != 3) return

        val comboSymbols = finalTargets.map { symbols[it] }
        val win = calculatePrize(comboSymbols)

        val newChips = ui.chips + win
        val newMax = maxOf(ui.maxChips, newChips)

        _uiState = ui.copy(
            chips = newChips,
            maxChips = newMax,
            lastWin = win,
            lastCombo = comboSymbols.joinToString(" "),
            spinning = false,
            currentReels = finalTargets
        )
    }

    private fun calculatePrize(combo: List<String>): Int {
        if (combo.size != 3) return 0
        return 100
    }

    fun cashOut(
        location: String?,
        onSaved: () -> Unit,
        onError: () -> Unit
    ) {
        val score = ui.maxChips

        repository.saveScore(
            maxChips = score,
            location = location,
            onSaved = {
                _uiState = SlotUiState()
                onSaved()
            },
            onError = {
                onError()
            }
        )
    }
}
