package com.amirami.simapp.radiostations.alarm.ui.activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiostations.alarm.data.local.AlarmRepository
import com.amirami.simapp.radiostations.alarm.data.model.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CommonViewModel @Inject constructor(private val repository: AlarmRepository) :
    ViewModel() {

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