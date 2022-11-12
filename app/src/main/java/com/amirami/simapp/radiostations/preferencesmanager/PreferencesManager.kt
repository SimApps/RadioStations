package com.amirami.simapp.radiostations.preferencesmanager

import android.content.Context
import androidx.datastore.preferences.core.* // ktlint-disable no-wildcard-imports
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_preferences")

data class FilterPreferences(
    val first_timeopen_record_folder: Boolean,
    val first_open: Boolean,
    val dark_theme: Boolean,
    val system_theme: Boolean,
    val switch_timer_data: Boolean,
    val default_country: String,
    val choosen_server: String,
    val save_data: Boolean
)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    //   private val dataStore = context.dataStore
    private val dataStore = context.dataStore

    /* suspend fun setThemeMode(mode: Int) {
         dataStore.edit { settings ->
             settings[Settings.NIGHT_MODE] = mode
         }
     }

     val themeMode: Flow<Int> = dataStore.data.map { preferences ->
         preferences[Settings.NIGHT_MODE] ?: AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
     }
     */

    // used to sort also
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                //  Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val first_timeopen_record_folder = preferences[PreferencesKeys.FIRST_TIMEOPEN_RECORD_FOLDER] ?: true
            val first_open = preferences[PreferencesKeys.FIRST_OPEN] ?: true
            val system_Theme = preferences[PreferencesKeys.SYSTEM_THEME] ?: false
            val switch_Timer_Data = preferences[PreferencesKeys.SWITCH_TIMER_DATA] ?: true
            val default_Country = preferences[PreferencesKeys.DEFAULT_COUNTRY] ?: Locale.getDefault().country
            val choosen_Server = preferences[PreferencesKeys.CHOOSEN_SERVER] ?: "http://91.132.145.114"
            val save_data = preferences[PreferencesKeys.SAVE_DATA] ?: false
            val dark_theme = preferences[PreferencesKeys.DARK_THEME] ?: true

            FilterPreferences(
                first_timeopen_record_folder,
                first_open,
                dark_theme,
                system_Theme,
                switch_Timer_Data,
                default_Country,
                choosen_Server,
                save_data
            )
        }

    suspend fun updateSaveData(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVE_DATA] = value
        }
    }

    suspend fun updateSwitchTimerData(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SWITCH_TIMER_DATA] = value
        }
    }

    suspend fun updateDefaultCountry(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_COUNTRY] = value
        }
    }

    suspend fun updateChoosenServer(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CHOOSEN_SERVER] = value
        }
    }

    suspend fun updateFirstOpen(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_OPEN] = value
        }
    }

    suspend fun updateDarkTheme(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME] = value
        }
    }

    suspend fun updateFirstTimeopenRecordFolder(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_TIMEOPEN_RECORD_FOLDER] = value
        }
    }

    suspend fun updatesystemTheme(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SYSTEM_THEME] = value
        }
    }

    private object PreferencesKeys {
        val FIRST_OPEN = booleanPreferencesKey("first_open")
        val DARK_THEME = booleanPreferencesKey("dark_theme")

        val FIRST_TIMEOPEN_RECORD_FOLDER = booleanPreferencesKey("first_timeopen_record_folder")

        val SYSTEM_THEME = booleanPreferencesKey("system_theme")
        val SWITCH_TIMER_DATA = booleanPreferencesKey("switch_timer_data")
        val SAVE_DATA = booleanPreferencesKey("save_data")

        val DEFAULT_COUNTRY = stringPreferencesKey("default_country")
        val CHOOSEN_SERVER = stringPreferencesKey("choosen_server")
    }
}
