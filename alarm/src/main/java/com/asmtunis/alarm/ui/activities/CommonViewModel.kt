package com.asmtunis.alarm.ui.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asmtunis.alarm.data.local.AlarmRepository
import com.asmtunis.alarm.data.model.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

//import dagger.hilt.android.lifecycle.HiltViewModel


class CommonViewModel @Inject constructor(
    private val repository: AlarmRepository,

    app: Application
) : AndroidViewModel(app) {


    val _alarmList = MutableLiveData<MutableList<Alarm>>()
    val alarmList: LiveData<MutableList<Alarm>> = _alarmList


    fun getAllLiveAlarm(): LiveData<List<Alarm>> {
        return repository.getAllLiveAlarm()
    }

    fun getAllAlarm() = viewModelScope.launch {
        _alarmList.postValue(repository.getAllAlarm().toMutableList())
    }

    fun update(alarm: Alarm) = viewModelScope.launch {
        repository.update(alarm)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun deleteAlarm(alarmId: Int) = viewModelScope.launch {
        repository.deleteAlarm(alarmId)
    }


    fun insertAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.insert(alarm)
    }



}