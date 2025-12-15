package com.durgasoft.slot.remote

import android.util.Log
import com.durgasoft.slot.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FirebaseRepository {

    private val api: FirebaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.FIREBASE_DB_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseApi::class.java)
    }

    suspend fun uploadScore(uid: String, name: String, maxChips: Int, timestamp: Long) {
        val res = api.putScore(
            uid = uid,
            body = FirebaseScoreDto(
                uid = uid,
                name = name,
                maxChips = maxChips,
                timestamp = timestamp
            )
        )

        if (!res.isSuccessful) {
            val msg = "RTDB PUT failed code=${res.code()} msg=${res.message()}"
            Log.e("FIREBASE_RTDB", msg)
            throw RuntimeException(msg)
        } else {
            Log.d("FIREBASE_RTDB", "RTDB PUT OK uid=$uid maxChips=$maxChips")
        }
    }

    suspend fun fetchTop10(): List<FirebaseScoreDto> {
        val res = api.getAllScores()

        if (!res.isSuccessful) {
            val msg = "RTDB GET failed code=${res.code()} msg=${res.message()}"
            Log.e("FIREBASE_RTDB", msg)
            throw RuntimeException(msg)
        }

        val map = res.body().orEmpty()
        Log.d("FIREBASE_RTDB", "RTDB GET OK entries=${map.size}")

        return map.values
            .map {
                FirebaseScoreDto(
                    uid = it.uid ?: "",
                    name = it.name ?: "Desconocido",
                    maxChips = it.maxChips ?: 0,
                    timestamp = it.timestamp ?: 0L
                )
            }
            .sortedWith(
                compareByDescending<FirebaseScoreDto> { it.maxChips ?: 0 }
                    .thenByDescending { it.timestamp ?: 0L }
            )
            .take(10)
    }
}
