package com.durgasoft.slot.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface ScoreDao {

    @Insert
    fun insert(score: ScoreEntity)

    @Query(
        "SELECT * FROM scores " +
                "ORDER BY maxChips DESC, savedAtMillis DESC " +
                "LIMIT 7"
    )
    fun getTop7(): Single<List<ScoreEntity>>
}
