package com.amirami.simapp.radiostations.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// @Entity(tableName = "radio_table" )
@Entity(tableName = "radio_table", indices = [Index(value = ["stationuuid", "fav"], unique = true)])
data class RadioEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "radioid")
    @SerializedName("radioID")
    var radioID: Int = 0,
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
    var moreinfo: String = ""
) {

    constructor(
        radiouid: String,
        name: String,
        bitrate: String,
        homepage: String,
        imageurl: String,
        tags: String,
        country: String,
        state: String,
        language: String,
        streamurl: String,
        fav: Boolean
    ) : this() {

        this.stationuuid = radiouid
        this.name = name
        this.bitrate = bitrate
        this.homepage = homepage
        this.favicon = imageurl
        this.tags = tags
        this.country = country
        this.state = state
        this.language = language
        this.streamurl = streamurl
        this.fav = fav
    }

    override fun toString(): String {
        return "MessageThreadListEntity(Id=$stationuuid, Name=$name)"
    }
}
