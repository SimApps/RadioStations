package com.amirami.simapp.priceindicatortunisia.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton



private val Context.dataStore by preferencesDataStore("user_preferences")


data class FilterPreferences(val date_list_name_refresh: String,
                             val first_open: Boolean,
                             val last_shopinglist_refresh: String,
                             val nbr_Interstitial_Ad_Showed:Int)

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
            val date_list_name_refresh = preferences[PreferencesKeys.Date_LIST_NAME_REFRESH] ?:  "01/08/2021"
            val first_open = preferences[PreferencesKeys.FIRST_OPEN] ?: true
            val nbr_Interstitial_Ad_Showed = preferences[PreferencesKeys.NBR_INTERSTITIAL_AD_SHOWED] ?:  0

            val last_shopinglist_refresh = preferences[PreferencesKeys.LAST_SHOPINGLIST_REFRESH] ?:  "01/08/2021"
            FilterPreferences(
                date_list_name_refresh,
                first_open,
                last_shopinglist_refresh,
                nbr_Interstitial_Ad_Showed
            )
        }


    suspend fun updateFirstOpen(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_OPEN] = value
        }
    }

    suspend fun updateLastShopingRefresh(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SHOPINGLIST_REFRESH] = value
        }
    }

    suspend fun ListNameRefresh(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.Date_LIST_NAME_REFRESH] = value
        }
    }

    suspend fun nbrInterstitialAdShowed(value: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NBR_INTERSTITIAL_AD_SHOWED] = value
        }
    }








    private object PreferencesKeys {
        val FIRST_OPEN = booleanPreferencesKey("first_open")
        val LAST_SHOPINGLIST_REFRESH = stringPreferencesKey("last_shopinglist_refresh")

        val Date_LIST_NAME_REFRESH = stringPreferencesKey("date_list_name_refresh")
        val NBR_INTERSTITIAL_AD_SHOWED = intPreferencesKey("nbr_Interstitial_Ad_Showed")


    }
}
