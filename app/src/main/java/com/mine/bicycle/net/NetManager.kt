package com.mine.bicycle.net

import android.util.Log
import com.mine.bicycle.annotation.BicycleAnnotation
import com.mine.bicycle.annotation.ClockAnnotation
import com.mine.bicycle.config.ConfigManager
import com.mine.bicycle.config.model.Config
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.sleep
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetManager {
    private val TAG:String = NetManager::class.simpleName!!
    var clockService: IClockService? = null
    var bicycleService: IBicycleServices? = null
    private var isClockBuilding = false
    private var isBicycleBuilding = false
    private var mConfig :Config? = null

    companion object {
        const val TYPE_CLOCK_CLIENT = 0
        const val TYPE_BICYCLE_CLIENT = 1

        @Provides
        @Singleton
        fun providesNetManager():NetManager {
            return NetManager()
        }
    }

    private fun createClockClient() {
        try {
            Log.i(TAG, "createClockClient: base = ${mConfig?.net?.clock?.base}")
            val retrofit = Retrofit.Builder()
                .baseUrl(if (mConfig?.net?.clock?.base == null) "http://www.baidu.com" else mConfig?.net?.clock?.base!!)
                .addConverterFactory(GsonConverterFactory.create())
                .client(buildClient(TYPE_CLOCK_CLIENT))
                .build()

            clockService = retrofit.create(IClockService::class.java)
        } catch (e:Exception) {
            Log.e(TAG, "createClient: ${e.javaClass.name + " "
                    + if (e.message!!.isNotBlank()) e.message else "unknown exception"}", )
        }
    }

    private fun createBicycleClient() {
        try {

            val retrofit = Retrofit.Builder()
                .baseUrl(if (mConfig?.net?.bicycle?.base == null) "http://www.baidu.com" else mConfig?.net?.bicycle?.base!!)
                .addConverterFactory(GsonConverterFactory.create())
                .client(buildClient(TYPE_BICYCLE_CLIENT))
                .build()

            bicycleService = retrofit.create(IBicycleServices::class.java)
        } catch (e:Exception) {
            Log.e(TAG, "createClient: ${e.javaClass.name + " "
                    + if (e.message!!.isNotBlank()) e.message else "unknown exception"}", )
        }
    }

    private fun buildClient(type:Int): OkHttpClient {

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .headers(builderHeader(type))
                .build()

            chain.proceed(request)
        }
            //logging
            .addInterceptor(logInterceptor)
            .build()
    }

    private fun builderHeader(type:Int) : Headers{
        val builder = Headers.Builder()

            .add("Accept", "*/*")
            .add("Accept-Encoding", "gzip, deflate")
            .add("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")


        mConfig?.apply {
            when (type) {
                TYPE_CLOCK_CLIENT ->
                    builder
                        .add("Cookie", if (net?.clock?.cookie == null) "" else net?.clock?.cookie!!)
                        .add(
                            "Content-Type",
                            if (net?.clock?.contentType == null) "" else net?.clock?.contentType!!
                        )
                        .add(
                            "User-Agent",
                            if (net?.clock?.userAgent == null) "" else net?.clock?.contentType!!
                        )

                TYPE_BICYCLE_CLIENT ->
                    builder.add("Cookie", if (net?.bicycle?.cookie == null) "" else net?.bicycle?.cookie!!)
                        .add("Dnt", if (net?.bicycle?.dnt == null) "" else net?.bicycle?.dnt!!)
                        .add("Content-Type", if(net?.bicycle?.contentType == null) "" else net?.bicycle?.contentType!!)
                        .add("User-Agent", if (net?.bicycle?.userAgent == null) "" else net?.bicycle?.userAgent!!)
            }
        }

        return builder.build()
    }

    private fun createClockService() :IClockService {
        if (isClockBuilding) {
            while (isClockBuilding) {
                sleep(50)
            }
        } else {
            isClockBuilding = true
            createClockClient()
            isClockBuilding = false

        }

        return clockService!!

    }

    private fun createBicycleService():IBicycleServices {
        if (isBicycleBuilding) {
            while (isBicycleBuilding) {
                sleep(50)
            }
        } else {
            isBicycleBuilding = true
            createBicycleClient()
            isBicycleBuilding = false

        }

        return bicycleService!!
    }

    fun rebuildClient() {

        Log.i(TAG, "rebuildClient: +++++++++++++++++++++++++")
        if (ConfigManager.mConfig == null) {
            Log.e(TAG, "rebuildClient: config is null!!")
            return
        }
        mConfig = ConfigManager.mConfig

        createBicycleService()
        createClockService()


        Log.i(TAG, "rebuildClient: ${clockService==null}")
    }

}