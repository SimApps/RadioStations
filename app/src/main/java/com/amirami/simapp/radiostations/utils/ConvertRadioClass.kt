package com.amirami.simapp.radiostations.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.room.RadioEntity

object ConvertRadioClass {
    private fun toListModel(prodNamesEntity: List<RadioEntity>): MutableList<RadioRoom> {
        val itemList: MutableList<RadioRoom> = mutableListOf<RadioRoom>()
        prodNamesEntity.map {
            itemList.add(
                RadioRoom(
                    it.radiouid ?: "",
                    it.name ?: "",
                    it.bitrate ?: "",
                    it.homepage ?: "",
                    it.imageurl ?: "",
                    it.tags ?: "",
                    it.country ?: "",
                    it.state ?: "",
                    it.language ?: "",
                    it.streamurl ?: "",
                    it.fav!!
                )
            )
        }
        return itemList
    }

    fun toLiveDataListModel(localList: LiveData<List<RadioEntity>>): LiveData<MutableList<RadioRoom>> {
        return Transformations.map<
            List<RadioEntity>, // localList Data Type
            MutableList<RadioRoom> // toListModel List Data Type
            >(
            localList
        ) { listEntity ->
            toListModel(listEntity)
        }
    }

    fun toEntity(radioRoomModel: RadioRoom): RadioEntity {
        return when (radioRoomModel.radiouid) {
            else -> {
                RadioEntity(
                    radioRoomModel.radiouid,
                    radioRoomModel.name,
                    radioRoomModel.bitrate,
                    radioRoomModel.homepage,
                    radioRoomModel.favicon,
                    radioRoomModel.tags,
                    radioRoomModel.country,
                    radioRoomModel.state,
                    // var RadiostateDB: String?,
                    radioRoomModel.language,
                    radioRoomModel.streamurl,
                    radioRoomModel.fav

                )
            }
        }
    }
}
