package com.amirami.simapp.radiostations.pairalarm.model

data class Failure(
    val error: Throwable,
    val retry: (() -> Unit)? = null
)