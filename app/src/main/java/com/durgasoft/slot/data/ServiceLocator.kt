package com.durgasoft.slot.data

import android.content.Context

object ServiceLocator {

    @Volatile
    private var repository: SlotRepository? = null

    fun provideRepository(context: Context): SlotRepository {
        return repository ?: synchronized(this) {
            repository ?: createRepository(context).also { repository = it }
        }
    }

    private fun createRepository(context: Context): SlotRepository {
        val db = AppDatabase.get(context)
        return SlotRepository(db.scoreDao())
    }
}
