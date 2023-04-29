package com.amirami.player_service




data class RadioEntity(

    var stationuuid: String = "",

    var name: String = "",

    var bitrate: String = "",

    var homepage: String = "",

    var favicon: String = "",

    var tags: String = "",

    var country: String = "",

    var state: String = "",

    var language: String = "",

    var streamurl: String = "",

    var fav: Boolean= false,


    var ip: String = "",


    var stationcount: String = "",


    var iso_639: String = "",


    var moreinfo: String = "",

    var isAlarm: Boolean= false,


    var isLastListned: Boolean= false,

    var timeStamp : Long? = null,


    var icyState : String? = "",
)
