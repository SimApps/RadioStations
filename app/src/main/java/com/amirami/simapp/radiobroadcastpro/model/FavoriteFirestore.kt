package com.amirami.simapp.radiobroadcastpro.model

import com.amirami.simapp.radiobroadcastpro.RadioFunction

data class FavoriteFirestore(
    var user_id: String= RadioFunction.getuserid(),
    var radio_favorites_list: ArrayList<String> = ArrayList(),
    var last_date_modified: Long= RadioFunction.getCurrentDate()
)
