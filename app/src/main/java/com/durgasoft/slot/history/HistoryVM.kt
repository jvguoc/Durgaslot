package com.durgasoft.slot.history

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.durgasoft.slot.data.ScoreEntity
import com.durgasoft.slot.data.SlotRepository
import com.durgasoft.slot.data.ServiceLocator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

data class HistoryUiState(
    val scores: List<ScoreEntity> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

class HistoryVM(
    private val repository: SlotRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private var _ui by mutableStateOf(HistoryUiState())
    val ui: HistoryUiState get() = _ui

    init {
        loadTop()
    }

    fun loadTop() {
        _ui = _ui.copy(loading = true, error = null)

        val d = repository.getTop7()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    _ui = _ui.copy(
                        scores = list,
                        loading = false,
                        error = null
                    )
                },
                { error ->
                    _ui = _ui.copy(
                        loading = false,
                        error = error.message ?: "Error al cargar el historial"
                    )
                }
            )

        disposables.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

class HistoryVMFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryVM::class.java)) {
            val repo = ServiceLocator.provideRepository(context)
            @Suppress("UNCHECKED_CAST")
            return HistoryVM(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
