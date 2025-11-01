package com.durgasoft.slot.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class SlotRepository(private val dao: ScoreDao) {
    fun saveScore(max: Int): Completable {
        val entity = ScoreEntity(maxChips = max, savedAtMillis = System.currentTimeMillis())
        return dao.insert(entity)
    }
    fun getTop7(): Single<List<ScoreEntity>> = dao.getTop(7)
}
