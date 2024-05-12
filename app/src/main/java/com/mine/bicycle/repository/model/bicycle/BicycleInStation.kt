package com.mine.bicycle.repository.model.bicycle

data class BicycleInStation(val op:String = "stationbike", val isjs:Int = 1) {
    fun toField() :Map<String,String> {
        val map = HashMap<String, String>()

        map["op"] = op
        map["isjs"] = isjs.toString()

        return map
    }
}
