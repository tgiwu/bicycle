package com.mine.bicycle.repository.model.clock


data class ClockIn(
    var st: String = "P5station", var x: Double = 116.641045,
    var y: Double = 40.381523, var t: String = "undefined",
    var tmv: String, var tmc: String = "uLTxqaJ4KXfJ5OloxGarzA=="
) : ClockSealed() {
    fun toFields():Map<String, String> {
        val map = HashMap<String, String>()

        map["st"] = st
        map["x"] = x.toString()
        map["y"] = y.toString()
        map["tmv"] = tmv
        map["tmc"] = tmc
        map["t"] = t.toString()
        map["op"] = op
        map["isjs"] = isjs.toString()

        return map
    }
}
