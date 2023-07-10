package io.paraga.moneytrackerdev.models

data class ImportModel (
    var title: String = "",
    var icon_select: String = "",
    var isCheck: Boolean = false,
    var mSelectPosition : Int = 0
        )