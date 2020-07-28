package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Segment (
    var segmentNr: Int = 0,
    var origin: String? = null,
    var destination: String? = null,
    var flightNumber: String? = null,
    var time: List<String>? = null,
    var timeUTC: List<String>? = null,
    var duration: String? = null
)