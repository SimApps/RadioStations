package com.amirami.simapp.radiostations.preferencesmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel  @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val preferencesFlow = preferencesManager.preferencesFlow


/*
used to sort database see prference manager class and code inflow
    val preferencesFlow = preferencesManager.preferencesFlow


    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }
    */


    fun onFirstOpenChanged(value: Boolean) = viewModelScope.launch {
        preferencesManager.updateFirstOpen(value)
    }

    fun onFirstTimeopenRecordFolderChanged(value: Boolean) = viewModelScope.launch {
        preferencesManager.updateFirstTimeopenRecordFolder(value)
    }

    fun onDefaultCountryChanged(value: String) = viewModelScope.launch {
        preferencesManager.updateDefaultCountry(value)
    }

    fun onChoosenServerChanged(value: String) = viewModelScope.launch {
        preferencesManager.updateChoosenServer(value)
    }


    fun onDarkThemeChanged(value: Boolean) = viewModelScope.launch {
        preferencesManager.updateDarkTheme(value)
    }

    fun onSwitchTimerDataChanged(value: Boolean) = viewModelScope.launch {
        preferencesManager.updateSwitchTimerData(value)
    }

    fun onSaveDataChanged(value: Boolean) = viewModelScope.launch {
        preferencesManager.updateSaveData(value)
    }



    fun onSystemThemeChanged(value: Boolean) = viewModelScope.launch {
        preferencesManager.updatesystemTheme(value)
    }
    //  val tasks = tasksFlow.asLiveData() ALSO FOR SORT ORDER SE CODE IN FLOW
}