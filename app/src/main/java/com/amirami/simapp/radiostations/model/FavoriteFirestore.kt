package com.amirami.simapp.radiostations.model

import com.amirami.simapp.radiostations.RadioFunction

data class FavoriteFirestore(
    var user_id: String= RadioFunction.getuserid(),
    var radio_favorites_list: ArrayList<String> = ArrayList(),
    var last_date_modified: Long= RadioFunction.getCurrentDate()
)
