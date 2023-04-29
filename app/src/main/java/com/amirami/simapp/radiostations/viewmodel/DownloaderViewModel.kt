package com.amirami.simapp.radiostations.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.amirami.simapp.downloader.Downloader
import com.amirami.simapp.downloader.core.OnDownloadListener
import com.amirami.simapp.radiostations.DefaultDispatcher
import com.amirami.simapp.radiostations.IoDispatcher
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.hiltcontainer.RadioApplication
import com.amirami.simapp.radiostations.model.DownloadState
import com.amirami.player_service.service.SimpleMediaServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@UnstableApi @HiltViewModel
class DownloaderViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    app: Application
) : AndroidViewModel(app) {


    private var customDownloader: Downloader? = null



    private var _downloadState = MutableStateFlow(DownloadState())
    val downloadState = _downloadState.asStateFlow()




    fun cancelDownloader(){
        if (_downloadState.value.isDownloading) {
            customDownloader?.cancelDownload()


            _downloadState.value =     DownloadState(
                isDownloading = false,
                downloadResumed = false,
                downloadStarted = false,
                isPaused = false,
                error = "",
                completed = true
            )
        }


    }
    fun startDownloader(customRecName : String = "", customUrl : String = "", icyandState: String = "") {
        Log.d("kkkkee",customUrl)
        if(customUrl=="" && MainActivity.Globalurl =="") return
        viewModelScope.launch(dispatcher) {

            val name = if(customRecName=="")  MainActivity.GlobalRadioName else customRecName
            var recordFileName =
                name + "_ _" + " " + icyandState + "___" + System.currentTimeMillis()
            recordFileName = recordFileName.replace(Regex("[\\\\/:*?\"<>|]"), " ")

            //  val sdfDate = SimpleDateFormat("MMM d yy_HH-mm-ss", Locale.getDefault())
            //  var recordFileName= GlobalRadioName + "_ _" + icyandState + " " + sdfDate.format(Date())

             customDownloader = Downloader.Builder(getApplication<RadioApplication>(),if(customUrl=="") MainActivity.Globalurl else customUrl)
                .downloadListener(object : OnDownloadListener {
                    override fun onStart() {
                        Log.d("kkkkee","onStart")
                        _downloadState.value =     DownloadState(
                            isDownloading = true,
                            downloadResumed = false,
                            downloadStarted = true,
                            isPaused = false,
                            error = "",
                            completed = false
                        )
                    }

                    override fun onPause() {
                        Log.d("kkkkee","onPause")
                        _downloadState.value =     DownloadState(
                            isDownloading = false,
                            downloadResumed = false,
                            downloadStarted = false,
                            isPaused = true,
                            error = "",
                            completed = false
                        )


                    }

                    override fun onResume() { Log.d("kkkkee","onResume")
                        _downloadState.value =     DownloadState(
                            isDownloading = true,
                            downloadResumed = true,
                            downloadStarted = false,
                            isPaused = false,
                            error = "",
                            completed = false
                        )


                    }

                    override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                      //  Log.d("kkkkee","onProgressUpdate")

                        _downloadState.value =     DownloadState(
                            isDownloading = true,
                            downloadResumed = false,
                            downloadStarted = false,
                            isPaused = false,
                            error = "",
                            completed = false
                        )
                        //  current_status_txt.text = "onProgressUpdate"
                        //   percent_txt.text = percent.toString().plus("%")
                        //   size_txt.text = getSize(downloadedSize)
                        //   total_size_txt.text = getSize(totalSize)
                        //  download_progress.progress = percent

                    }

                    override fun onCompleted(file: File?) {
                        Log.d("kkkkee","onCompleted")
                        _downloadState.value =     DownloadState(
                            isDownloading = false,
                            downloadResumed = false,
                            downloadStarted = false,
                            isPaused = false,
                            error = "",
                            completed = true
                        )


                    }

                    override fun onFailure(reason: String?) {
                        Log.d("kkkkee",reason ?: "Unkown Download Error")
                        _downloadState.value =     DownloadState(
                            isDownloading = false,
                            downloadResumed = false,
                            downloadStarted = false,
                            isPaused = false,
                            error = reason ?: "Unkown Download Error",
                            completed = true
                        )

                    }

                    override fun onCancel() {
                        Log.d("kkkkee","onCancel")
                        _downloadState.value =     DownloadState(
                            isDownloading = false,
                            downloadResumed = false,
                            downloadStarted = false,
                            isPaused = false,
                            error = "",
                            completed = true
                        )

                    }
                }).fileName(recordFileName).downloadDirectory(
                    RadioFunction.getDownloadDir().toString())
                .build()

           customDownloader?.download()
        }


    }


    fun resetDownloadState(){
        _downloadState.value =     DownloadState(
             isDownloading = false,
         downloadStarted = false,
         downloadResumed = false,
         completed  = false,
         isPaused  = false,
         error  = "",
        )
    }
}