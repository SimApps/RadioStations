package com.amirami.simapp.radiobroadcastpro.data.datastore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiobroadcastpro.IoDispatcher
import com.amirami.simapp.radiobroadcastpro.data.datastore.preferences.abstraction.DataStoreRepository
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.CHOOSEN_SERVER_KEY
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.THEME_KEY
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.DEFAULT_COUNTRY_KEY
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.FIRST_OPEN_KEY
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.FIRST_TIMEOPEN_RECORD_FOLDER_KEY
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.RADIO_URL_KEY
import com.amirami.simapp.radiobroadcastpro.data.datastore.utils.SAVE_DATA_KEY

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val repository: DataStoreRepository
) : ViewModel() {

init {

    if(getRadioUrl()=="") saveRadioUrl("http://91.132.145.114")
   if(getDefaultCountr()=="") saveDefaultCountry(Locale.getDefault().country)
    if(!getFirstTimeopenRecordFolder()) saveFirstTimeopenRecordFolder(true)
    if(!getFirstOpen()) saveFirstOpen(true)


}
    fun saveDefaultCountry(value: String) {
        viewModelScope.launch(dispatcher) {
            repository.putString(DEFAULT_COUNTRY_KEY, value)
        }
    }

    fun getDefaultCountr(): String = runBlocking {
        repository.getString(DEFAULT_COUNTRY_KEY)
    }

    fun saveChoosenServer(value: String) {
        viewModelScope.launch(dispatcher) {
            repository.putString(CHOOSEN_SERVER_KEY, value)
        }
    }

    fun getChoosenServer(): String = runBlocking {
        repository.getString(CHOOSEN_SERVER_KEY)
    }


    fun saveRadioUrl(value: String) {
        viewModelScope.launch(dispatcher) {
            repository.putString(RADIO_URL_KEY, value)
        }
    }

    fun getRadioUrl(): String = runBlocking {
        repository.getString(RADIO_URL_KEY)
    }






    fun saveFirstTimeopenRecordFolder(value: Boolean) {
        viewModelScope.launch(dispatcher) {
            repository.putBoolean(FIRST_TIMEOPEN_RECORD_FOLDER_KEY, value)
        }
    }

    fun getFirstTimeopenRecordFolder(): Boolean = runBlocking {
        repository.getBoolean(FIRST_TIMEOPEN_RECORD_FOLDER_KEY)
    }


    fun saveDarkTheme(value: String) {
        viewModelScope.launch(dispatcher) {
            repository.putString(THEME_KEY, value)
        }
    }

    fun getDarkTheme(): String = runBlocking {
        repository.getString(THEME_KEY)
    }





    fun saveFirstOpen(value: Boolean) {
        viewModelScope.launch(dispatcher) {
            repository.putBoolean(FIRST_OPEN_KEY, value)
        }
    }

    fun getFirstOpen(): Boolean = runBlocking {
        repository.getBoolean(FIRST_OPEN_KEY)
    }


    fun saveSaveData(value: Boolean) {
        viewModelScope.launch(dispatcher) {
            repository.putBoolean(SAVE_DATA_KEY, value)
        }
    }

    fun getSaveData(): Boolean = runBlocking {
        repository.getBoolean(SAVE_DATA_KEY)
    }

 }