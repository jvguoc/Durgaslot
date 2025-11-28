package com.durgasoft.slot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.durgasoft.slot.data.ServiceLocator

class SlotVMFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SlotViewModel::class.java)) {
            val repo = ServiceLocator.provideRepository(context)
            @Suppress("UNCHECKED_CAST")
            return SlotViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
