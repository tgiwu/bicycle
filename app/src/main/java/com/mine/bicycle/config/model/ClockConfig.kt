package com.mine.bicycle.config.model

import android.util.Base64


data class ClockConfig(var base:String?, var cookie:String?, var contentType:String?, var userAgent: String?) {

    fun decode() : ClockConfig {

        base = base?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        cookie = cookie?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        contentType = contentType?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        userAgent = userAgent?.let { String(Base64.decode(it, Base64.DEFAULT)) }
        return this
    }
}
