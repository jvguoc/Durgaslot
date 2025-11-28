package com.durgasoft.slot.data

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class SlotRepository(
    private val scoreDao: ScoreDao
) {

    fun saveScore(
        maxChips: Int,
        onSaved: () -> Unit,
        onError: () -> Unit
    ) {
        Completable.fromAction {
            scoreDao.insert(
                ScoreEntity(
                    maxChips = maxChips,
                    savedAtMillis = System.currentTimeMillis()
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onSaved() },
                { onError() }
            )
    }

    fun getTop7(): Single<List<ScoreEntity>> = scoreDao.getTop7()
}
