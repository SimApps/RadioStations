package com.amirami.simapp.radiobroadcastpro.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// @Entity(tableName = "radio_table" )
@Entity(tableName = "radio_table", indices = [Index(value = ["stationuuid"], unique = true)])
@Serializable
data class RadioEntity(


    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "stationuuid")
    @SerializedName("stationuuid")
    var stationuuid: String = "",
    @ColumnInfo(name = "radioname")
    @SerializedName("name")
    var name: String = "",
    @ColumnInfo(name = "bitrate")
    @SerializedName("bitrate")
    var bitrate: String = "",
    @ColumnInfo(name = "homepage")
    @SerializedName("homepage")
    var homepage: String = "",
    @ColumnInfo(name = "favicon")
    @SerializedName("favicon")
    var favicon: String = "",
    @ColumnInfo(name = "tags")
    @SerializedName("tags")
    var tags: String = "",
    @ColumnInfo(name = "country")
    @SerializedName("country")
    var country: String = "",
    @ColumnInfo(name = "state")
    @SerializedName("state")
    var state: String = "",
    @ColumnInfo(name = "language")
    @SerializedName("language")
    var language: String = "",
    @ColumnInfo(name = "url_resolved")
    @SerializedName("url_resolved")
    var streamurl: String = "",
    @ColumnInfo(name = "fav")
    var fav: Boolean= false,

    @ColumnInfo(name = "ip")
    @SerializedName("ip")
    var ip: String = "",


    @ColumnInfo(name = "stationcount")
    @SerializedName("stationcount")
    var stationcount: String = "",

    @ColumnInfo(name = "iso_639")
    @SerializedName("iso_639")
    var iso_639: String = "",

    @ColumnInfo(name = "moreinfo")
    @SerializedName("moreinfo")
    var moreinfo: String = "",

    @ColumnInfo(name = "isAlarm")
    var isAlarm: Boolean= false,



    @ColumnInfo(name = "isLastListned")
    var isLastListned: Boolean= false,

    @ColumnInfo(name = "timeStamp")
    var timeStamp : Long? = null,

     @Transient
    var icyState : String? = "",

    @Transient
    var isRec : Boolean = false,
)
