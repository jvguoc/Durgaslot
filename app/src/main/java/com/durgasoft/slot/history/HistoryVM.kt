package com.durgasoft.slot.history

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.durgasoft.slot.data.ScoreEntity
import com.durgasoft.slot.data.ServiceLocator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

data class HistoryUiState(
    val loading: Boolean = true,
    val top: List<ScoreEntity> = emptyList(),
    val error: String? = null
)

class HistoryVM(context: Context) : ViewModel() {
    private val repo = ServiceLocator.repo(context)
    private val disposables = CompositeDisposable()

    var ui by mutableStateOf(HistoryUiState())
        private set

    fun loadTop7() {
        ui = ui.copy(loading = true, error = null)
        val d = repo.getTop7()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                ui = ui.copy(loading = false, top = list, error = null)
            }, { e ->
                ui = ui.copy(loading = false, error = e.message ?: "Error")
            })
        disposables.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
