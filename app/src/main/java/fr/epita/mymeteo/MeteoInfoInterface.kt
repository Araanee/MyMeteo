package fr.epita.mymeteo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MeteoInfoInterface {

    @GET("forecast.json")
    fun getForecastInfo(@Query("q") city: String, @Query("key") key: String) : Call<ForecastInfo>
}