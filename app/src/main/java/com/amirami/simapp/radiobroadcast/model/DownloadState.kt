package com.amirami.simapp.radiobroadcast.model

data class DownloadState(
    var isDownloading:Boolean = false,
    var downloadStarted:Boolean = false,
    var downloadResumed:Boolean = false,
    var completed:Boolean  = false,
    var isPaused:Boolean  = false,
    var error:String  = "",


)
