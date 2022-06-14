package com.amirami.simapp.radiostations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.firestore.ProductFirestoreRepository
import com.amirami.simapp.radiostations.model.FavoriteFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@HiltViewModel
class FavoriteFirestoreViewModel @Inject constructor(
    private val repository: ProductFirestoreRepository
): ViewModel() {
    //  private val _dataState: MutableLiveData<RoomDataState<List<Product>>> = MutableLiveData()


    val getAllRadioFavoriteListFromFirestore = liveData(Dispatchers.IO) {
        emit(repository.getAllRadioFavoriteListFromFirestore())

    }


    fun addFavoriteRadioidinArrayFirestore(product: String,lastmodified:Long) = liveData(Dispatchers.IO) {
        if(RadioFunction.getuserid()!="no_user"){
            emit(repository.addFavoriteRadioidInArrayFirestore(product,lastmodified))

        }


    }




    fun deleteFavoriteRadioFromArrayinFirestore(radioUid: String) = liveData(Dispatchers.IO) {
        if(RadioFunction.getuserid()!="no_user"){
            emit(repository.deleteFavoriteRadioFromArrayInFirestore(radioUid))

        }

    }


    fun addUserDocumentInFirestore(favoriteFirestore: FavoriteFirestore) = liveData(Dispatchers.IO) {
        emit(repository.adduserDocumentInFirestore(favoriteFirestore))
    }
}