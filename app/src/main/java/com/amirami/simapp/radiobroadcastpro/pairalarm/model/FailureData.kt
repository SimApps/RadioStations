package com.amirami.simapp.radiobroadcastpro.pairalarm.model

data class Failure(
    val error: Throwable,
    val retry: (() -> Unit)? = null
)