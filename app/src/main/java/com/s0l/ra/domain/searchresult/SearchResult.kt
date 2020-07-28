package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    var termsOfUse: String? = null,
    var currency: String? = null,
    var currPrecision: Int = 0,
    var routeGroup: String? = null,
    var tripType: String? = null,
    var upgradeType: String? = null,
    var trips: List<Trip>? = null,
    var serverTimeUTC: String? = null
)