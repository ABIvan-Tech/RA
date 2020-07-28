package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegularFare (
    var fareKey: String? = null,
    var fareClass: String? = null,
    var fares: List<Fare>? = null
)