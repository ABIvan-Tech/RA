package com.s0l.ra.domain.station

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Market(
    var code: String? = null,
    var group: Any? = null
)