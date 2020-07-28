package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Flight (
    var faresLeft: Int = 0,
    var flightKey: String? = null,
    var infantsLeft: Int = 0,
    var regularFare: RegularFare? = null,
    var operatedBy: String? = null,
    var segments: List<Segment>? = null,
    var flightNumber: String? = null,
    var time: List<String>? = null,
    var timeUTC: List<String>? = null,
    var duration: String? = null
)