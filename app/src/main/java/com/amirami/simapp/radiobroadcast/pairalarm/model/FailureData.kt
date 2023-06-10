package com.amirami.simapp.radiobroadcast.pairalarm.model

data class Failure(
    val error: Throwable,
    val retry: (() -> Unit)? = null
)