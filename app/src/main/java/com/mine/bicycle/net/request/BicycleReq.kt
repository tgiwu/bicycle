package com.mine.bicycle.net.request

data class BicycleReq(var op:String, var filter:String? = null, var state:Int? = null, var isjs:Int? = null, var sortstr:String? = null)
