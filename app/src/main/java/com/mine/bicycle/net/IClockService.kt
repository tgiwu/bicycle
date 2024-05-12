package com.mine.bicycle.net

import com.mine.bicycle.net.respone.ClockResponse
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query


interface IClockService {

    @FormUrlEncoded
    @POST("daka.aspx")
    suspend fun clockIn(@Query("st") st:String = "P5station", @FieldMap fields:Map<String, String>) : Response<ClockResponse>

    @FormUrlEncoded
    @POST("daka.aspx")
    suspend fun clockOut(@Query("st") st:String = "P5station", @FieldMap fields:Map<String, String>): Response<ClockResponse>
}