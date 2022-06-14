package com.amirami.simapp.radiostations.data

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
    private val dataStore = context.dataStore


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
            val dateListNameRefresh = preferences[PreferencesKeys.Date_LIST_NAME_REFRESH] ?:  "01/08/2021"
            val firstOpen = preferences[PreferencesKeys.FIRST_OPEN] ?: true
            val nbrInterstitialAdShowed = preferences[PreferencesKeys.NBR_INTERSTITIAL_AD_SHOWED] ?:  0

            val lastShopinglistRefresh = preferences[PreferencesKeys.LAST_SHOPINGLIST_REFRESH] ?:  "01/08/2021"
            FilterPreferences(
                dateListNameRefresh,
                firstOpen,
                lastShopinglistRefresh,
                nbrInterstitialAdShowed
            )
        }


    private object PreferencesKeys {
        val FIRST_OPEN = booleanPreferencesKey("first_open")
        val LAST_SHOPINGLIST_REFRESH = stringPreferencesKey("last_shopinglist_refresh")

        val Date_LIST_NAME_REFRESH = stringPreferencesKey("date_list_name_refresh")
        val NBR_INTERSTITIAL_AD_SHOWED = intPreferencesKey("nbr_Interstitial_Ad_Showed")


    }
}
