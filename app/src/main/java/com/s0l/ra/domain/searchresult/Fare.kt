package com.s0l.ra.domain.searchresult
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Fare (
    var type: String? = null,
    var amount: Double = 0.0,
    var count: Int = 0,
    var isHasDiscount: Boolean = false,
    var publishedFare: Double = 0.0,
    var discountInPercent: Int = 0,
    var isHasPromoDiscount: Boolean = false,
    var discountAmount: Double = 0.0
)