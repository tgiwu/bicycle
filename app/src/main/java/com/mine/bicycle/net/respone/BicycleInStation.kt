package com.mine.bicycle.net.respone

data class BicycleInStation(
    val btypes: List<String>,
    val `data`: List<List<String>>,
    val rowlength: Int,
    val stations: List<String>
)