package com.mine.bicycle.net.respone


/*
ex: ["imeiC13334373274","捷安特",17,"骑行中",62,false,false,true,"116.652213333333,40.4062466666667,0.062",false]
 */
data class Bicycle(var imei:String, var name:String, var status:Int, var station: String, var power:Int,
                   var locked:Boolean, var online:Boolean, var unknownB1:Boolean, var location: String, var unknownB2: Boolean)
