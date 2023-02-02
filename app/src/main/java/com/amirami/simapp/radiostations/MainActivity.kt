package com.amirami.simapp.radiostations

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.downloader.Downloader
import com.amirami.simapp.radiostations.RadioFunction.errorToast
import com.amirami.simapp.radiostations.RadioFunction.getCurrentDate
import com.amirami.simapp.radiostations.RadioFunction.icyandStateWhenPlayRecordFiles
import com.amirami.simapp.radiostations.RadioFunction.parseColor
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.RadioFunction.succesToast
import com.amirami.simapp.radiostations.adapter.RadioFavoriteAdapterHorizantal
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiostations.databinding.ActivityContentMainBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.utils.Constatnts.CORNER_RADIUS_8F
import com.amirami.simapp.radiostations.utils.Constatnts.FROM_PLAYER
import com.amirami.simapp.radiostations.utils.ManagePermissions
import com.amirami.simapp.radiostations.utils.connectivity.internet.NetworkViewModel
import com.amirami.simapp.radiostations.viewmodel.*
import com.asmtunis.player_service.service.SimpleMediaService
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.io.IOException

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RadioFavoriteAdapterHorizantal.OnItemClickListener {
    private var isServiceRunning = false
    var populateFavRv = true // temoprary mesure

    private lateinit var radioFavoriteAdapterVertical: RadioFavoriteAdapterHorizantal
    private var isExpanded = false

   var  radioEntity : RadioEntity = RadioEntity()
     var stationuuid: String = ""
    //var  radio: RadioEntity = RadioEntity()
    private lateinit var adViewSmallActivityplayer: NativeAdView

    private lateinit var adViewAdaptiveBanner: AdView

    private val favoriteFirestoreViewModel: FavoriteFirestoreViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()

    private val infoViewModel: InfoViewModel by viewModels()
    private val radioRoomViewModel: RadioRoomViewModel by viewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by viewModels()

  //  private val favList: MutableList<RadioEntity> = mutableListOf()

    var recordDrawable = R.drawable.pop

    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 12322

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


    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isExpanded) bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else {
                val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)



               if(navController.graph.startDestinationId == navController.currentDestination?.id)
                   finish()
                else navController.navigateUp()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityContentMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        firebaseappCheck()

        if (fromAlarm) getAlarmRadioRoom()

        getPref()
        setTheme()

        setDataConsumption()
        dataConsuptionTimer()
        putTimer()
        collectLatestLifecycleFlow(simpleMediaViewModel.icyStreamInfoState) { info ->
            binding.radioplayer.radioInfotxV.text = info
        }


       /* collectLatestLifecycleFlow(simpleMediaViewModel.radioVar) { radioVar ->
            radio = radioVar
        }*/

            collectLatestLifecycleFlow(simpleMediaViewModel.isPlaying) { isPlayin ->
                if (isPlayin) {


                    binding.radioplayer.pauseplayButton.setImageResource(R.drawable.pause_2)
                    binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)
                } else  {

                    binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                    binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
                }
            }


        collectLatestLifecycleFlow(simpleMediaViewModel.uiState) { uiState ->
            when(uiState) {
                is UIState.Initial ->{
                    Log.d("eedrd","Initial")
                  //  binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                  //  binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
                }
                is UIState.Ready -> {
                   // if(!isServiceRunning)
                        startService()
                    Log.d("eedrd","Ready")
                  //  binding.radioplayer.pauseplayButton.setImageResource(R.drawable.pause_2)
                  //  binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)
                }

            }

        }
        setPlayerBottomSheet()
/*
        collectLatestLifecycleFlow(radioRoomViewModel.favList) { favList ->
            if (populateFavRv) {
                setupRadioLisRV()
                populateRecyclerView(favList)
            }
            populateFavRv = true

            if (favList.isNotEmpty()) {
                //this.favList.clear()
               // this.favList.addAll(favList)

                  if (stationuuid != "") {
                       if (radioRoomViewModel.isFavoriteStation(stationuuid)) {
                           binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_liked)
                           binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_liked)
                       } else {
                           binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                           binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                       }
                   }
            }
        }

*/
        collectLatestLifecycleFlow(radioRoomViewModel.lastListnedList) { lastListned ->
            if (lastListned.isNotEmpty()) {


                simpleMediaViewModel.setRadioVar(lastListned[0])



                if (radioRoomViewModel.isFavoriteStation(lastListned[0].stationuuid)) {
                    binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_liked)
                    binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_liked)
                } else {
                    binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                    binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                }
                //  getFavRadioRoom(list[0].radiouid)
            } else {
                binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                binding.radioplayer.RadioNameImVFrag.text = getString(R.string.click_to_expand)
                binding.radioplayer.radioInfotxV.text = getString(R.string.playernullinfo)

                binding.radioplayer.RadioImVFragBig.setImageResource(R.drawable.radioerror)
                binding.radioplayer.RadioImVFrag.setImageResource(R.drawable.radioerror)

                icyandStateWhenPlayRecordFiles(getString(R.string.playernullinfo), "")
            }
            // }
        }






        loadBannerAD()

        setTitleText()

        searchquerry()

        btnsClicks()

        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // This happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                simpleMediaViewModel.radioVar.collectLatest { radioVar ->
                    setPlayer(radioVar)
                    radioEntity = radioVar
                }
            }
        }

        collectLatestLifecycleFlow(simpleMediaViewModel.isRecFile) { isRec ->
            if(isRec)   {
                binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_recordings_folder)
                binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_recordings_folder)

            }
            else  {
                if (radioRoomViewModel.isFavoriteStation(radioEntity.stationuuid)) {
                    binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_liked)
                    binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_liked)
                } else {
                    binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_like)
                    binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_like)
                }
            }
        }

        AppRater.applaunched(this@MainActivity)
    }

    private fun putTimer() {
        lifecycleScope.launch {
            //  repeatOnLifecycle(Lifecycle.State.STARTED) { //not used becuase player dont stop when app is in background
            infoViewModel.putTimer.collectLatest {
                when (it) {
                    1 -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)

                        infoViewModel.stoptimer(true)
                        //       Exoplayer.releasePlayer(this@MainActivity)
                    }
                    -1 -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)
                    }
                    else -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.time_left)
                    }
                }
            }
            // }
        }
    }

    private fun dataConsuptionTimer() {
        lifecycleScope.launch {
            // repeatOnLifecycle(Lifecycle.State.STARTED) {//not used becuase player dont stop when app is in background

            infoViewModel.putDataConsumptionTimer.collectLatest {
                when {
                    it == -1L -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)
                    }
                    it < 0L -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.timeu)
                        infoViewModel.stopdatatimer(true)
                        //  Exoplayer.releasePlayer(this@MainActivity)
                    }
                    else -> {
                        binding.searchView.addAlarmButton.setImageResource(R.drawable.time_left)
                    }
                }
            }
            //   }
        }
    }

    private fun vedeoisOnviews() {
        binding.radioplayer.apply {
           videoView.player = simpleMediaViewModel.getPlayer()


            RadioImVFragBig.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            videoView.defaultArtwork = ContextCompat.getDrawable(this@MainActivity, recordDrawable)



        }
    }
    private fun vedeoisNotOnviews() {
        binding.radioplayer.apply {
            videoView.visibility = View.INVISIBLE
            RadioImVFragBig.visibility = View.VISIBLE
        }
    }

    private fun setPlayer(radioVar: RadioEntity) {


        simpleMediaViewModel.setRadioVar(radioVar)
        if (!is_playing_recorded_file) {
         //   if (video_on) vedeoisOnviews()
         //   else vedeoisNotOnviews()

            RadioFunction.loadImageString(
                this@MainActivity,
                radioVar.favicon,
                imagedefaulterrorurl,
                binding.radioplayer.RadioImVFrag,
                CORNER_RADIUS_8F
            )
            RadioFunction.loadImageString(
                this@MainActivity,
                radioVar.favicon,
                imagedefaulterrorurl,
                binding.radioplayer.RadioImVFragBig,
                CORNER_RADIUS_8F
            )
        } else {
            vedeoisOnviews()
            binding.radioplayer.apply {
                //    RadioImVFragBig.setImageResource(R.drawable.rec_on)
                RadioImVFrag.setImageResource(R.drawable.rec_on)

                likeImageView.setImageResource(R.drawable.ic_recordings_folder)
                likeImageViewPlayermain.setImageResource(R.drawable.ic_recordings_folder)
            }
        }


        binding.radioplayer.RadioNameImVFrag.isSelected = true
        binding.radioplayer.RadioNameImVFrag.text = radioVar.name


        binding.radioplayer.likeImageViewPlayermain.setSafeOnClickListener {
            if (!is_playing_recorded_file) {
                val isFav = radioRoomViewModel.isFavoriteStation(radioVar.stationuuid)
                if (!isFav && radioVar.stationuuid != "") {
                    addFavoriteRadioIdInArrayFirestore(radioVar.stationuuid)
                }
                else if (isFav) {
                    deleteFavoriteRadioFromArrayinfirestore(radioVar.stationuuid)
                }
                radioRoomViewModel.setFavRadio(radioVar, isFav)
            } else {
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
dataViewModel.saveFirstTimeopenRecordFolder(firstTimeopenRecordfolder)

                }
                RadioFunction.openRecordFolder(this@MainActivity)
            }
        }

        binding.radioplayer.likeImageView.setSafeOnClickListener {
            if (!is_playing_recorded_file) {
                val isFav = radioRoomViewModel.isFavoriteStation(radioVar.stationuuid)
                if (!isFav && radioVar.stationuuid != "") {
                    addFavoriteRadioIdInArrayFirestore(radioVar.stationuuid)
                }
                else if (isFav) {
                    deleteFavoriteRadioFromArrayinfirestore(radioVar.stationuuid)
                }
                radioRoomViewModel.setFavRadio(radioVar, isFav)
            } else {
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
                    dataViewModel.saveFirstTimeopenRecordFolder(firstTimeopenRecordfolder)

                }
                RadioFunction.openRecordFolder(this@MainActivity)
            }
            // getRadioRoom()
            // infoViewModel.putRadiopalyerInfo(radioVar)
        }

        binding.radioplayer.stopButtonMain.setSafeOnClickListener {
            if (isDownloadingCustomurl) {
                customdownloader?.cancelDownload()
                binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                return@setSafeOnClickListener
            }

            // if (Exoplayer.player != null) {
            if (Exoplayer.is_downloading) {
                downloader?.cancelDownload()
                binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
                return@setSafeOnClickListener
            }
            simpleMediaViewModel.onUIEvent(UIEvent.Stop)
            binding.radioplayer.videoView.player = null


            /*  if (video_on || Exoplayer.is_playing_recorded_file) vedeoisOnviews()
              else {
                  vedeoisNotOnviews()

                  RadioFunction.loadImageString(
                      this@MainActivity,
                      radioVar.favicon,
                      imagedefaulterrorurl,
                      binding.radioplayer.RadioImVFragBig,
                      CORNER_RADIUS_8F
                  )
              }*/
            // }
        }
        binding.radioplayer.stopButton.setSafeOnClickListener {

            if (isDownloadingCustomurl) {
                customdownloader?.cancelDownload()
                binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                return@setSafeOnClickListener
            }

            // if (Exoplayer.player != null) {
            if (Exoplayer.is_downloading) {
                downloader?.cancelDownload()
                binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
                return@setSafeOnClickListener
            }
           // simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
            simpleMediaViewModel.onUIEvent(UIEvent.Stop)

            binding.radioplayer.videoView.player = null

            /*  if (video_on || Exoplayer.is_playing_recorded_file) vedeoisOnviews()
              else {
                  vedeoisNotOnviews()
                  RadioFunction.loadImageString(
                      this@MainActivity,
                      radioVar.favicon,
                      imagedefaulterrorurl,
                      binding.radioplayer.RadioImVFragBig,
                      CORNER_RADIUS_8F
                  )
              }*/
            //}
        }

        binding.radioplayer.pauseplayButtonMain.setSafeOnClickListener {
             //   simpleMediaViewModel.loadData(radio)
            simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
        }
        binding.radioplayer.pauseplayButton.setSafeOnClickListener {
            simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
        }


        binding.radioplayer.recordOffONButton.setSafeOnClickListener {
            if (Exoplayer.is_downloading) {
                downloader?.cancelDownload()
                binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
            } else {
                //if (video_on || Exoplayer.is_playing_recorded_file) errorToast(this@MainActivity, getString(R.string.VideoRecordNotAvailable))

                if (is_playing_recorded_file) errorToast(this@MainActivity, getString(R.string.cantRecordArecordedStream))

                if (/*Exoplayer.player != null && */ !is_playing_recorded_file) {
                    if (!isDownloadingCustomurl) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            RadioFunction.getDownloader(this@MainActivity)
                        } else {
                            val list =  listOf(
                                WRITE_EXTERNAL_STORAGE,
                                READ_EXTERNAL_STORAGE
                            )
                            // Initialize a new instance of ManagePermissions class
                            managePermissions = ManagePermissions(this@MainActivity, list, PermissionsRequestCode)
                            if (managePermissions.isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
                                managePermissions.checkPermissions()
                            } else
                                RadioFunction.getDownloader(this@MainActivity)
                        }


                     /*   if (allPermissionsGranted(this@MainActivity)) RadioFunction.getDownloader(this@MainActivity)
                        else requestMultiplePermissions.launch(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE))
*/
                        downloader?.download()

                        if (Exoplayer.is_downloading) {
                            binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_on)
                            binding.radioplayer.stopButton.setImageResource(R.drawable.rec_on)
                        }
                        RadioFunction.interatialadsShow(this@MainActivity)
                    } else errorToast(this@MainActivity, getString(R.string.cantrecordwhendownload))
                }
            }
        }

        binding.radioplayer.radioInfotxV.setSafeOnClickListener {
            if (binding.radioplayer.radioInfotxV.text.toString().isNotEmpty() &&
                binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.playernullinfo) &&
                binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.BUFFERING) &&
                binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.OoOps_Try_another_station)
            ) {
                RadioFunction.copytext(
                    this@MainActivity,
                    binding.radioplayer.radioInfotxV.text.toString()
                )

                googleSearch()
            }
        }

        binding.radioplayer.moreButtons.setSafeOnClickListener {
            //     val radioVars = radioVar
            radioVar.moreinfo = FROM_PLAYER
            infoViewModel.putRadioInfo(radioVar)
            val navController =
                Navigation.findNavController(this@MainActivity, R.id.fragment_container)
            // navController.navigateUp()
            navController.navigate(R.id.moreBottomSheetFragment)
        }
    }

    private fun btnsClicks() {
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
    }

    private fun firebaseappCheck() {
        FirebaseApp.initializeApp(this@MainActivity)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
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
        val userRecord = FirebaseAuth.getInstance()
        var color1 = parseColor("#03071e") // -256
        var color2 = parseColor("#03071e") // -65536
        var color3 = parseColor("#03071e")
        var color4 = parseColor("#03071e")
        var darkTheme = true
        var systemTheme = true

        var saveData = false

        var time = true

        var mInterstitialAd: InterstitialAd? = null

        var isDownloadingCustomurl = false
        var repeat_tryconnect_server = -1
        var server_arraylist = arrayOf(
            "http://91.132.145.114",
            "http://89.58.16.19",
            "http://95.179.139.106" // ,
            //  "http://all.api.radio-browser.info"
        )
        var downloader: Downloader? = null
        var customdownloader: Downloader? = null

        val handlers: Handler = Handler(Looper.getMainLooper()) // Handler()

        var BASE_URL = "http://91.132.145.114"

        var GlobalRadioName = ""
        var GlobalRadiourl: Uri = Uri.parse("")
        var Globalurl : String  = ""

        var fromAlarm = false

        var video_on = true


        var is_playing_recorded_file = false
        var defaultCountry = ""

        var initial_data_consumed: Long = 0L

        var scale: Float = 0.0F
        var data = 0L
        var icyandState = ""
        var currentNativeAd: NativeAd? = null
        var firstTimeOpened = false
        var firstTimeopenRecordfolder = true
        var imagedefaulterrorurl = R.drawable.radioerror
    }

    public override fun onResume() {
        super.onResume()

       /* if (Exoplayer.player != null) {
            if (video_on) binding.radioplayer.videoView.onResume()

            if (video_on || Exoplayer.is_playing_recorded_file) vedeoisOnviews()
            else vedeoisNotOnviews()
        } else {
            binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
            binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
        }*/

    }

    /*  override fun onStop() {
         super.onStop()
         if (Exoplayer.player != null && video_on) {
             if (Util.SDK_INT > 23) binding.radioplayer.videoView.onPause()

         }

     }

     */
    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()

        stopService(Intent(this, SimpleMediaService::class.java))
        isServiceRunning = false
    }
    private fun startService() {

        if (!isServiceRunning) {
            val intent = Intent(this, SimpleMediaService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
            isServiceRunning = true
        }
    }
    override fun onPause() {
        super.onPause()
     //   if (Exoplayer.player != null && video_on) binding.radioplayer.videoView.onPause()
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value) {
                    RadioFunction.getDownloader(this@MainActivity)
                    downloader?.download()
                    succesToast(this@MainActivity, getString(R.string.Permissionsgranted))
                } else {
                    errorToast(this@MainActivity, getString(R.string.PermissionsNotgranted))

                    customdownloader?.cancelDownload()
                }
            }
        }



    private fun setTheme() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    RadioFunction.cardViewColor(binding.searchView.searchcardview, it)
                    //  RadioFunction.gradiancolorTransition(binding.searchView.searchVwframe, 4, it)
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

               //     setupRadioLisRV()
                 //   populateRecyclerView(favList)
                }
            }
        }
    }

    private fun setDataConsumption() {
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
        RadioFunction.interatialadsLoad(this@MainActivity)
    }

    private fun getAlarmRadioRoom() {
        turnScreenOnAndKeyguardOff()
        collectLatestLifecycleFlow(radioRoomViewModel.alarmList) { list ->
            if (list.isNotEmpty() && fromAlarm) {
                val radioVar = RadioEntity(
                    1,
                    name =  list[0].name,

                    homepage =   list[0].homepage,
                    favicon=   list[0].favicon,
                    tags = list[0].tags,
                    country =  list[0].country,
                    state =  list[0].state,
                    language =  list[0].language,
                    streamurl = list[0].streamurl,
                    bitrate =  list[0].bitrate,
                    stationuuid =  list[0].stationuuid,
                    moreinfo =    list[0].moreinfo
                )

                //  if (list[0].radiouid == "") Exoplayer.initializePlayer(this, true, Uri.parse(list[0].streamurl))
                // else Exoplayer.initializePlayer(this, false, Uri.parse(list[0].streamurl))

                // Exoplayer.startPlayer()

                simpleMediaViewModel.loadData(radioVar)
              //  simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)

                setPlayer(radioVar)

                radioRoomViewModel.deleteAllAlarm("")

                bottomsheetopenclose()
            }
        }

    }







    private fun bottomsheetopenclose() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    private fun setPlayerBottomSheet() {
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
                    // binding.radioplayer.datainfotxvw.alpha = 1 - slideOffset
                    //    binding.radioplayer.RadioImVFrag.alpha = slideOffset

                    //  }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            if (::adViewSmallActivityplayer.isInitialized) adViewSmallActivityplayer.destroy()
                            binding.radioplayer.apply {
                                RadioImVFrag.visibility = View.VISIBLE
                                stopButton.visibility = View.VISIBLE
                                pauseplayButton.visibility = View.VISIBLE
                                likeImageView.visibility = View.VISIBLE
                                // binding.radioplayer.datainfotxvw.visibility = View.VISIBLE
                            }

                            isExpanded = false
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            //    setupRadioLisRV()
                            // getRadioRoomplayer()
                            binding.radioplayer.apply {
                                RadioImVFrag.visibility = View.INVISIBLE
                                stopButton.visibility = View.INVISIBLE
                                pauseplayButton.visibility = View.INVISIBLE
                                likeImageView.visibility = View.INVISIBLE
                                //  binding.radioplayer.datainfotxvw.visibility = View.GONE
                            }

                            isExpanded = true
                            //    DynamicToast.makeSuccess(this@MainActivity,"STATE_EXPANDED", 3).show()
                            loadNativeAdPlayer()
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            // DynamicToast.makeSuccess(this@MainActivity,"STATE_DRAGGING", 3).show()
                            isExpanded = false
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            isExpanded = false
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            // DynamicToast.makeSuccess(this@MainActivity,"STATE_HIDDEN", 3).show()
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
// Step 1: Create an inline adaptive banner ad size using the activity context.
        //     val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(this, 320)

// Step 2: Create banner using activity context and set the inline ad size and
// ad unit ID.
        adViewAdaptiveBanner = AdView(this@MainActivity)
        binding.adAdaptivebannerMain.addView(adViewAdaptiveBanner)
        adViewAdaptiveBanner.adUnitId = resources.getString(R.string.adaptive_banner_adUnitId)
        adViewAdaptiveBanner.setAdSize(adSize)

// Step 3: Load an ad.
        val adRequest = AdRequest.Builder().build()
        adViewAdaptiveBanner.loadAd(adRequest)
    }

    private fun setTitleText() {
        collectLatestLifecycleFlow(networkViewModel.isConnected) { isConnected ->
            if(isConnected){
                binding.searchView.opencloseSearchButton.visibility = View.VISIBLE

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


            else  {
                binding.searchView.apply {
                    ActionBarTitle.text = "Not Connected !"
                    opencloseSearchButton.visibility = View.INVISIBLE
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
        binding.searchView.searchInputText.setQuery("", false) // .setText("")
        binding.searchView.opencloseSearchButton.setImageResource(R.drawable.ic_right_arrow)
        binding.searchView.ActionBarTitle.visibility = View.INVISIBLE
        binding.searchView.searchInputText.visibility = View.VISIBLE
        binding.searchView.searchInputText.isEnabled = true
        binding.searchView.searchInputText.requestFocus()

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }



    private fun addFavoriteRadioIdInArrayFirestore(radioUid: String) {
        val addFavoritRadioIdInArrayFirestore =
            favoriteFirestoreViewModel.addFavoriteRadioidinArrayFirestore(radioUid, getCurrentDate())
        addFavoritRadioIdInArrayFirestore.observe(this) {
            // if (it != null)  if (it.data!!)  prod name array updated
            RadioFunction.interatialadsShow(this@MainActivity)
            if (it.e != null) {
                // prod bame array not updated
                errorToast(this, it.e!!)
            }
        }
    }

    private fun deleteFavoriteRadioFromArrayinfirestore(radioUid: String) {
        val deleteFavoriteRadiofromArrayInFirestore = favoriteFirestoreViewModel.deleteFavoriteRadioFromArrayinFirestore(radioUid)
        deleteFavoriteRadiofromArrayInFirestore.observe(this) {
            RadioFunction.interatialadsShow(this@MainActivity)
            // if (it != null)  if (it.data!!)  prod name array updated
            if (it.e != null) {
                // prod bame array not updated
                RadioFunction.dynamicToast(this, it.e!!)
            }
        }
    }



    private fun getPref() {


        darkTheme =  dataViewModel.getDarkTheme()
        systemTheme = dataViewModel.getSystemTheme()

        infoViewModel.putThemes(darkTheme)

    }

    private fun populateRecyclerView(radioRoom: List<RadioEntity>) {
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

    override fun onItemFavClick(radioRoom: RadioEntity) {
        try {
            populateFavRv = false



            simpleMediaViewModel.loadData(radioRoom)
                //  simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)


            val radioVariables = RadioEntity()
            radioVariables.apply {
                name = radioRoom.name
                bitrate = radioRoom.bitrate
                country = radioRoom.country
                stationuuid = radioRoom.stationuuid
                favicon = radioRoom.favicon
                language = radioRoom.language
                state = radioRoom.state
                streamurl = radioRoom.streamurl
                homepage = radioRoom.homepage
                tags = radioRoom.tags
            }
            simpleMediaViewModel.setRadioVar(radioVariables)


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

    override fun onMoreItemFavClick(radio: RadioEntity) {
        TODO("Not yet implemented")
    }

    private fun opensettingfrag() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        navController.navigateUp()
        navController.navigate(R.id.fragmentSetting)
    }

    private fun openCountdownTimer() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        //  navController.navigateUp()
        navController.navigate(R.id.setTimerBottomSheetFragment)
    }

    private fun opensearchfrag() {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        navController.navigateUp()
        navController.navigate(R.id.searchFragment)
    }

    private fun closesearchfrag() {
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
                                .replaceFirstChar { it.uppercase() }
                        )
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
                    // Start filtering the list as user start entering the characters

                    return false
                }
            })
    }

    private fun googleSearch() {
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

    private fun Activity.turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        with(getSystemService(KEYGUARD_SERVICE) as KeyguardManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
            }
        }
    }

    private fun Activity.turnScreenOffAndKeyguardOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

    private fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        this.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }


}
