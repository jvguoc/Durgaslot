package com.durgasoft.slot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.durgasoft.slot.data.SlotRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.random.Random

data class SlotUiState(
    val chips: Int = 5,
    val maxChips: Int = 5,
    val lastWin: Int = 0,
    val spinning: Boolean = false,
    val currentReels: List<Int> = listOf(0, 0, 0),
    val lastCombo: String = ""
)

class SlotViewModel(
    private val repository: SlotRepository
) : ViewModel() {

    val symbols = listOf("7", "A", "K", "Q", "J", "B")
    private val disposables = CompositeDisposable()

    var ui by mutableStateOf(SlotUiState())
        private set

    private val rng get() = Random(System.nanoTime())

    fun canSpin() = !ui.spinning && ui.chips > 0

    fun requestSpin(): List<Int> {
        if (ui.spinning || ui.chips <= 0) return emptyList()
        val targets = List(3) { rng.nextInt(symbols.size) }
        ui = ui.copy(chips = ui.chips - 1, lastWin = 0, spinning = true)
        return targets
    }

    fun finalizeSpin(targets: List<Int>) {
        val prize = payout(targets.map { symbols[it] })
        val chipsNow = ui.chips + prize
        val max = maxOf(ui.maxChips, chipsNow)
        ui = ui.copy(
            chips = chipsNow,
            maxChips = max,
            lastWin = prize,
            spinning = false,
            currentReels = targets,
            lastCombo = targets.joinToString(" | ") { symbols[it] }
        )
    }

    /** Guardar SIEMPRE la puntuación al pulsar Cash Out y resetear sesión */
    fun cashOut(onSaved: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
        val d = repository.saveScore(ui.maxChips)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                ui = SlotUiState()
                onSaved?.invoke()
            }, { e -> onError?.invoke(e) })
        disposables.add(d)
    }

    fun resetSession() {
        ui = SlotUiState()
    }

    private fun payout(r: List<String>): Int {
        val counts = r.groupingBy { it }.eachCount()
        val c7 = counts["7"] ?: 0
        val cA = counts["A"] ?: 0
        val cK = counts["K"] ?: 0
        val cQ = counts["Q"] ?: 0
        val cJ = counts["J"] ?: 0
        val cB = counts["B"] ?: 0

        when {
            c7 == 3 -> return 100
            cA == 3 -> return 50
            cK == 3 -> return 40
            cQ == 3 -> return 30
            cJ == 3 -> return 20
            cB == 3 -> return 10
        }
        if (cB == 1) {
            when {
                c7 == 2 -> return 40
                cA == 2 -> return 30
                cK == 2 -> return 20
                cQ == 2 -> return 10
                cJ == 2 -> return 5
            }
        }
        if (cB == 2) return 1
        return 0
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
