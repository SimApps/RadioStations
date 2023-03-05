package com.amirami.simapp.downloader

/**
 * Author:  Alireza Tizfahm Fard
 * Date:    21/6/2019
 * Email:   alirezat775@gmail.com
 */

internal interface IDownload {
   suspend fun download()
    fun cancelDownload()
    fun pauseDownload()
    suspend  fun resumeDownload()
}