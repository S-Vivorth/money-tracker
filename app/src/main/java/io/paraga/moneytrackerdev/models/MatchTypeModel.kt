package io.paraga.moneytrackerdev.models


data class MatchTypeModel(
    val list: ArrayList<ArrayList<ImportModel>>
)
data class DataModel(var title: String = "",
                     var icon_select: String = "")

