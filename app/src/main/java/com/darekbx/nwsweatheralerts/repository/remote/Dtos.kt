package com.darekbx.nwsweatheralerts.repository.remote

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("features") var features: ArrayList<Features> = arrayListOf(),
    @SerializedName("title") var title: String? = null,
    @SerializedName("updated") var updated: String = ""
)

data class Features(
    @SerializedName("id") var id: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("geometry") var geometry: Geometry? = null,
    @SerializedName("properties") var properties: Properties? = Properties()
)

data class Geometry(
    @SerializedName("type") var type: String,
    @SerializedName("coordinates") var coordinates: List<List<List<Double>>>,
)

data class Properties(
    @SerializedName("@type") var type: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("areaDesc") var areaDesc: String? = null,
    @SerializedName("effective") var effective: String? = null,
    @SerializedName("ends") var ends: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("messageType") var messageType: String = "",
    @SerializedName("category") var category: String? = null,
    @SerializedName("severity") var severity: String? = null,
    @SerializedName("certainty") var certainty: String? = null,
    @SerializedName("urgency") var urgency: String? = null,
    @SerializedName("event") var event: String = "",
    @SerializedName("senderName") var senderName: String? = null,
    @SerializedName("headline") var headline: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("instruction") var instruction: String? = null,
)
