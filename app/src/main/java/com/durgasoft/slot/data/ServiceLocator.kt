package com.durgasoft.slot.data

import android.content.Context

object ServiceLocator {
    fun repo(context: Context): SlotRepository {
        val db = AppDatabase.get(context)
        return SlotRepository(db.scoreDao())
    }
}
