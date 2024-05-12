package com.mine.bicycle.repository.model.clock

data class ClockOut( var st:String = "P5station", var x:Double = 116.641045,
                    var y:Double = 40.381523, var tmv:String, val t:Int = 1,
                     var tmc:String = "778/esUgyhSEsctHrdtvcw==") : ClockSealed() {
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
