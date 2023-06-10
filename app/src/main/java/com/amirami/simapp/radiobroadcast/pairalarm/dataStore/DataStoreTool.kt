package com.amirami.simapp.radiobroadcast.pairalarm.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.amirami.simapp.radiobroadcast.R
import com.amirami.simapp.radiobroadcast.RadioFunction.errorToast
import com.amirami.simapp.radiobroadcast.hiltcontainer.RadioApplication.Companion.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

interface DataStoreTool : CoroutineScope {

    val context: Context
    val job: Job

    fun getStoredStringDataWithFlow(key: String): Flow<String> {
        val setDataKey = stringPreferencesKey(key)
        return context.dataStore.data.catch { exception ->

            errorToast(context, context.getString(R.string.toast_get_dataStore_error))
        }.map { preferences ->
            // No type safety.
            preferences[setDataKey] ?: ""
        }
    }

    fun getStoredIntDataWithFlow(key: String): Flow<Int> {
        val setDataKey = intPreferencesKey(key)
        return context.dataStore.data.catch { exception ->
            errorToast(context, context.getString(R.string.toast_get_dataStore_error))
        }.map { preferences ->
            // No type safety.
            preferences[setDataKey] ?: 0
        }
    }

    suspend fun saveStringData(key: String, textData: String) {
        val setDataKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
           // Timber.d("setQuickAlarmBell title: $key")
           // Timber.d("setQuickAlarmBell description: $textData")
            preferences[setDataKey] = textData
        }
    }

    suspend fun saveIntData(key: String, intData: Int) {
        val setDataKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[setDataKey] = intData
        }
    }
}
