package com.mine.bicycle.config

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.Xml
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.mine.bicycle.config.model.BicycleConfig
import com.mine.bicycle.config.model.ClockConfig
import com.mine.bicycle.config.model.Config
import com.mine.bicycle.config.model.Net
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Singleton

object ConfigManager {
    val TAG = ConfigManager::class.simpleName

    var mConfig : Config? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun readConfig(){
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/bluetooth/config.xml")
        if (!file.exists()) {
            mConfig = Config(null)
            return
        }

        FileInputStream(file)
            .use { input ->
                val parser : XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(input, null)
                parser.next()
                mConfig = doReadConfig(parser)!!
                Log.i(TAG, "readConfig: $mConfig")
            }
    }
    @Throws(XmlPullParserException::class, IOException::class)
    private fun doReadConfig(parser: XmlPullParser): Config? {

        var config = Config(null)
        var net:Net? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when(parser.name) {
                "config" -> config = Config(null)
                "net" ->  net = Net(null,null)
                "clock" -> net?.clock = parseClock(parser)
                "bicycle" -> net?.bicycle = parseBicycle(parser)
            }

        }
        config.net = net
        return config
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseClock(parser: XmlPullParser) : ClockConfig {
        parser.require(XmlPullParser.START_TAG, "", "clock")

        var baseUrl : String? = null
        var cookie: String? = null
        var userAgent:String? = null
        var contentType:String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when(parser.name) {
                "base" -> baseUrl = readText(parser)
                "cookie" -> cookie = readText(parser)
                "userAgent" -> userAgent = readText(parser)
                "contentType" -> contentType = readText(parser)
                else -> Log.e(TAG, "parseClock: unknown tag : ${parser.name}")
            }
        }
        return ClockConfig(baseUrl, cookie, contentType, userAgent).decode()

    }
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseBicycle(parser: XmlPullParser) : BicycleConfig {
        parser.require(XmlPullParser.START_TAG, "", "bicycle")

        var baseUrl : String? = null
        var cookie: String? = null
        var userAgent:String? = null
        var contentType:String? = null
        var dnt :String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when(parser.name) {
                "base" -> baseUrl = readText(parser)
                "cookie" -> cookie = readText(parser)
                "userAgent" -> userAgent = readText(parser)
                "contentType" -> contentType = readText(parser)
                "dnt" -> dnt = readText(parser)
                else -> Log.e(TAG, "parseBicycle: unknown tag :${parser.name}")
            }
        }

        return BicycleConfig(baseUrl, cookie, contentType, userAgent, dnt).decode()
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}