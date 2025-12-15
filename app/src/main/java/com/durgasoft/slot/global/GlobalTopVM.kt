package com.durgasoft.slot.global

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durgasoft.slot.remote.FirebaseRepository
import com.durgasoft.slot.remote.FirebaseScoreDto
import kotlinx.coroutines.launch

data class GlobalTopUi(
    val loading: Boolean = false,
    val items: List<FirebaseScoreDto> = emptyList(),
    val error: String? = null
)

class GlobalTopVM(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var ui by mutableStateOf(GlobalTopUi())
        private set

    fun load() {
        if (ui.loading) return
        ui = ui.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                val items = repo.fetchTop10()
                ui = ui.copy(loading = false, items = items, error = null)
            } catch (e: Exception) {
                ui = ui.copy(loading = false, error = e.message ?: "Error cargando top global")
            }
        }
    }
}
