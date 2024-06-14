package com.darekbx.nwsweatheralerts.repository.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NWSService {

    @GET("alerts/active")
    suspend fun getActiveAlerts(
        @Query("status") status: String,
        @Query("message_type") messageType: String,
        @Query("limit") limit: Int
    ): Response
}
