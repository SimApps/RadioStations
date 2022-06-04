package com.amirami.simapp.radiostations.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.hiltcontainer.RadioApplication
import com.amirami.simapp.radiostations.model.Resource
import com.amirami.simapp.radiostations.repository.RetrofitRadioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.net.ConnectivityManager.*

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiostations.model.Status
import kotlinx.coroutines.flow.*
import java.io.IOException

@HiltViewModel
class RetrofitRadioViewModel
@Inject
constructor(
    private val repository: RetrofitRadioRepository,
    app: Application
) : AndroidViewModel(app) {


    private val _statsresponse = MutableStateFlow(Resource(Status.SUCCESS, null, ""))
    val statsresponse = _statsresponse.asStateFlow()


    private val _responseListRadio = MutableStateFlow<Resource<*>>(Resource(Status.SUCCESS, null, ""))
    val responseRadioList = _responseListRadio.asStateFlow()


    private val _responseListCountrieRadio = MutableStateFlow<Resource<*>>(Resource(Status.SUCCESS, null, ""))
    val responseListCountrieRadio = _responseListCountrieRadio.asStateFlow()


    private val _responseRadio = MutableStateFlow<Resource<*>>(Resource(Status.SUCCESS, null, ""))
    val responseRadio = _responseRadio.asStateFlow()

    private val _responseLastClicked = MutableStateFlow<Resource<*>>(Resource(Status.SUCCESS, null, ""))
    val responseLastClicked = _responseLastClicked.asStateFlow()

    private val _responseRadioSreach = MutableStateFlow<Resource<*>>(Resource(Status.LOADING, null, ""))
    val responseRadioSreach = _responseRadioSreach.asStateFlow()

    private val _responseRadioUID = MutableStateFlow<Resource<*>>(Resource(Status.LOADING, null, ""))
    val responseRadioUID = _responseRadioUID.asStateFlow()

    private val _responseLocalRadio = MutableStateFlow(Resource(Status.SUCCESS, null, ""))
    val responseLocalRadio = _responseLocalRadio.asStateFlow()


    init {
        /*viewModelScope.launch {

            getLocalRadio(preferencesViewModel.preferencesFlow.first().default_country)
        }
           viewModelScope.launch {
               getLocalRadio(Locale.getDefault().country)
           }*/
        getListCountrieRadios()
    }


    fun getListservers() = viewModelScope.launch {
        getListserver()
    }

    suspend fun getListserver() = viewModelScope.launch {
        _responseListRadio.value = Resource.loading(null)
        //    delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getListServers().let { response ->
                    if (response.isSuccessful) _responseListRadio.value = Resource.success(response.body())
                    else _responseListRadio.value = Resource.error(response.code().toString(), response.body())
                }
            } else _responseListRadio.value = Resource.error("No internet connection", null)

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseListRadio.value = Resource.error("Network Failure", null)

                else -> _responseListRadio.value = Resource.error("Conversion Error", null)
            }
        }
    }

    fun getStatis() = viewModelScope.launch {
        getStat()
    }


    suspend fun getStat() = viewModelScope.launch {
        _statsresponse.value = Resource.loading(null)
        //   _statsresponse.postValue(Resource.loading(null))
        //   delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getStatistics().let { response ->
                    if (response.isSuccessful) _statsresponse.value =
                        Resource.success(response.body()) as Resource<Nothing> //.postValue(Resource.success(response.body()))
                    else _statsresponse.value =
                        Resource.error(
                            response.code().toString(),
                            response.body()
                        ) as Resource<Nothing>//.postValue(Resource.error(response.code().toString(),response.body()))
                }
            } else _statsresponse.value = Resource.error(
                "No internet connection",
                null
            )//.postValue(Resource.error("No internet connection",null))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseListRadio.value = Resource.error("Network Failure", null)
                else -> _responseListRadio.value = Resource.error("Conversion Error", null)
            }
        }
    }

    fun getLocalRadio(countriecode: String) = viewModelScope.launch {
        getlocalRadio(countriecode)
    }

    suspend fun getlocalRadio(countriecode: String) = viewModelScope.launch {

        _responseLocalRadio.value = Resource.loading(null) //.postValue(Resource.loading(null))
        //  delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getRadiosLocals(countriecode).let { response ->

                    if (response.isSuccessful) _responseLocalRadio.value =
                        Resource.success(response.body()) as Resource<Nothing>//.postValue(Resource.success(response.body()))
                    else _responseLocalRadio.value =
                        Resource.error(
                            response.code().toString(),
                            response.body()
                        ) as Resource<Nothing>//.postValue(Resource.error(response.code().toString(),response.body()))
                }

            } else _responseLocalRadio.value = Resource.error(
                "No internet connection",
                null
            )//.postValue(Resource.error("No internet connection",null))

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseListRadio.value = Resource.error("Network Failure", null)

                else -> _responseListRadio.value = Resource.error("Conversion Error", null)
            }
        }
    }

    fun getLastClikedRadio(nbr: String) = viewModelScope.launch {
        getLastclikedRadio(nbr)
    }

    suspend fun getLastclikedRadio(nbr: String) = viewModelScope.launch {
        _responseLastClicked.value = Resource.loading(null)
        //   delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getClickedLast(nbr).let { response ->
                    if (response.isSuccessful) _responseLastClicked.value = Resource.success(response.body())
                    else _responseLastClicked.value = Resource.error(response.code().toString(), response.body())
                }
            }
            else
                _responseLastClicked.value = Resource.error("No internet connection", null)

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseListRadio.value = Resource.error("Network Failure", null)
                else -> _responseListRadio.value = Resource.error("Conversion Error", null)
            }
        }
    }

    fun getRadiosByName(name: String) = viewModelScope.launch {
        getRadiosByname(name)
    }

    fun getRadiosByUId(name: String) = viewModelScope.launch {
        getRadioByUId(name)
    }

    suspend fun getRadioByUId(UId: String) = viewModelScope.launch {
        //_responseRadioSreach.emit(Resource.loading(null))
        _responseRadioUID.value = Resource.loading(null)
        //    delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getRadiobyuid(UId).let { response ->
                    if (response.isSuccessful) _responseRadioUID.value = Resource.success(response.body())//_responseRadioSreach.emit(Resource.success(response.body()))
                    else _responseRadioUID.value = Resource.error(response.code().toString(), response.body())// _responseRadioSreach.emit(Resource.error(response.code().toString(), response.body()))
                }
            }
            else _responseRadioUID.value =Resource.error("No internet connection", null)//_responseRadioSreach.emit(Resource.error("No internet connection", null))

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseRadioUID.value = Resource.error("Network Failure", null)//_responseRadioSreach.emit(Resource.error("Network Failure", null))

                else -> _responseRadioUID.value = Resource.error("Conversion Error", null)//_responseRadioSreach.emit(Resource.error("Conversion Error", null))
            }
        }
    }



    suspend fun getRadiosByname(name: String) = viewModelScope.launch {
        //_responseRadioSreach.emit(Resource.loading(null))
        _responseRadioSreach.value = Resource.loading(null)
        //    delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getRadioByname(name).let { response ->
                    if (response.isSuccessful) _responseRadioSreach.value = Resource.success(response.body())//_responseRadioSreach.emit(Resource.success(response.body()))

                    else _responseRadioSreach.value = Resource.error(response.code().toString(), response.body())// _responseRadioSreach.emit(Resource.error(response.code().toString(), response.body()))
                }
            } else _responseRadioSreach.value =Resource.error("No internet connection", null)//_responseRadioSreach.emit(Resource.error("No internet connection", null))

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseRadioSreach.value = Resource.error("Network Failure", null)//_responseRadioSreach.emit(Resource.error("Network Failure", null))

                else -> _responseRadioSreach.value = Resource.error("Conversion Error", null)//_responseRadioSreach.emit(Resource.error("Conversion Error", null))
            }
        }
    }


    fun getRadios(msg: String, secondmsg: String) = viewModelScope.launch {
        getRadio(msg, secondmsg)
    }


    suspend fun getRadio(msg: String, secondmsg: String) = viewModelScope.launch {

        _responseRadio.value = Resource.loading(null)
        //  delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.apply {
                    when (msg) {
                        MainActivity.defaultCountry -> getRadiobyCountriesCodeExact(msg)
                        // MAY BE UNECECERY CHECK
                        getApplication<RadioApplication>().resources.getString(R.string.Pop) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Inspirational) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.International) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Electronic) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Reggae) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Latin) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Hits) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Rock) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.Country) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.News) -> getRadiobytags(
                            msg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.languages) -> getRadiobyLanguages(
                            secondmsg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.countries) -> getRadiobyCountriesCodeExact(
                            secondmsg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.states) -> getRadiobyStates(
                            secondmsg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.tags) -> getRadiobytags(
                            secondmsg
                        )
                        getApplication<RadioApplication>().resources.getString(R.string.codecs) -> getRadiobycodec(
                            secondmsg
                        )
                        "Top Votes" -> getRadiobytopvote(secondmsg)
                        getApplication<RadioApplication>().resources.getString(R.string.recents) -> getClickedLast(
                            secondmsg
                        )
                        "Top clicks" -> getRadiobytopclick(secondmsg)
                        else -> getRadiobytags(secondmsg)
                    }.let { response ->
                        if (response.isSuccessful) _responseRadio.value = Resource.success(response.body())

                        else _responseRadio.value = Resource.error(response.code().toString(), response.body())


                    }
                }
            }
            else _responseRadio.value = Resource.error("No internet connection", null)

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseRadio.value = Resource.error("Network Failure", null)
                else -> _responseRadio.value = Resource.error("Conversion Error", null)
            }
        }


    }

    fun getListRadios(msg: String) = viewModelScope.launch {
        getListRadio(msg)
    }

    fun getListCountrieRadios() = viewModelScope.launch {
        getListCountrieRadio()
    }

    suspend fun getListCountrieRadio() = viewModelScope.launch {
        _responseListCountrieRadio.value = Resource.loading(null)
        //    delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.getCountriesList().let { response ->
                    if (response.isSuccessful) _responseListCountrieRadio.value =
                        Resource.success(response.body())
                    else _responseListCountrieRadio.value =
                        Resource.error(response.code().toString(), response.body())
                }
            }
            else _responseListCountrieRadio.value = Resource.error("No internet connection", null)

        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _responseListCountrieRadio.value = Resource.error("Network Failure", null)
                }
                else -> _responseListCountrieRadio.value = Resource.error("Conversion Error", null)
            }
        }
    }

    suspend fun getListRadio(msg: String) = viewModelScope.launch {
        _responseListRadio.value = Resource.loading(null)
        //    delay(1500)
        try {
            if (hasInternetConnection()) {
                repository.apply {
                    when (msg) {
                        getApplication<RadioApplication>().resources.getString(R.string.languages) -> getLanguagesList()
                        getApplication<RadioApplication>().resources.getString(R.string.stations) -> getStationsList()
                        getApplication<RadioApplication>().resources.getString(R.string.states) -> getStatesList()
                        getApplication<RadioApplication>().resources.getString(R.string.codecs) -> getCodecsList()
                        getApplication<RadioApplication>().resources.getString(R.string.tags) -> getTagsList()
                        getApplication<RadioApplication>().resources.getString(R.string.Countries) -> getCountriesList()

                        else -> getCountriesList()
                    }.let { response ->
                        if (response.isSuccessful) _responseListRadio.value = Resource.success(response.body())
                        else _responseListRadio.value = Resource.error(response.code().toString(), response.body())
                    }
                }
            }
            else _responseListRadio.value = Resource.error("No internet connection", null)

        } catch (t: Throwable) {
            when (t) {
                is IOException -> _responseListRadio.value = Resource.error("Network Failure", null)
                else -> _responseListRadio.value = Resource.error("Conversion Error", null)
            }
        }
    }


    fun changeBseUrl() {
        if (MainActivity.repeat_tryconnect_server < MainActivity.server_arraylist.size - 1) {
            MainActivity.repeat_tryconnect_server += 1
            MainActivity.server_arraylist[MainActivity.repeat_tryconnect_server]
            MainActivity.BASE_URL =
                MainActivity.server_arraylist[MainActivity.repeat_tryconnect_server]

        } else MainActivity.repeat_tryconnect_server = -1
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<RadioApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}