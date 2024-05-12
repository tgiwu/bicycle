package com.mine.bicycle.net

import com.mine.bicycle.net.respone.BicycleInStation
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IBicycleServices {

    @FormUrlEncoded
    @POST("sys/setbicks.aspx")
    suspend fun bicycleStation(@FieldMap fieldMap: Map<String, String>) : Response<BicycleInStation>
}