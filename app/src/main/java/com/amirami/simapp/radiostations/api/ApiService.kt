package com.amirami.simapp.radiostations.api

import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.model.RetrofitRadioResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/json/countrycodes")
    suspend fun getListCountries():Response<RetrofitRadioResponse>

    @GET("/json/languages")
    suspend fun getListLanguages():Response<RetrofitRadioResponse>

    @GET("/json/stations")
    suspend fun getListStations():Response<RetrofitRadioResponse>

    @GET("/json/states")
    suspend fun getListStates():Response<RetrofitRadioResponse>


    @GET("/json/codecs")
    suspend fun getListCodecs():Response<RetrofitRadioResponse>


    @GET("/json/tags")
    suspend fun getListTags():Response<RetrofitRadioResponse>


    @GET("/json/stats")
    suspend fun getStats():Response<JsonObject>

    @GET("/json/stations/bycountrycodeexact/{id}")
    suspend fun getLocalRadios(@Path("id") postId:String):Response<RetrofitRadioResponse>

    @GET("/json/stations/lastclick/{nbr}")
    suspend fun getLastClickedRadios(@Path("nbr") postId:String):Response<RetrofitRadioResponse>

    @GET("/json/stations/byuuid/{id}")
    suspend fun getRadioByUID(@Path("id") postId:String):Response<RetrofitRadioResponse>

    @GET("/json/stations/byname/{id}")
    suspend fun getRadioByName(@Path("id") postId:String):Response<RetrofitRadioResponse>




    @GET("/json/stations/bycountrycodeexact/{id}")
    suspend fun getRadioByCountriesCodeExact(@Path("id") postId:String): Response<RetrofitRadioResponse>

    @GET("/json/stations/bytag/{id}")
    suspend fun getRadioBytags(@Path("id") postId:String): Response<RetrofitRadioResponse>

    @GET("/json/stations/bylanguage/{searchterm}")
    suspend fun getRadioByLanguages(@Path("searchterm") postId:String): Response<RetrofitRadioResponse>

    @GET("/json/stations/bystate/{searchterm}")
    suspend fun getRadioByStates(@Path("searchterm") postId:String):  Response<RetrofitRadioResponse>


    @GET("/json/stations/bycodec/{id}")
    suspend fun getRadioBycodec(@Path("id") postId:String): Response<RetrofitRadioResponse>


    @GET("/json/stations/topclick/{nbr}")
    suspend fun getRadioBytopclick(@Path("nbr") postId:String): Response<RetrofitRadioResponse>

    @GET("/json/stations/topvote/{nbr}")
    suspend  fun getRadioBytopvote(@Path("nbr") postId:String): Response<RetrofitRadioResponse>


    @GET("/json/servers")
    suspend fun getServersList(): Response<RetrofitRadioResponse>








    //   @get:GET("/json/stations/topvote")
    //  val topvote:Call<List<RadioVariables>>












    // @get:GET("{id}")
    // var countrie:Call<List<RadioVariables>>






    @get:GET("/json/servers")
    val servers: Call<List<RadioVariables>>




    //  @get:GET("")
    //  val first: Call<List<RadioVariables>>



    @GET("/json/url/{id}")
    fun addclick(@Path("id") postId:String): Call<List<RadioVariables>>

    // @GET("/json/vote/{id}")
    // fun vote(@Path("id") postId:String):Call<List<RadioVariables>>

















}