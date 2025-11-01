package com.durgasoft.slot.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ScoreDao {
    @Insert
    fun insert(score: ScoreEntity): Completable

    @Query("SELECT * FROM scores ORDER BY maxChips DESC, savedAtMillis DESC LIMIT :limit")
    fun getTop(limit: Int): Single<List<ScoreEntity>>
}
