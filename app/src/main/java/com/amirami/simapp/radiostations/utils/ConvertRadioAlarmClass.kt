package com.amirami.simapp.radiostations.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.amirami.simapp.radiostations.alarm.AlarmRadioRoomEntity
import com.amirami.simapp.radiostations.alarm.RadioAlarmRoom

object ConvertRadioAlarmClass {
    private fun toListModel(prodNamesEntity: List<AlarmRadioRoomEntity>): MutableList<RadioAlarmRoom> {
        val itemList: MutableList<RadioAlarmRoom> = mutableListOf<RadioAlarmRoom>()
        prodNamesEntity.map {
            itemList.add(
                RadioAlarmRoom(
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
                    it.moreinfo ?: ""
                )
            )
        }
        return itemList
    }

    fun toLiveDataListModel(localList: LiveData<List<AlarmRadioRoomEntity>>): LiveData<MutableList<RadioAlarmRoom>> {
        return Transformations.map<
            List<AlarmRadioRoomEntity>, // localList Data Type
            MutableList<RadioAlarmRoom> // toListModel List Data Type
            >(
            localList
        ) { listEntity ->
            toListModel(listEntity)
        }
    }

    fun toEntity(radioRoomModel: RadioAlarmRoom): AlarmRadioRoomEntity {
        return when (radioRoomModel.radiouid) {
            else -> {
                AlarmRadioRoomEntity(
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
                    radioRoomModel.moreinfo
                )
            }
        }
    }
}
