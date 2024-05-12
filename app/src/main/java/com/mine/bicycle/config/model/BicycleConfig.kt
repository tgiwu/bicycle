package com.mine.bicycle.config.model

import android.util.Base64

data class BicycleConfig(var base:String?, var cookie:String?, var contentType:String?, var userAgent: String?, var dnt:String?) {
    fun decode() : BicycleConfig{
        base = base?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        cookie = cookie?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        contentType = contentType?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        userAgent = userAgent?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        dnt = dnt?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        return this
    }
}
