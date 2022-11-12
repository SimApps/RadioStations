package com.amirami.simapp.radiostations.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// @Entity(tableName = "radio_table" )
@Entity(tableName = "radio_table", indices = [Index(value = ["radiouid", "fav"], unique = true)])

data class RadioEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "radioid")
    var radioid: Int? = null,
    @ColumnInfo(name = "radiouid")
    var radiouid: String? = null,
    @ColumnInfo(name = "radioname")
    var name: String? = null,
    @ColumnInfo(name = "bitrate")
    var bitrate: String? = null,
    @ColumnInfo(name = "homepage")
    var homepage: String? = null,
    @ColumnInfo(name = "imageurl")
    var imageurl: String? = null,
    @ColumnInfo(name = "tags")
    var tags: String? = null,
    @ColumnInfo(name = "country")
    var country: String? = null,
    @ColumnInfo(name = "state")
    var state: String? = null,
    @ColumnInfo(name = "language")
    var language: String? = null,
    @ColumnInfo(name = "streamurl")
    var streamurl: String? = null,
    @ColumnInfo(name = "fav")
    var fav: Boolean? = null
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

        this.radiouid = radiouid
        this.name = name
        this.bitrate = bitrate
        this.homepage = homepage
        this.imageurl = imageurl
        this.tags = tags
        this.country = country
        this.state = state
        this.language = language
        this.streamurl = streamurl
        this.fav = fav
    }

    override fun toString(): String {
        return "MessageThreadListEntity(Id=$radiouid, Name=$name)"
    }
}
