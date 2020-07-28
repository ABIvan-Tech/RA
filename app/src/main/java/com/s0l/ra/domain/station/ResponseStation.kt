package com.s0l.ra.domain.station

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ResponseStation {
    var stations: List<Station>? = null
}