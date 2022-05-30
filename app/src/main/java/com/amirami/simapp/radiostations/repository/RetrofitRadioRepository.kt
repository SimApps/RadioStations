package com.amirami.simapp.radiostations.repository

import com.amirami.simapp.radiostations.api.ApiService
import javax.inject.Inject

class RetrofitRadioRepository
@Inject
constructor(private val apiService: ApiService) {

    suspend fun getCountriesList() = apiService.getListCountries()
    suspend fun getLanguagesList() = apiService.getListLanguages()

    suspend fun getStationsList() = apiService.getListStations()
    suspend fun getCodecsList() = apiService.getListCodecs()
    suspend fun getStatesList() = apiService.getListStates()
    suspend fun getTagsList() = apiService.getListTags()
    suspend fun getListServers() = apiService.getServersList()
    suspend fun getStatistics()  =  apiService.getStats()


    suspend fun getRadiosLocals(countriecode:String) = apiService.getLocalRadios(countriecode)

    suspend fun getClickedLast(nbr:String) = apiService.getLastClickedRadios(nbr)

    suspend fun getRadiobyuid(Uid:String) = apiService.getRadioByUID(Uid)

    suspend fun getRadioByname(name:String) = apiService.getRadioByName(name)


    suspend fun getRadiobyCountriesCodeExact(value:String) = apiService.getRadioByCountriesCodeExact(value)
    suspend fun getRadiobytags(tags:String) = apiService.getRadioBytags(tags)
    suspend fun getRadiobyLanguages(language:String) = apiService.getRadioByLanguages(language)
    suspend fun getRadiobyStates(states:String) = apiService.getRadioByStates(states)
    suspend fun getRadiobycodec(codec:String) = apiService.getRadioBycodec(codec)
    suspend fun getRadiobytopclick(value:String) = apiService.getRadioBytopclick(value)
    suspend fun getRadiobytopvote(value:String) = apiService.getRadioBytopvote(value)






}

