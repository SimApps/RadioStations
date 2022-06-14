package com.amirami.simapp.radiostations.firestore

import com.amirami.simapp.radiostations.data.DataOrException
import com.amirami.simapp.radiostations.RadioFunction.getuserid
import com.amirami.simapp.radiostations.model.FavoriteFirestore
import com.amirami.simapp.radiostations.utils.Constatnts.LAST_DATE_MODIFIED
import com.amirami.simapp.radiostations.utils.Constatnts.RADIO_FAVORITE_ARRAYS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.amirami.simapp.radiostations.utils.Constatnts.RADIO_FAVORITE_COLLECTION
import com.google.firebase.firestore.*

@Singleton
class ProductFirestoreRepository @Inject constructor(
    private val db : FirebaseFirestore
) {

    suspend  fun getAllRadioFavoriteListFromFirestore(): DataOrException<MutableList<ArrayList<String>>, String> {

        val DataOrExceptionProdNames = DataOrException<MutableList<ArrayList<String>>, String>()
        try {
            val productList = mutableListOf<ArrayList<String>>()


            //db.firestoreSettings.isPersistenceEnabled
            val products = disableOfflineMode(false).collection(RADIO_FAVORITE_COLLECTION).document(getuserid ())//.document(RADIO_FAVORITE_COLLECTION)
                //.orderBy(NAME_PROPERTY, Query.Direction.ASCENDING)

                .get().await()

            if(products.exists())
                productList.add(products.get(RADIO_FAVORITE_ARRAYS) as ArrayList<String>)
            else   DataOrExceptionProdNames.e = "error"

            DataOrExceptionProdNames.data = productList
        } catch (e: FirebaseFirestoreException) {
            DataOrExceptionProdNames.e = e.toString()
        }
        return DataOrExceptionProdNames
    }



    suspend    fun addFavoriteRadioidInArrayFirestore(prodName:String,lastmodified:Long): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {

            val products = disableOfflineMode(true).collection(RADIO_FAVORITE_COLLECTION).document(getuserid())//document(RADIO_FAVORITE_COLLECTION)
            //.orderBy(NAME_PROPERTY, Query.Direction.ASCENDING)
        // products.update(RADIO_FAVORITE_ARRAYS, FieldValue.arrayUnion(prodName)).await()
            products.update(RADIO_FAVORITE_ARRAYS, FieldValue.arrayUnion(prodName),LAST_DATE_MODIFIED,lastmodified).await()
           // products.set(favoriteFirestore,SetOptions.merge()).await()
            dataOrException.data = true

        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e.toString()
        }
        return dataOrException
    }




    suspend    fun deleteFavoriteRadioFromArrayInFirestore(prodName:String): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {
            val products = disableOfflineMode(true).collection(RADIO_FAVORITE_COLLECTION).document(getuserid ())//.document(RADIO_FAVORITE_COLLECTION)
            //.orderBy(NAME_PROPERTY, Query.Direction.ASCENDING)
            products.update(RADIO_FAVORITE_ARRAYS, FieldValue.arrayRemove(prodName)).await()

            dataOrException.data = true






        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e.toString()
        }
        return dataOrException
    }


   suspend  fun adduserDocumentInFirestore(favoriteFirestore: FavoriteFirestore): DataOrException<Boolean, String> {
        val dataOrException = DataOrException<Boolean, String>()
        try {


            val products=  disableOfflineMode(false).collection(RADIO_FAVORITE_COLLECTION).document(getuserid ())



            products.set(favoriteFirestore,SetOptions.merge()).await()
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
            dataOrException.e = e.toString()
        }
        return dataOrException
    }

    private fun disableOfflineMode(enableoffline:Boolean/* app crush when change enable offline from true to false*/): FirebaseFirestore {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings

        return db
    }

}