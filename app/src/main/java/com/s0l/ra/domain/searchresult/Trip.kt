package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Trip (
    var origin: String? = null,
    var originName: String? = null,
    var destination: String? = null,
    var destinationName: String? = null,
    var routeGroup: String? = null,
    var tripType: String? = null,
    var upgradeType: String? = null,
    var dates: List<Date>? = null
)