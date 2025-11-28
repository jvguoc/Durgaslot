package com.durgasoft.slot.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val maxChips: Int,
    val savedAtMillis: Long,
    val location: String?
)
