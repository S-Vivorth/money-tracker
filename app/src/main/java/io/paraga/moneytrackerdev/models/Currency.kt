package io.paraga.moneytrackerdev.models


data class Currency(
    val symbol: String,
    val name: String,
    val symbol_native: String,
    val decimal_digits: Int? = 0,
    val rounding: Int? = 0,
    val code: String,
    val name_plural: String? = ""
)
