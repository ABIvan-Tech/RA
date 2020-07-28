package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Date (
    var dateOut: String? = null,
    var flights: List<Flight>? = null
)