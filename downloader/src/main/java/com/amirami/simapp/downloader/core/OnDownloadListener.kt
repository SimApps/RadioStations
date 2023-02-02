package com.amirami.simapp.downloader.core

import java.io.File


interface OnDownloadListener {
    fun onStart()
    fun onPause()
    fun onResume()
    fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int)
    fun onCompleted(file: File?)
    fun onFailure(reason: String?)
    fun onCancel()
}
