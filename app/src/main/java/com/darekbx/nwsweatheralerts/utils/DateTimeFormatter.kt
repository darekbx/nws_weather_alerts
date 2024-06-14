package com.darekbx.nwsweatheralerts.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateTimeFormatter {

    fun format(input: String): String {
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = OffsetDateTime.parse(input, inputFormatter)
        val utcDateTime = dateTime.withOffsetSameInstant(ZoneOffset.UTC)
        return utcDateTime.format(outputFormatter)
    }
}
