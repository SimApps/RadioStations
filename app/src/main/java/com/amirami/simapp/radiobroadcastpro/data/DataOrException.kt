package com.amirami.simapp.radiobroadcastpro.data

data class DataOrException<T, E : String?>(
    var data: T? = null,
    var e: String? = null // E? = null
)
