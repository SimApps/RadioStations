package com.amirami.simapp.radiobroadcast.firestore

import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.amirami.simapp.radiobroadcast.RadioFunction.getuserid
import com.amirami.simapp.radiobroadcast.data.DataOrException
import com.amirami.simapp.radiobroadcast.model.FavoriteFirestore
import com.amirami.simapp.radiobroadcast.utils.Constatnts.LAST_DATE_MODIFIED
import com.amirami.simapp.radiobroadcast.utils.Constatnts.RADIO_FAVORITE_ARRAYS
import com.amirami.simapp.radiobroadcast.utils.Constatnts.RADIO_FAVORITE_COLLECTION
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi @Singleton
class ProductFirestoreRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    suspend fun getAllRadioFavoriteListFromFirestore(): DataOrException<MutableList<ArrayList<String>>, String> {
        val dataOrExceptionProdNames = DataOrException<MutableList<ArrayList<String>>, String>()
        try {
            val productList = mutableListOf<ArrayList<String>>()

            // db.firestoreSettings.isPersistenceEnabled
            val products = disableOfflineMode(false)
                .collection(RADIO_FAVORITE_COLLECTION).document(getuserid()) // .document(RADIO_FAVORITE_COLLECTION)
                // .orderBy(NAME_PROPERTY, Query.Direction.ASCENDING)
                .get().await()

            if (products.exists()) {
                productList.add(products.get(RADIO_FAVORITE_ARRAYS) as ArrayList<String>)
            } else dataOrExceptionProdNames.e = "error"

            dataOrExceptionProdNames.data = productList
        } catch (e: FirebaseFirestoreException) {
            dataOrExceptionProdNames.e = e.toString()
        }
        return dataOrExceptionProdNames
    }

    suspend fun addFavoriteRadioidInArrayFirestore(prodName: String, lastmodified: Long): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {
            val products = disableOfflineMode(true).collection(RADIO_FAVORITE_COLLECTION).document(getuserid()) // document(RADIO_FAVORITE_COLLECTION)
            // .orderBy(NAME_PROPERTY, Query.Direction.ASCENDING)
            // products.update(RADIO_FAVORITE_ARRAYS, FieldValue.arrayUnion(prodName)).await()
            products.update(RADIO_FAVORITE_ARRAYS, FieldValue.arrayUnion(prodName), LAST_DATE_MODIFIED, lastmodified).await()
            // products.set(favoriteFirestore,SetOptions.merge()).await()
            dataOrException.data = true
        } catch (e: FirebaseFirestoreException) {

            Log.d("lldssqqqq",e.toString())
            Log.d("lldssqqqq",e.code.name)


            dataOrException.e = e.toString()
        }
        return dataOrException
    }

    suspend fun deleteFavoriteRadioFromArrayInFirestore(prodName: String): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {
            val products = disableOfflineMode(true).collection(RADIO_FAVORITE_COLLECTION).document(getuserid()) // .document(RADIO_FAVORITE_COLLECTION)
            // .orderBy(NAME_PROPERTY, Query.Direction.ASCENDING)
            products.update(RADIO_FAVORITE_ARRAYS, FieldValue.arrayRemove(prodName)).await()

            dataOrException.data = true
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e.toString()
        }
        return dataOrException
    }

    suspend fun adduserDocumentInFirestore(favoriteFirestore: FavoriteFirestore): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {
            val products = disableOfflineMode(false).collection(RADIO_FAVORITE_COLLECTION).document(getuserid())

            products.set(favoriteFirestore, SetOptions.merge()).await()
            dataOrException.data = true

            //  products.get()
            /*    .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if(document != null) {
                        dataOrException.data = true
                        products.set(favoriteFirestore,SetOptions.merge())//.await()
                      /* if (document.exists()) {
                           dataOrException.e = "getRadioUID"
                           products.set(favoriteFirestore,SetOptions.merge())//.await()
                        }
                        else  products.set(FavoriteFirestore(),SetOptions.merge())//.await()
*/

                    }
                }
                else {
                    dataOrException.e = task.exception.toString()
                }
            }*/
        } catch (e: FirebaseFirestoreException) {
            Log.d("lldssqqqq","doc "+e.toString())
            Log.d("lldssqqqq","doc "+e.code.name)
            dataOrException.e = e.toString()
        }
        return dataOrException
    }

    private fun disableOfflineMode(enableoffline: Boolean/* app crush when change enable offline from true to false*/): FirebaseFirestore {
        val settings = FirebaseFirestoreSettings.Builder()
          //  .setPersistenceEnabled(false)
          //  .setLocalCacheSettings(PersistentCacheSettings(1000L))
            .build()
        db.firestoreSettings = settings

        return db
    }

    suspend fun deleteUserDocumentInFirestore(id: String): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {
            disableOfflineMode(false).collection(RADIO_FAVORITE_COLLECTION).document(id).delete().await()
            dataOrException.data = true
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e.toString()
        }
        return dataOrException
    }
}
