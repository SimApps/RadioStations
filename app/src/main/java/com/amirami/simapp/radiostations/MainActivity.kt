package com.amirami.simapp.radiostations

import alirezat775.lib.downloader.Downloader
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.RadioFunction.parseColor
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.RadioFunction.startServices
import com.amirami.simapp.radiostations.adapter.RadioFavoriteAdapterHorizantal
import com.amirami.simapp.radiostations.databinding.ActivityContentMainBinding
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.preferencesmanager.PreferencesViewModel
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RadioRoomViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.io.IOException

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RadioFavoriteAdapterHorizantal.OnItemClickListener {


    var populateFavRv = true //temoprary mesure

    private lateinit var radioFavoriteAdapterVertical: RadioFavoriteAdapterHorizantal
    private var isExpanded = false

    private var videoPosition: Long = 0L

    var stationuuid: String = ""

    private lateinit var adViewSmallActivityplayer: NativeAdView


    private lateinit var adViewAdaptiveBanner: AdView

    private val infoViewModel: InfoViewModel by viewModels()
    private val preferencesViewModel: PreferencesViewModel by viewModels()
    private val radioRoomViewModel: RadioRoomViewModel by viewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by viewModels()

    private val radioRoom: MutableList<RadioRoom> = mutableListOf()

    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {

            //
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this.display
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }
            val outMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // val display = activity.display
                display?.getRealMetrics(outMetrics)
            } else {
                //  val display = activity.windowManager.defaultDisplay
                @Suppress("DEPRECATION")
                display?.getMetrics(outMetrics)
            }

            // display!!.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adAdaptivebannerMain.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }
    private lateinit var binding: ActivityContentMainBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityContentMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        getPref()
        setTheme()


        setDataConsumption()
        subsucribers()
        setPlayerBottomSheet()
        loadBannerAD()

        setTitleText()
        getLastPlayedRadio()
        getFavRadioRoom()
        searchquerry()




        binding.searchView.settingButton.setSafeOnClickListener {
            opensettingfrag()
        }
        binding.searchView.opencloseSearchButton.setSafeOnClickListener {
            if (binding.searchView.searchInputText.isVisible) {
                closeSearch()
                closesearchfrag()
            } else openSearch()

        }

        binding.searchView.addAlarmButton.setSafeOnClickListener {
            // showCountdownTimerPopup()
            openCountdownTimer()
        }



        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // This happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                infoViewModel.putRadioPlayerInfo.collectLatest { radioVar ->

                    if (Exoplayer.player != null && !Exoplayer.is_playing_recorded_file) {
                        val radioroom = RadioRoom(
                            radioVar.stationuuid,
                            radioVar.name,
                            radioVar.bitrate,
                            radioVar.homepage,
                            radioVar.favicon,
                            radioVar.tags,
                            radioVar.country,
                            radioVar.state,
                            //var RadiostateDB: String?,
                            radioVar.language,
                            radioVar.url_resolved,
                            false
                        )
                        radioRoomViewModel.upsertRadio(radioroom, "Radio added")
                        radioRoomViewModel.deletelistened(false, "Radio listened deleted")

                    }
                    stationuuid = radioVar.stationuuid


                    GlobalImage = radioVar.favicon
                    GlobalRadioName = radioVar.name

                    startServices(this@MainActivity)





                    binding.radioplayer.RadioNameImVFrag.isSelected = true
                    binding.radioplayer.RadioNameImVFrag.text = radioVar.name



                    if (!Exoplayer.is_playing_recorded_file) {
                        RadioFunction.loadImageString(
                            this@MainActivity,
                            radioVar.favicon,
                            imagedefaulterrorurl,
                            binding.radioplayer.RadioImVFragBig
                        )
                        RadioFunction.loadImageString(
                            this@MainActivity,
                            radioVar.favicon,
                            imagedefaulterrorurl,
                            binding.radioplayer.RadioImVFrag
                        )
                        //fav icon begin

                        RadioFunction.loadImageString(
                            this@MainActivity,
                            radioVar.favicon,
                            imagedefaulterrorurl,
                            binding.radioplayer.RadioImVFrag
                        )
                        RadioFunction.loadImageString(
                            this@MainActivity,
                            radioVar.favicon,
                            imagedefaulterrorurl,
                            binding.radioplayer.RadioImVFragBig
                        )
                    }
                    else {
                        binding.radioplayer.RadioImVFragBig.setImageResource(R.drawable.rec_on)
                        binding.radioplayer.RadioImVFrag.setImageResource(R.drawable.rec_on)

                        //maybe change maun faiv image like in mini palayer
                        binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_recordings_folder)

                        binding.radioplayer.RadioImVFrag.setImageResource(R.drawable.rec_on)
                        //  RadioFunction.loadImageInt(R.drawable.recordings, imagedefaulterrorurl, binding.radioplayer.RadioImVFrag)
                    }


                    binding.radioplayer.likeImageViewPlayermain.setOnClickListener {
                        if (!Exoplayer.is_playing_recorded_file)
                            favRadio(
                                radioVar.favicon,
                                radioVar.name,
                                radioVar.homepage,
                                radioVar.url_resolved,
                                radioVar.country,
                                radioVar.bitrate,
                                radioVar.language,
                                radioVar.tags,
                                radioVar.stationuuid,
                                radioVar.state
                            )
                        else DynamicToast.makeError(
                            this@MainActivity,
                            " Recorded files cannot be added to favorite radio ",
                            3
                        ).show()
                    }
                    binding.radioplayer.likeImageView.setOnClickListener {
                        if (!Exoplayer.is_playing_recorded_file)
                            favRadio(
                                radioVar.favicon,
                                radioVar.name,
                                radioVar.homepage,
                                radioVar.url_resolved,
                                radioVar.country,
                                radioVar.bitrate,
                                radioVar.language,
                                radioVar.tags,
                                radioVar.stationuuid,
                                radioVar.state
                            )
                        else {
                            if (firstTimeopenRecordfolder) {
                                firstTimeopenRecordfolder = false
                                if (!isFinishing) {
                                    val navController = Navigation.findNavController(
                                        this@MainActivity,
                                        R.id.fragment_container
                                    )
                                    navController.navigateUp()
                                    val bundle = bundleOf(
                                        "title" to getString(R.string.Keep_in_mind),
                                        "msg" to getString(R.string.recordmessage)
                                    )
                                    navController.navigate(R.id.infoBottomSheetFragment, bundle)
                                }

                                preferencesViewModel.onFirstTimeopenRecordFolderChanged(
                                    firstTimeopenRecordfolder
                                )
                            }
                            RadioFunction.open_record_folder(this@MainActivity)


                        }
                        //getRadioRoom()
                        //infoViewModel.putRadiopalyerInfo(radioVar)
                    }

                    binding.radioplayer.moreButtons.setSafeOnClickListener {
                        //     val radioVars = radioVar

                        radioVar.moreinfo = "fromplayer"
                        infoViewModel.putRadioInfo(radioVar)
                        val navController =
                            Navigation.findNavController(this@MainActivity, R.id.fragment_container)
                        // navController.navigateUp()
                        navController.navigate(R.id.moreBottomSheetFragment)


                    }

                    binding.radioplayer.radioInfotxV.setSafeOnClickListener {
                        if (binding.radioplayer.radioInfotxV.text.toString().isNotEmpty()
                            && binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.playernullinfo)
                            && binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.BUFFERING)
                            && binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.OoOps_Try_another_station)
                        ) {

                            RadioFunction.copytext(
                                this@MainActivity,
                                binding.radioplayer.radioInfotxV.text.toString()
                            )

                           googleSearch()
                        }

                    }

                    binding.radioplayer.stopButtonMain.setOnClickListener {
                        if (isDownloadingCustomurl) {
                            customdownloader?.cancelDownload()
                            binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                        } else {
                            if (Exoplayer.player != null) {
                                if (!Exoplayer.is_downloading) {
                                    binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                                    binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
                                    RadioFunction.stopService(this@MainActivity)
                                    Exoplayer.releasePlayer()
                                } else {
                                    downloader?.cancelDownload()
                                    binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
                                }



                                binding.radioplayer.videoView.player = null

                                if (video_on || Exoplayer.is_playing_recorded_file) {

                                    binding.radioplayer.videoView.player = Exoplayer.player
                                    binding.radioplayer.RadioImVFragBig.visibility = View.GONE
                                    binding.radioplayer.videoView.visibility = View.VISIBLE

                                } else {
                                    binding.radioplayer.videoView.visibility = View.INVISIBLE
                                    binding.radioplayer.RadioImVFragBig.visibility = View.VISIBLE

                                    RadioFunction.loadImageString(
                                        this@MainActivity,
                                        radioVar.favicon,
                                        imagedefaulterrorurl,
                                        binding.radioplayer.RadioImVFragBig
                                    )
                                }

                            }
                        }

                        /*     if(ExoPlayer.player !=null){
                            RadioFunction.startServices(this@MainActivity)
                        }*/
                    }
                    binding.radioplayer.stopButton.setSafeOnClickListener {
                        if (isDownloadingCustomurl) {
                            customdownloader?.cancelDownload()
                            binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                        }
                        else
                            if (Exoplayer.player != null) {
                                if (!Exoplayer.is_downloading) {
                                    binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                                    binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
                                    RadioFunction.stopService(this@MainActivity)
                                    Exoplayer.releasePlayer()
                                } else {
                                    downloader?.cancelDownload()
                                    binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
                                }



                                binding.radioplayer.videoView.player = null

                                if (video_on || Exoplayer.is_playing_recorded_file) {

                                    binding.radioplayer.videoView.player = Exoplayer.player
                                    binding.radioplayer.RadioImVFragBig.visibility = View.GONE
                                    binding.radioplayer.videoView.visibility = View.VISIBLE

                                } else {
                                    binding.radioplayer.videoView.visibility = View.INVISIBLE
                                    binding.radioplayer.RadioImVFragBig.visibility = View.VISIBLE

                                    RadioFunction.loadImageString(
                                        this@MainActivity,
                                        radioVar.favicon,
                                        imagedefaulterrorurl,
                                        binding.radioplayer.RadioImVFragBig
                                    )
                                }

                            }
                    }

                    binding.radioplayer.pauseplayButtonMain.setOnClickListener {

                        if (Exoplayer.player != null && GlobalstateString == "Player.STATE_PAUSED") {
                            Exoplayer.startPlayer()
                            //    seekbarUpdate()
                            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)

                        } else if (Exoplayer.player == null) {
                            if (Exoplayer.is_playing_recorded_file) {
                                Exoplayer.initializePlayerRecodedradio(this@MainActivity)
                                //    seekbarUpdate()
                            } else Exoplayer.initializePlayer(this@MainActivity)

                            Exoplayer.startPlayer()

                            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)


                            if (video_on || Exoplayer.is_playing_recorded_file) {
                                binding.radioplayer.videoView.player = Exoplayer.player
                                binding.radioplayer.RadioImVFragBig.visibility = View.GONE
                                binding.radioplayer.videoView.visibility = View.VISIBLE
                            } else {
                                binding.radioplayer.videoView.visibility = View.GONE
                                binding.radioplayer.RadioImVFragBig.visibility = View.VISIBLE
                            }
                        } else if (Exoplayer.player != null && GlobalstateString == "Player.STATE_READY") {
                            //  seekbarUpdate()
                            Exoplayer.pausePlayer(this@MainActivity)
                            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                            binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
                        }
                    }

                    binding.radioplayer.pauseplayButton.setOnClickListener {
                        DynamicToast.makeError(
                            this@MainActivity,
                            ff+GlobalstateString + Exoplayer.playWhenReady.toString(),
                            3
                        ).show()
                         if (Exoplayer.player != null && GlobalstateString == "Player.STATE_PAUSED" /*&& GlobalRadiourl!=""*/) {

                            Exoplayer.startPlayer()
                            //   seekbarUpdate()
                            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)
                            //binding.radioplayer.pauseplayButton.setImageResource(R.drawable.pause_2)

                        }
                        else if (Exoplayer.player == null  /*&& GlobalRadiourl!=""*/) {
                            if (Exoplayer.is_playing_recorded_file) {
                                Exoplayer.initializePlayerRecodedradio(this@MainActivity)
                                //   seekbarUpdate()
                            } else {
                                Exoplayer.initializePlayer(this@MainActivity)
                                // video_view.player = player

                            }

                            Exoplayer.startPlayer()

                            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)


                            if (video_on || Exoplayer.is_playing_recorded_file) {
                                binding.radioplayer.videoView.player = Exoplayer.player
                                binding.radioplayer.RadioImVFragBig.visibility = View.GONE
                                binding.radioplayer.videoView.visibility = View.VISIBLE
                            } else {
                                binding.radioplayer.videoView.visibility = View.INVISIBLE
                                binding.radioplayer.RadioImVFragBig.visibility = View.VISIBLE
                            }
                        }
                        else if (Exoplayer.player != null && GlobalstateString == "Player.STATE_READY" /*&& GlobalRadiourl!=""*/) {
                            //    seekbarUpdate()
                            Exoplayer.pausePlayer(this@MainActivity)
                            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                            binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)

                        }
                    }

                    binding.radioplayer.recordOffONButton.setOnClickListener {

                        if (Exoplayer.is_downloading) {
                            downloader?.cancelDownload()
                            binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                            binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
                        } else {
                            if (video_on || Exoplayer.is_playing_recorded_file) {
                                DynamicToast.makeError(
                                    this@MainActivity,
                                    getString(R.string.VideoRecordNotAvailable),
                                    6
                                ).show()
                            }
                            if (Exoplayer.is_playing_recorded_file) {
                                DynamicToast.makeError(
                                    this@MainActivity,
                                    getString(R.string.cantRecordArecordedStream),
                                    6
                                ).show()
                            }
                            if (Exoplayer.player != null && GlobalstateString == "Player.STATE_READY" && !video_on && !Exoplayer.is_playing_recorded_file) {

                                if (!isDownloadingCustomurl) {

                                    RadioFunction.getDownloader(this@MainActivity)

                                    downloader?.download()

                                    if (Exoplayer.is_downloading) {
                                        binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_on)
                                        binding.radioplayer.stopButton.setImageResource(R.drawable.rec_on)

                                    }
                                    RadioFunction.interatial_ads_show(this@MainActivity)
                                } else {
                                    DynamicToast.makeError(
                                        this@MainActivity,
                                        getString(R.string.cantrecordwhendownload),
                                        6
                                    ).show()
                                }
                            }
                        }

                    }


                    //    savedInstanceState?.let { videoPosition = savedInstanceState.getLong(argVideoPosition) }

                    Exoplayer.Observer.changeText("Main text view", RadioFunction.infoString())
                    Exoplayer.Observer.changeText("text view", RadioFunction.infoString())


                    if (video_on || Exoplayer.is_playing_recorded_file) {
                        binding.radioplayer.videoView.player = Exoplayer.player
                        binding.radioplayer.RadioImVFragBig.visibility = View.GONE
                        binding.radioplayer.videoView.visibility = View.VISIBLE
                    } else {
                        binding.radioplayer.videoView.visibility = View.INVISIBLE
                        binding.radioplayer.RadioImVFragBig.visibility = View.VISIBLE
                    }
                }

            }
        }

        lifecycleScope.launch {
            //  repeatOnLifecycle(Lifecycle.State.STARTED) { THIS IS NOT GOOD §§§§§§§§§§§§§

            infoViewModel.putTimer.collectLatest {
                //    DynamicToast.makeSuccess(this@MainActivity, "intent", 9).show()
                when (it) {
                    1 -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)
                        Exoplayer.releasePlayer()
                        infoViewModel.stoptimer()
                    }
                    -1 -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)
                    }
                    else -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.time_left)
                    }
                }
            }
            //   }
        }



        lifecycleScope.launch {
            //   repeatOnLifecycle(Lifecycle.State.STARTED) {

            infoViewModel.putDataConsumptionTimer.collectLatest {
                //      DynamicToast.makeError(this@MainActivity, it, 1).show()
                when {
                    it == -1L -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)

                    }
                    it < 0L -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)
                        Exoplayer.releasePlayer()
                        infoViewModel.stopdatatimer()
                    }
                    else -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.time_left)
                    }
                }
            }
            //  }
        }



        AppRater.applaunched(this@MainActivity)

    }


    private fun transitionBottomSheetBackgroundColor(slideOffset: Float) {
        val colorFrom: Int
        val colorTo: Int
        if (darkTheme) {
            colorFrom = parseColor("#000000")
            colorTo = parseColor("#070326")
        } else {
            colorFrom = parseColor("#FFFFFF")
            colorTo = parseColor("#F0FFFF")
        }

        binding.radioplayer.containermainplayer.setBackgroundColor(
            interpolateColor(
                slideOffset,
                colorFrom,
                colorTo
            )
        )
    }

    private fun interpolateColor(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff
        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff
        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }

    companion object {
        var color1 = parseColor("#03071e")//-256
        var color2 = parseColor("#03071e")//-65536
        var color3 = parseColor("#03071e")
        var color4 = parseColor("#03071e")
        var darkTheme = true
        var systemTheme = true

        var saveData = false

        var time = true

        var mInterstitialAd: InterstitialAd? = null
        const val REQUEST_CODE_PERMISSIONS = 101
        val REQUIRED_PERMISSIONS = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )

        var isDownloadingCustomurl = false
        var repeat_tryconnect_server = -1
        var server_arraylist = arrayOf(
            "http://91.132.145.114",
            "http://45.77.62.161",
            "http://95.179.139.106",
            "http://all.api.radio-browser.info"
        )
        var downloader: Downloader? = null
        var customdownloader: Downloader? = null

        val handlers: Handler = Handler(Looper.getMainLooper())// Handler()

        var BASE_URL = "http://91.132.145.114"

        //var jsonCall: Call<List<RadioVariables>>? = null


        var GlobalRadioName = ""
        var GlobalRadiourl = ""


        const val argVideoPosition = "VideoActivity.POSITION"

        var video_on = false

        var GlobalImage = ""
        var GlobalstateString = ""
var ff=""
        const val tag = "MainActivity"

        var defaultCountry = ""

        var initial_data_consumed: Long = 0L

        var scale: Float = 0.0F
        var data = 0L
        var icyandState = ""
        var icybackup = ""
        var currentNativeAd: NativeAd? = null
        var firstTimeOpened = false
        var firstTimeopenRecordfolder = true
        var imagedefaulterrorurl = R.drawable.radioerror

    }


    private fun loadBanner() {
        adViewAdaptiveBanner.adUnitId = resources.getString(R.string.adaptive_banner_adUnitId)

        adViewAdaptiveBanner.adSize = adSize
        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest
            .Builder()
            // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build()
        // Start loading the ad in the background.
        adViewAdaptiveBanner.loadAd(adRequest)
    }


    public override fun onResume() {
/*
        if ((video_on || Exoplayer.is_playing_recorded_file) && Exoplayer.player != null)
            binding.radioplayer.videoView.player = Exoplayer.player
*/
        //SMALL PLAYER END

        adViewAdaptiveBanner.resume()

        //Makes sure that the media controls pop up on resuming and when going between PIP and non-PIP states.

        if (Exoplayer.player != null) {
            Exoplayer.player!!.addAnalyticsListener(Exoplayer.Analyticslistener)
            if (video_on || Exoplayer.is_playing_recorded_file) {
                /*  if (videoPosition > 0L/* && !isInPipMode*/) {
                      Exoplayer.player!!.seekTo(videoPosition)
                  }*/
                //Makes sure that the media controls pop up on resuming and when going between PIP and non-PIP states.
                binding.radioplayer.videoView.useController = true
                //     showSystemUi()
                binding.radioplayer.videoView.player = Exoplayer.player
                binding.radioplayer.RadioImVFragBig.visibility = View.GONE
                binding.radioplayer.videoView.visibility = View.VISIBLE

            } else {
                binding.radioplayer.videoView.visibility = View.INVISIBLE
                binding.radioplayer.RadioImVFragBig.visibility = View.VISIBLE
            }
        } else {
            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
            binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
        }


        super.onResume()

    }

    override fun onPause() {

        if (Exoplayer.player != null) {
            videoPosition = Exoplayer.player!!.currentPosition
        }
        adViewAdaptiveBanner.pause()


        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::adViewAdaptiveBanner.isInitialized) {
            adViewAdaptiveBanner.destroy()
        }
        currentNativeAd?.destroy()

        /*   if (Build.VERSION.SDK_INT > 23 && !isChangingConfigurations) {
               ExoPlayer.releasePlayer()
           }
   */

        if (Exoplayer.player != null) {
            //  binding.radioplayer.videoView.player = null
            Exoplayer.player!!.removeAnalyticsListener(Exoplayer.Analyticslistener)
        }

    }

    /*
    override fun onStop() {
        super.onStop()

        //     player.release()
        //PIPmode activity.finish() does not remove the activity from the recents stack.
        //Only finishAndRemoveTask does this.
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            finishAndRemoveTask()
            if(allowStopPlayer) ExoPlayer.releasePlayer()
        }
    }
*/


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (RadioFunction.allPermissionsGranted(this@MainActivity)) {
                RadioFunction.getDownloader(this@MainActivity)
                downloader?.download()
                DynamicToast.makeSuccess(
                    this@MainActivity,
                    getString(R.string.Permissionsgranted),
                    3
                ).show()

            } else {
                DynamicToast.makeError(
                    this@MainActivity,
                    getString(R.string.PermissionsNotgranted),
                    3
                ).show()
                downloader?.cancelDownload()

            }
        }
    }


    fun subsucribers() {
        Exoplayer.Observer.subscribe("text view", binding.radioplayer.radioInfotxV)


        Exoplayer.Observer.subscribeImagePlayPause(
            "Main image view",
            binding.radioplayer.pauseplayButtonMain
        )
        Exoplayer.Observer.subscribeImagePlayPause(
            "image view",
            binding.radioplayer.pauseplayButton
        )

        Exoplayer.Observer.subscribeImageRecord(
            "Main record image view",
            binding.radioplayer.recordOffONButton
        )

        //  ExoPlayer.Observer.subscribeImageFave("Main fav image view", binding.radioplayer.likeImageViewPlayermain)
        //    ExoPlayer.Observer.subscribeImageFave("fav image view", binding.radioplayer.likeImageView)

        Exoplayer.Observer.subscribeImageRecord(
            "Main stop image view",
            binding.radioplayer.stopButton
        )
    }


    private fun setTheme() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    RadioFunction.gradiancolorTransition(binding.searchView.searchVwframe, 4, it)
                    RadioFunction.textcolorSearchviewTransition(binding.searchView.searchInputText, it)
                    RadioFunction.maintextviewColor(binding.searchView.ActionBarTitle, it)

                    RadioFunction.maintextviewColor(binding.radioplayer.RadioNameImVFrag, it)
                    RadioFunction.secondarytextviewColor(binding.radioplayer.radioInfotxV, it)

                    RadioFunction.gradiancolorTransition(binding.containerMain, 4, it)
                    RadioFunction.gradiancolorConstraintLayoutTransition(
                        binding.radioplayer.containermainplayer,
                        0,
                        it
                    )
                    RadioFunction.gradiancolorNativeAdslayout(binding.radioplayer.adsFrame, 0)


                    setupRadioLisRV()
                    populateRecyclerView(radioRoom)
                }


            }
        }


    }


    fun setDataConsumption() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putDataConsumption.collectLatest {
                    if (it != "Not Suported") binding.radioplayer.datainfotxvw.text = it
                    else binding.radioplayer.datainfotxvw.visibility = View.GONE
                }
            }


        }
    }


    fun loadNativeAdPlayer() {
        //  adViewSmallActivityplayer = LayoutInflater.from(this@MainActivity).inflate(R.layout.ads_small, null) as NativeAdView

        adViewSmallActivityplayer =
            View.inflate(this@MainActivity, R.layout.ads_small, null) as NativeAdView
        RadioFunction.nativeSmallAds(
            this@MainActivity,
            binding.radioplayer.adsFrame,
            adViewSmallActivityplayer
        )
        RadioFunction.interatial_ads_load(this@MainActivity)
    }


    private fun getFavRadioRoom() {
        radioRoomViewModel.getAll(true).observe(this) { list ->
            //    Log.d("MainFragment","ID ${list.map { it.id }}, Name ${list.map { it.name }}")


            if (populateFavRv) {
                setupRadioLisRV()
                populateRecyclerView(list)
            }
            populateFavRv = true

            if (list.isNotEmpty()) {
                radioRoom.clear()
                radioRoom.addAll(list)

                if (stationuuid != "") {

                    if (setfavIcons(stationuuid)) {
                        binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_liked)
                        binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_liked)
                    } else {
                        binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                        binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                    }
                }
            }
        }

    }

    private fun getLastPlayedRadio() {
        radioRoomViewModel.getAll(false).observe(this) { list ->

            //  DynamicToast.makeError(this, list.size.toString(), 9).show()
            if (list.isNotEmpty()) {
                if (Exoplayer.player == null) {
                    val radioVariables = RadioVariables()
                    radioVariables.name = list[0].name
                    radioVariables.bitrate = list[0].bitrate
                    radioVariables.country = list[0].country
                    radioVariables.stationuuid = list[0].radiouid
                    radioVariables.favicon = list[0].favicon
                    radioVariables.language = list[0].language
                    radioVariables.state = list[0].state
                    radioVariables.url_resolved = list[0].streamurl
                    radioVariables.homepage = list[0].homepage
                    radioVariables.tags = list[0].tags
                    infoViewModel.putRadiopalyerInfo(radioVariables)

                    GlobalRadiourl = list[0].streamurl
                    stationuuid = list[0].radiouid

                    if (setfavIcons(list[0].radiouid)) {
                        binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_liked)
                        binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_liked)
                    } else {
                        binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                        binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                    }
                    //  getFavRadioRoom(list[0].radiouid)
                }

            } else {
                if (Exoplayer.player == null) {
                    binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                    binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                    binding.radioplayer.RadioNameImVFrag.text = getString(R.string.click_to_expand)
                    binding.radioplayer.radioInfotxV.text = getString(R.string.playernullinfo)

                    binding.radioplayer.RadioImVFragBig.setImageResource(R.drawable.radioerror)
                    binding.radioplayer.RadioImVFrag.setImageResource(R.drawable.radioerror)
                    icyandState = getString(R.string.playernullinfo)
                }

            }


        }

    }


    override fun onBackPressed() {
        if (isExpanded) bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else super.onBackPressed()

    }

    fun bottomsheetopenclose(){
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    fun setPlayerBottomSheet() {

        binding.radioplayer.RadioImVFrag.setSafeOnClickListener {
            bottomsheetopenclose()
        }

        binding.radioplayer.RadioNameImVFrag.setSafeOnClickListener {
            bottomsheetopenclose()
        }

        //  binding.radioplayer.toolbarLayoutPlayermain.visibility = View.INVISIBLE

        bottomSheetBehavior = BottomSheetBehavior.from(binding.radioplayer.containermainplayer)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //  if (isAdded()) {
                transitionBottomSheetBackgroundColor(slideOffset)

                //   binding.radioplayer.miniPaleryy.visibility = View.VISIBLE
                //    binding.radioplayer.toolbarLayoutPlayermain.visibility = View.VISIBLE
                binding.radioplayer.RadioImVFrag.alpha = 1 - slideOffset
                binding.radioplayer.stopButton.alpha = 1 - slideOffset
                binding.radioplayer.pauseplayButton.alpha = 1 - slideOffset
                binding.radioplayer.likeImageView.alpha = 1 - slideOffset
                binding.radioplayer.datainfotxvw.alpha = 1 - slideOffset
                //    binding.radioplayer.RadioImVFrag.alpha = slideOffset


                //  }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (::adViewSmallActivityplayer.isInitialized) adViewSmallActivityplayer.destroy()
                        binding.radioplayer.RadioImVFrag.visibility = View.VISIBLE
                        binding.radioplayer.stopButton.visibility = View.VISIBLE
                        binding.radioplayer.pauseplayButton.visibility = View.VISIBLE
                        binding.radioplayer.likeImageView.visibility = View.VISIBLE
                        binding.radioplayer.datainfotxvw.visibility = View.VISIBLE
                        isExpanded = false
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //    setupRadioLisRV()
                        // getRadioRoomplayer()

                        binding.radioplayer.RadioImVFrag.visibility = View.INVISIBLE
                        binding.radioplayer.stopButton.visibility = View.INVISIBLE
                        binding.radioplayer.pauseplayButton.visibility = View.INVISIBLE
                        binding.radioplayer.likeImageView.visibility = View.INVISIBLE
                        binding.radioplayer.datainfotxvw.visibility = View.INVISIBLE
                        isExpanded = true
                        //    DynamicToast.makeSuccess(this@MainActivity,"STATE_EXPANDED", 3).show()
                        loadNativeAdPlayer()

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        //DynamicToast.makeSuccess(this@MainActivity,"STATE_DRAGGING", 3).show()
                        isExpanded = false
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        isExpanded = false
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        //DynamicToast.makeSuccess(this@MainActivity,"STATE_HIDDEN", 3).show()
                        isExpanded = false
                    }
                    else -> {
                        isExpanded = false
                        Toast.makeText(this@MainActivity, "OTHER_STATE", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun loadBannerAD() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this@MainActivity) { }
        adViewAdaptiveBanner = AdView(this@MainActivity)
        binding.adAdaptivebannerMain.addView(adViewAdaptiveBanner)
        loadBanner()


        //  set layout height begin
        scale = this@MainActivity.resources.displayMetrics.density
        //  set layout height end
    }


    private fun setTitleText() {
        lifecycleScope.launchWhenStarted {
            infoViewModel.putTitleText.collectLatest { title ->
                binding.searchView.ActionBarTitle.text = title

                if (title != getString(R.string.Search)) {
                    // if (binding.searchView.searchOpenView.isAttachedToWindow) {
                    closeSearch()
                    //  }
                }
            }
        }


    }

    private fun closeSearch() {
        binding.searchView.searchInputText.queryHint = getString(R.string.Search)
        binding.searchView.opencloseSearchButton.setImageResource(R.drawable.search)
        binding.searchView.searchInputText.visibility = View.INVISIBLE
        binding.searchView.ActionBarTitle.visibility = View.VISIBLE
        binding.searchView.searchInputText.setQuery("", false)

        UIUtil.hideKeyboard(this@MainActivity, binding.searchView.searchInputText)
    }

    private fun openSearch() {
        binding.searchView.searchInputText.setQuery("", false)//.setText("")
        binding.searchView.opencloseSearchButton.setImageResource(R.drawable.ic_left_arrow)
        binding.searchView.ActionBarTitle.visibility = View.INVISIBLE
        binding.searchView.searchInputText.visibility = View.VISIBLE
        binding.searchView.searchInputText.isEnabled = true
        binding.searchView.searchInputText.requestFocus()

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun favRadio(
        faviconJson: String,
        nameJson: String,
        homepageJson: String,
        urlJson: String,
        countryJson: String,
        bitrateJson: String,
        languageJson: String,
        tagsJson: String,
        idListJson: String,
        state: String
    ) {
        /*
        val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
        .build()
        //creating the api interface
        val api = retrofit.create(Api::class.java)
        */

        val radio = RadioVariables()

        /*   var idIn = false

           var position = -1
           if (radioRoom.size > 0) {

           loop@ for (i in 0 until radioRoom.size) {

            if (idListJson == radioRoom[i].radiouid) {

                idIn = true
                position = i

                break@loop
            }
           }
           }
       */

        if (!setfavIcons(idListJson) && idListJson != "") {
            // jsonCall = api.addclick(idListJson)
            binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_liked)
            binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_liked)
            //DB BEGIN

            //  dbHandler= DBHandler(this@SearchResultsActivity,null,null,1)
            radio.name = nameJson
            radio.tags = tagsJson
            radio.stationuuid = idListJson
            radio.country = countryJson
            radio.language = languageJson
            radio.bitrate = bitrateJson
            radio.url_resolved = urlJson
            radio.favicon = faviconJson
            radio.homepage = homepageJson

            val radioroom = RadioRoom(
                idListJson,
                nameJson,
                bitrateJson,
                homepageJson,
                faviconJson,
                tagsJson,
                countryJson,
                state,
                //var RadiostateDB: String?,
                languageJson,
                urlJson,
                true
            )
            radioRoomViewModel.upsertRadio(radioroom, "Radio added")
        } else if (setfavIcons(idListJson)) {
            binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
            binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
            radioRoomViewModel.delete(/*radioRoom[position].radiouid*/idListJson,
                true,
                "Radio Deleted"
            )
            //  radioRoom.removeAll { it.radiouid == idListJson }
        }
    }


    private fun setfavIcons(stationuuid: String): Boolean {
        var idIn = false
        if (radioRoom.size > 0) {
            loop@ for (i in 0 until radioRoom.size) {
                if (stationuuid == radioRoom[i].radiouid /*&& radioRoom[i].fav */) {
                    idIn = true
                    break@loop
                }
            }
        }
        // DynamicToast.makeSuccess(this, k+ " " +b.toString()+" "+stationuuid , 9).show()
        return idIn

    }


    fun getPref() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                darkTheme = preferencesViewModel.preferencesFlow.first().dark_theme
                systemTheme = preferencesViewModel.preferencesFlow.first().system_theme

                infoViewModel.putThemes(preferencesViewModel.preferencesFlow.first().dark_theme)
            }


        }


    }


    private fun populateRecyclerView(radioRoom: MutableList<RadioRoom>) {
        if (radioRoom.isNotEmpty()) {
            radioFavoriteAdapterVertical.setItems(radioRoom)
            binding.radioplayer.favRadioPlayerRv.visibility = View.VISIBLE
        } else binding.radioplayer.favRadioPlayerRv.visibility = View.INVISIBLE
    }

    private fun setupRadioLisRV() {
        radioFavoriteAdapterVertical = RadioFavoriteAdapterHorizantal(this@MainActivity)
        binding.radioplayer.favRadioPlayerRv.apply {
            adapter = radioFavoriteAdapterVertical
            layoutManager =
                GridLayoutManager(this@MainActivity, 1, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onItemFavClick(radioRoom: RadioRoom) {
        try {
            populateFavRv = false
            GlobalRadiourl = radioRoom.streamurl
            GlobalImage = radioRoom.favicon
            Exoplayer.initializePlayer(this)
            Exoplayer.startPlayer()

            val radioVariables = RadioVariables()

            radioVariables.name = radioRoom.name
            radioVariables.bitrate = radioRoom.bitrate
            radioVariables.country = radioRoom.country
            radioVariables.stationuuid = radioRoom.radiouid
            radioVariables.favicon = radioRoom.favicon
            radioVariables.language = radioRoom.language
            radioVariables.state = radioRoom.state
            radioVariables.url_resolved = radioRoom.streamurl
            radioVariables.homepage = radioRoom.homepage
            radioVariables.tags = radioRoom.tags
            infoViewModel.putRadiopalyerInfo(radioVariables)


            //   jsonCall=api.addclick(radioRoom[position].radiouid)

        } catch (e: IOException) {
            // Catch the exception
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun onMoreItemFavClick(radio: RadioRoom) {
        TODO("Not yet implemented")
    }

    fun opensettingfrag() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        navController.navigateUp()
        navController.navigate(R.id.fragmentSetting)
    }


    fun openCountdownTimer() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        //  navController.navigateUp()
        navController.navigate(R.id.setTimerBottomSheetFragment)
    }

    fun opensearchfrag() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        navController.navigateUp()
        navController.navigate(R.id.searchFragment)
    }

    fun closesearchfrag() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        navController.navigateUp()
        //  navController.navigate(R.id.searchFragment)
    }


    private fun searchquerry() {

        binding.searchView.searchInputText.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (binding.searchView.searchInputText.query.toString().count() > 2) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchView.searchInputText.windowToken, 0)


                    retrofitRadioViewModel.getRadiosByName(binding.searchView.searchInputText.query.toString())
                    infoViewModel.putSearchquery(binding.searchView.searchInputText.query.toString())

                    infoViewModel.putTitleText(
                        binding.searchView.searchInputText.query.toString()
                            .replaceFirstChar { it.uppercase() })
                    binding.searchView.searchInputText.clearFocus()

                    opensearchfrag()

                } else {

                    binding.searchView.searchInputText.setQuery("", false)
                    // search_input_text.setHintTextColor(resources.getColor(R.color.primaryDark))
                    binding.searchView.searchInputText.queryHint =
                        getString(R.string.not_vaid_search)
                }


                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //Start filtering the list as user start entering the characters

                return false
            }
        })

    }

    fun googleSearch() {
        /*  val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, uri)
    unwrap(context).startActivity(intent)
*/

        val browserIntent = Intent()
            .setAction(Intent.ACTION_WEB_SEARCH)
            //  .addCategory(Intent.CATEGORY_BROWSABLE)
            .putExtra(SearchManager.QUERY, binding.radioplayer.radioInfotxV.text.toString())
        //   .setData(Uri.fromParts("http", "", null))

        startActivity(browserIntent)


    }
}
