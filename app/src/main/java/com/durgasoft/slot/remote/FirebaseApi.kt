package com.durgasoft.slot.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface FirebaseApi {

    @PUT("scores/{uid}.json")
    suspend fun putScore(
        @Path("uid") uid: String,
        @Body body: FirebaseScoreDto
    ): Response<FirebaseScoreDto>

    @GET("scores.json")
    suspend fun getAllScores(): Response<Map<String, FirebaseScoreDto>?>
}
