package com.amirami.simapp.radiobroadcastpro.api

import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/json/countrycodes")
    suspend fun getListCountries(): Response<List<RadioEntity>>

    @GET("/json/languages")
    suspend fun getListLanguages(): Response<List<RadioEntity>>

    @GET("/json/stations")
    suspend fun getListStations(): Response<List<RadioEntity>>

    @GET("/json/states")
    suspend fun getListStates(): Response<List<RadioEntity>>

    @GET("/json/codecs")
    suspend fun getListCodecs(): Response<List<RadioEntity>>

    @GET("/json/tags")
    suspend fun getListTags(): Response<List<RadioEntity>>

    @GET("/json/stats")
    suspend fun getStats(): Response<JsonObject>

    @GET("/json/stations/bycountrycodeexact/{id}")
    suspend fun getLocalRadios(@Path("id") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/lastclick/{nbr}")
    suspend fun getLastClickedRadios(@Path("nbr") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/byuuid/{id}")
    suspend fun getRadioByUID(@Path("id") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/byname/{id}")
    suspend fun getRadioByName(@Path("id") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/bycountrycodeexact/{id}")
    suspend fun getRadioByCountriesCodeExact(@Path("id") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/bytag/{id}")
    suspend fun getRadioBytags(@Path("id") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/bylanguage/{searchterm}")
    suspend fun getRadioByLanguages(@Path("searchterm") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/bystate/{searchterm}")
    suspend fun getRadioByStates(@Path("searchterm") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/bycodec/{id}")
    suspend fun getRadioBycodec(@Path("id") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/topclick/{nbr}")
    suspend fun getRadioBytopclick(@Path("nbr") postId: String): Response<List<RadioEntity>>

    @GET("/json/stations/topvote/{nbr}")
    suspend fun getRadioBytopvote(@Path("nbr") postId: String): Response<List<RadioEntity>>

    @GET("/json/servers")
    suspend fun getServersList(): Response<List<RadioEntity>>

    //   @get:GET("/json/stations/topvote")
    //  val topvote:Call<List<RadioVariables>>

    // @get:GET("{id}")
    // var countrie:Call<List<RadioVariables>>

    @get:GET("/json/servers")
    val servers: Call<List<RadioEntity>>

    //  @get:GET("")
    //  val first: Call<List<RadioVariables>>

    @GET("/json/url/{id}")
    fun addclick(@Path("id") postId: String): Call<List<RadioEntity>>

    // @GET("/json/vote/{id}")
    // fun vote(@Path("id") postId:String):Call<List<RadioVariables>>
}
