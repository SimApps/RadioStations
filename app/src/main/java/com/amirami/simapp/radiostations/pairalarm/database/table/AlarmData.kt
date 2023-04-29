package com.amirami.simapp.radiostations.pairalarm.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.amirami.simapp.radiostations.model.RadioEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO: 각 파라미터 의미 적어놓기
@Entity(tableName = "alarm_data")
data class AlarmData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int?,
    @ColumnInfo(name = "onOff") var alarmIsOn: Boolean = false,
    @ColumnInfo(name = "Sun") var Sun: Boolean = false,
    @ColumnInfo(name = "Mon") var Mon: Boolean = false,
    @ColumnInfo(name = "Tue") var Tue: Boolean = false,
    @ColumnInfo(name = "Wed") var Wed: Boolean = false,
    @ColumnInfo(name = "Thu") var Thu: Boolean = false,
    @ColumnInfo(name = "Fri") var Fri: Boolean = false,
    @ColumnInfo(name = "Sat") var Sat: Boolean = false,
    // hour은 24시간 형식으로 저장됨
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int,
    @ColumnInfo(name = "volume") var volume: Int,
    @ColumnInfo(name = "vibration") var vibration: Int,
    @ColumnInfo(name = "quick") var quick: Boolean,
    @ColumnInfo(name = "bell") var bell: Int,
    @ColumnInfo(name = "mode") var mode: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "alarmCode") var alarmCode: String,
    @ColumnInfo(name = "radio") var radio: RadioEntity
)

enum class Weekend{
    SAT,
    SUN,
    WEEK
}


class RadioConverter{
 @TypeConverter
 fun radioToString(radio : RadioEntity):String{

     return Json.encodeToString(radio)
 }


    @TypeConverter
    fun stringToRadio(string : String):RadioEntity{
        return  Json.decodeFromString(string)

    }
}