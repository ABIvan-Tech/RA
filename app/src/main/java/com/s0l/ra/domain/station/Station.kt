package com.s0l.ra.domain.station

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Station (
    var code: String? = null,
    var name: String? = null,
    var alternateName: String? = null,
    var alias: List<String>? = null,
    var countryCode: String? = null,
    var countryName: String? = null,
    var countryAlias: String? = null,
    var countryGroupCode: String? = null,
    var countryGroupName: String? = null,
    var timeZoneCode: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var isMobileBoardingPass: Boolean = false,
    var markets: List<Market>? = null,
    var notices: String? = null
)