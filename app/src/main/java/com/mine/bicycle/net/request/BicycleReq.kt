package com.mine.bicycle.net.request

import android.util.Log

data class BicycleReq(var op:String, var filter:String? = null, var state:Int? = null, var isjs:Int? = null, var sortstr:String? = null){
    fun toMap():Map<String, String> {
        val map = hashMapOf<String, String>()

        map += Pair("op", op)
        filter?.apply {  map["filter"] = filter!!}
        state?.apply { map["state"] = state!!.toString() }
        isjs?.apply { map["isjs"] = isjs!!.toString() }
        sortstr?.apply { map["sortstr"] = sortstr!! }

        return map
    }
}
