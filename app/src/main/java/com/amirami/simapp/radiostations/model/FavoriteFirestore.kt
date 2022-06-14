package com.amirami.simapp.radiostations.model

data class FavoriteFirestore (
    var user_id:String,
    var radio_favorites_list: ArrayList<String> = ArrayList(),
    var last_date_modified:Long
        )
