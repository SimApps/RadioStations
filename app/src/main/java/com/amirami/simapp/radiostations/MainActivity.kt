package com.amirami.simapp.radiostations

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
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
import androidx.media3.common.util.UnstableApi
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.amirami.player_service.service.PlayerState
import com.amirami.player_service.service.SimpleMediaService
import com.amirami.simapp.radiostations.RadioFunction.errorToast
import com.amirami.simapp.radiostations.RadioFunction.getCurrentDate
import com.amirami.simapp.radiostations.RadioFunction.setFavIcon
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioFavoriteAdapterHorizantal
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiostations.databinding.ActivityContentMainBinding
import com.amirami.simapp.radiostations.model.FavoriteFirestore
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.utils.Constatnts.CORNER_RADIUS_8F
import com.amirami.simapp.radiostations.utils.Constatnts.FROM_PLAYER
import com.amirami.simapp.radiostations.utils.ManagePermissions
import com.amirami.simapp.radiostations.utils.connectivity.internet.NetworkViewModel
import com.amirami.simapp.radiostations.viewmodel.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.elevation.SurfaceColors
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException


@UnstableApi @AndroidEntryPoint
class MainActivity : AppCompatActivity(), RadioFavoriteAdapterHorizantal.OnItemClickListener {
    private var isServiceRunning = false
    var favList: List<RadioEntity> = emptyList()
    private lateinit var radioFavoriteAdapterVertical: RadioFavoriteAdapterHorizantal
    private var isExpanded = false

     var stationuuid: String = ""
    //var  radio: RadioEntity = RadioEntity()
    private lateinit var adViewSmallActivityplayer: NativeAdView

    private lateinit var adViewAdaptiveBanner: AdView

    private val favoriteFirestoreViewModel: FavoriteFirestoreViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()

    private val infoViewModel: InfoViewModel by viewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by viewModels()
    private val downloaderViewModel: DownloaderViewModel by viewModels()

    //  private val favList: MutableList<RadioEntity> = mutableListOf()


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


    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isExpanded) bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                else {
                    val navController =
                        Navigation.findNavController(this@MainActivity, R.id.fragment_container)



                    if (navController.graph.startDestinationId == navController.currentDestination?.id)
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


        val permissionList =   listOf(
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this, permissionList, PermissionsRequestCode)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        if (managePermissions.isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            managePermissions.checkPermissions()
        }




        firebaseappCheck()

     //   if (fromAlarm) getAlarmRadioRoom()

        setDataConsumption()
        dataConsuptionTimer()


        setPlayerBottomSheet()




        collectLatestLifecycleFlow(downloaderViewModel.downloadState) { downloadState ->

            binding.radioplayer.recordOffONButton.setSafeOnClickListener {


                    if (!downloadState.isDownloading) {

                      /*  if (is_playing_recorded_file) errorToast(
                            this@MainActivity,
                            getString(R.string.cantRecordArecordedStream)
                        )*/
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                downloaderViewModel.startDownloader(icyandState= binding.radioplayer.radioInfotxV.text.toString())
                            } else {

                                // Initialize a new instance of ManagePermissions class
                                managePermissions =
                                    ManagePermissions(this@MainActivity, permissionList, PermissionsRequestCode)
                                if (managePermissions.isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
                                    managePermissions.checkPermissions()
                                } else
                                    downloaderViewModel.startDownloader(icyandState= binding.radioplayer.radioInfotxV.text.toString())

                            }

                            RadioFunction.interatialadsShow(this@MainActivity)


                    }
                     else   downloaderViewModel.cancelDownloader()

//else errorToast(this@MainActivity, getString(R.string.cantrecordwhendownload))
            }

            if(downloadState.downloadStarted){

                DynamicToast.make(
                    this@MainActivity,
                    "Recording Started . . .",
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.rec_on),
                    ContextCompat.getColor(this@MainActivity, R.color.blue),
                    ContextCompat.getColor(this@MainActivity, R.color.violet_medium),
                    9
                ).show()
            }
            if(downloadState.isPaused){
                DynamicToast.makeSuccess(this@MainActivity, "Recording paused", 3).show()

            }
            if(downloadState.downloadResumed){
                DynamicToast.makeSuccess(this@MainActivity, "Recording resumed", 3).show()

            }

            if(downloadState.completed){
                DynamicToast.make(
                    this@MainActivity,
                    "Recording Saved",
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.rec_on),
                    ContextCompat.getColor(this@MainActivity, R.color.blue),
                    ContextCompat.getColor(this@MainActivity, R.color.violet_medium),
                    9
                ).show()
                downloaderViewModel.resetDownloadState()
            }

            if(downloadState.error!=""){
                DynamicToast.makeError(this@MainActivity, downloadState.error, 9).show()
                downloaderViewModel.resetDownloadState()
            }

            Log.d("jjdnshs",downloadState.isDownloading.toString())


            if (downloadState.isDownloading) {
                binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_on)
                binding.radioplayer.stopButton.setImageResource(R.drawable.rec_on)
            }
            else {
                binding.radioplayer.recordOffONButton.setImageResource(R.drawable.rec_2)
                binding.radioplayer.stopButton.setImageResource(R.drawable.stop_2)
            }

            putTimer()
        }




        collectLatestLifecycleFlow(simpleMediaViewModel.uiState) { uiState ->
            when (uiState) {
                is UIState.Initial -> {
                    Log.d("eedrd", "Initial")
                    //  binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                    //  binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
                }

                is UIState.Ready -> {
                    // if(!isServiceRunning)
                    startService()
                    Log.d("eedrd", "Ready")
                    //  binding.radioplayer.pauseplayButton.setImageResource(R.drawable.pause_2)
                    //  binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)
                }

            }

        }



        collectLatestLifecycleFlow(simpleMediaViewModel.state) { state ->



            if (state.isRecFile) {
                binding.radioplayer.likeImageView.setImageResource(R.drawable.ic_recordings_folder)
                binding.radioplayer.likeImageViewPlayermain.setImageResource(R.drawable.ic_recordings_folder)
            }


            if (state.playerState == PlayerState.PLAYING) {
                binding.radioplayer.pauseplayButton.setImageResource(R.drawable.pause_2)
                binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.pause_2)
            } else {

                binding.radioplayer.pauseplayButtonMain.setImageResource(R.drawable.play_2)
                binding.radioplayer.pauseplayButton.setImageResource(R.drawable.play_2)
            }




          val radioVar =  state.radioState
            infoViewModel.putRadioInfo(radioVar)
            simpleMediaViewModel.upsertRadio(radioVar)

            setPlayer(radioVar = radioVar, isRec = state.isRecFile)


            binding.radioplayer.likeImageViewPlayermain.setSafeOnClickListener {
                handleFavClick(radioVar = radioVar, isRec = state.isRecFile)
            }

            binding.radioplayer.likeImageView.setSafeOnClickListener {
                handleFavClick(radioVar = radioVar, isRec = state.isRecFile)
            }

            binding.radioplayer.stopButtonMain.setSafeOnClickListener {
                stopPlayer()
            }
            binding.radioplayer.stopButton.setSafeOnClickListener {
                stopPlayer()
            }

            binding.radioplayer.pauseplayButtonMain.setSafeOnClickListener {
                Log.d("ikjnhbg", "isPlaying "+state.playerState.toString())

                if(state.playerState == PlayerState.STOPED || state.playerState == PlayerState.INITIANIAL){
                    simpleMediaViewModel.loadData(radio = listOf(radioVar))

                }
                simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
            }
            binding.radioplayer.pauseplayButton.setSafeOnClickListener {
                Log.d("ikjnhbg", "isPlaying "+state.playerState.toString())

                if(state.playerState == PlayerState.STOPED ||state.playerState == PlayerState.INITIANIAL){

                    simpleMediaViewModel.loadData(radio = listOf(radioVar))

                }
                simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
            }



            binding.radioplayer.radioInfotxV.setSafeOnClickListener {
                if (binding.radioplayer.radioInfotxV.text.toString().isNotEmpty() &&
                    binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.playernullinfo) &&
                    binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.BUFFERING) &&
                    binding.radioplayer.radioInfotxV.text.toString() != getString(R.string.OoOps_Try_another_station)&&
                    binding.radioplayer.radioInfotxV.text.toString()  != "null"
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
               // infoViewModel.putRadioInfo(radioVar)
                val navController =
                    Navigation.findNavController(this@MainActivity, R.id.fragment_container)
                // navController.navigateUp()
                navController.navigate(R.id.moreBottomSheetFragment)
            }
        }

        collectLatestLifecycleFlow(infoViewModel.radioList) { list ->

              favList = list.filter { it.fav }
            setupRadioLisRV()
            populateRecyclerView(favList)

            val lastListned = list.filter { it.isLastListned }

            if (lastListned.isNotEmpty()) {
          //       setPlayer(lastListned[0])

            }
            else {
                binding.radioplayer.RadioNameImVFrag.text = getString(R.string.click_to_expand)
                binding.radioplayer.radioInfotxV.text = getString(R.string.playernullinfo)
                binding.radioplayer.RadioImVFrag.setImageResource(R.drawable.radioerror)
            }
        }

        loadBannerAD()

        setTitleText()

        searchquerry()



        btnsClicks()


        AppRater.applaunched(this@MainActivity)


    }

    private fun stopPlayer(){
        downloaderViewModel.cancelDownloader()
        simpleMediaViewModel.onUIEvent(UIEvent.Stop)
        binding.radioplayer.videoView.player = null
    }


    private fun handleFavClick(radioVar : RadioEntity, isRec: Boolean){
        if (!isRec) {
            val isFav = infoViewModel.isFavRadio(radioVar)
            if (!isFav && radioVar.stationuuid != "") {
                addFavoriteRadioIdInArrayFirestore(radioVar.stationuuid)
            } else if (isFav) {
                deleteFavoriteRadioFromArrayinfirestore(radioVar.stationuuid)
            }

            infoViewModel.setFavRadio(radioVar)
            binding.radioplayer.likeImageViewPlayermain.setFavIcon(!isFav)
            binding.radioplayer.likeImageView.setFavIcon(!isFav)
          //  simpleMediaViewModel.setRadioVar(radioVar)


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
    private fun putTimer() {
        lifecycleScope.launch {
            //  repeatOnLifecycle(Lifecycle.State.STARTED) { //not used becuase player dont stop when app is in background
            infoViewModel.putTimer.collectLatest {
                when (it) {
                    1 -> {
                        binding.searchView.addTimerButton.setImageResource(R.drawable.timeu)

                            downloaderViewModel.cancelDownloader()


                        infoViewModel.stoptimer(true)
                        //       Exoplayer.releasePlayer(this@MainActivity)
                    }

                    -1 -> {
                        binding.searchView.addTimerButton.setImageResource(R.drawable.timeu)
                    }

                    else -> {
                        binding.searchView.addTimerButton.setImageResource(R.drawable.time_left)
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
                        binding.searchView.addTimerButton.setImageResource(R.drawable.timeu)
                    }

                    it < 0L -> {
                        binding.searchView.addTimerButton.setImageResource(R.drawable.timeu)
                        infoViewModel.stopdatatimer(true)
                        //  Exoplayer.releasePlayer(this@MainActivity)
                    }

                    else -> {
                        binding.searchView.addTimerButton.setImageResource(R.drawable.time_left)
                    }
                }
            }
            //   }
        }
    }

    private fun vedeoisOnviews(url: String) {

        binding.radioplayer.apply {
            videoView.player = simpleMediaViewModel.getPlayer()


            if (url.isNotEmpty()) {
                val loader = ImageLoader(this@MainActivity)
                val req = ImageRequest.Builder(this@MainActivity)
                    .data(url) // demo link
                    .target { result ->
                       // val bitmap = (result as BitmapDrawable).bitmap

                        //   videoView.defaultArtwork = ContextCompat.getDrawable(this@MainActivity, recordDrawable)
                        videoView.defaultArtwork = result

                    }
                    .build()

                val disposable = loader.enqueue(req)

            } else videoView.defaultArtwork =
                ContextCompat.getDrawable(this@MainActivity, imagedefaulterrorurl)

        }

    }


    private fun setPlayer(radioVar : RadioEntity,isRec : Boolean) {

        vedeoisOnviews(radioVar.favicon)

        binding.radioplayer.RadioNameImVFrag.isSelected = true
        binding.radioplayer.RadioNameImVFrag.text = radioVar.name
      //  binding.radioplayer.RadioNameImVFrag.text = simpleMediaViewModel.getPlayer().mediaMetadata.albumTitle?:""
        binding.radioplayer.radioInfotxV.text = if(radioVar.icyState== "null") "" else radioVar.icyState
        //   binding.radioplayer.radioInfotxV.text = simpleMediaViewModel.getPlayer().mediaMetadata.title ?: ""



        if (!isRec) {
            val isFav = infoViewModel.isFavRadio(radioVar)
            binding.radioplayer.likeImageViewPlayermain.setFavIcon(isFav)
            binding.radioplayer.likeImageView.setFavIcon(isFav)

            RadioFunction.loadImageString(
                context = this@MainActivity,
                mainiconSting =   radioVar.favicon,
                erroricon = imagedefaulterrorurl,
                imageview = binding.radioplayer.RadioImVFrag,
                cornerRadius = CORNER_RADIUS_8F
            )

        } else {
            binding.radioplayer.apply {
                //    RadioImVFragBig.setImageResource(R.drawable.rec_on)
                RadioImVFrag.setImageResource(R.drawable.rec_on)

                likeImageView.setImageResource(R.drawable.ic_recordings_folder)
                likeImageViewPlayermain.setImageResource(R.drawable.ic_recordings_folder)
            }
        }
    }

    private fun btnsClicks() {

        binding.searchView.ActionBarTitle.setSafeOnClickListener {
            if (binding.searchView.searchInputText.isVisible) {
                closeSearch()
                //closesearchfrag()
            } else openSearch()
        }
        binding.searchView.opencloseSearchButton.setSafeOnClickListener {
            if (binding.searchView.searchInputText.isVisible) {
                closeSearch()
                //closesearchfrag()
            } else openSearch()
        }
        binding.searchView.addTimerButton.setSafeOnClickListener {
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


        binding.radioplayer.containermainplayer.setBackgroundColor(
           interpolateColor(
                slideOffset,
               SurfaceColors.SURFACE_1.getColor(this),
               SurfaceColors.SURFACE_5.getColor(this)
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


        var saveData = false

        var time = true

        var mInterstitialAd: InterstitialAd? = null

        var repeat_tryconnect_server = -1
        var server_arraylist = arrayOf(
            "http://91.132.145.114",
            "http://89.58.16.19",
            "http://95.179.139.106" // ,
            //  "http://all.api.radio-browser.info"
        )


        var BASE_URL = "http://91.132.145.114"

        var GlobalRadioName = ""
        var Globalurl: String = ""





        var defaultCountry = ""

        var initial_data_consumed: Long = 0L

        var scale: Float = 0.0F
        var data = 0L
        var currentNativeAd: NativeAd? = null
        var firstTimeOpened = false
        var firstTimeopenRecordfolder = true
        var imagedefaulterrorurl = R.drawable.radioerror
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
            adViewSmallActivityplayer,
            dataViewModel.getDarkTheme()
        )
        RadioFunction.interatialadsLoad(this@MainActivity)
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


        bottomSheetBehavior = BottomSheetBehavior.from(binding.radioplayer.containermainplayer)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
              transitionBottomSheetBackgroundColor(slideOffset)

                binding.radioplayer.RadioImVFrag.alpha = 1 - slideOffset
                binding.radioplayer.stopButton.alpha = 1 - slideOffset
                binding.radioplayer.pauseplayButton.alpha = 1 - slideOffset
                binding.radioplayer.likeImageView.alpha = 1 - slideOffset

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
                        }

                        isExpanded = false
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.radioplayer.apply {
                            RadioImVFrag.visibility = View.INVISIBLE
                            stopButton.visibility = View.INVISIBLE
                            pauseplayButton.visibility = View.INVISIBLE
                            likeImageView.visibility = View.INVISIBLE
                        }

                        isExpanded = true
                        loadNativeAdPlayer()
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        isExpanded = false
                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                        isExpanded = false
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
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
            if (isConnected) {
                binding.searchView.opencloseSearchButton.visibility = View.VISIBLE

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
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

            } else {

                binding.searchView.apply {
                    searchInputText.queryHint = ""
                   ActionBarTitle.text = "Not Connected !"
                    opencloseSearchButton.visibility = View.INVISIBLE
                }
            }
        }


    }

    private fun closeSearch() {


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment, R.id.searchFragment -> {
                    binding.searchView.searchInputText.queryHint = getString(R.string.Search)

                }
                R.id.listRadioFragment -> {
                    binding.searchView.searchInputText.queryHint =   getString(R.string.Search_list)
                }
                R.id.radiosFragment -> {
                    binding.searchView.searchInputText.queryHint = getString(R.string.Search_radio)
                }
                R.id.favoriteRadioFragment -> {
                    binding.searchView.searchInputText.queryHint =  getString(R.string.Search_favorite)
                }

            }
        }


        binding.searchView.opencloseSearchButton.setImageResource(R.drawable.search)
        binding.searchView.searchInputText.visibility = View.INVISIBLE
        binding.searchView.ActionBarTitle.visibility = View.VISIBLE
        binding.searchView.searchInputText.setQuery("", false)

      //  hideSoftKeyboard(binding.searchView.searchInputText)
    }

    private fun openSearch() {
        binding.searchView.searchInputText.setQuery("", false) // .setText("")
        binding.searchView.opencloseSearchButton.setImageResource(R.drawable.ic_right_arrow)
        binding.searchView.ActionBarTitle.visibility = View.INVISIBLE
        binding.searchView.searchInputText.visibility = View.VISIBLE
       // binding.searchView.searchInputText.isEnabled = true
      //   binding.searchView.searchInputText.requestFocus()


        showSoftKeyboard(binding.searchView.searchInputText)

    }
    private fun addFavoriteRadioIdInArrayFirestore(radioUid: String) {
        val addFavoritRadioIdInArrayFirestore =
            favoriteFirestoreViewModel.addFavoriteRadioidinArrayFirestore(
                radioUid,
                getCurrentDate()
            )
        addFavoritRadioIdInArrayFirestore.observe(this) {
            // if (it != null)  if (it.data!!)  prod name array updated
            RadioFunction.interatialadsShow(this@MainActivity)
            if (it.e != null) {
                // prod bame array not updated
                errorToast(this, it.e!!)

                if(it.e!!.contains( "NOT_FOUND") ){
                    val isProductAddLiveData = favoriteFirestoreViewModel.addUserDocumentInFirestore(FavoriteFirestore())

                    isProductAddLiveData.observe(this) { dataOrException ->
                        val isProductAdded = dataOrException.data
                        if (isProductAdded != null) {
                            //   hideProgressBar()

                        }
                        if (dataOrException.e != null) {
                            errorToast(this,dataOrException.e!!)
                            /*   if(dataOrException.e=="getRadioUID"){

                               }*/

                        }
                    }
                }

            }
        }
    }

    private fun deleteFavoriteRadioFromArrayinfirestore(radioUid: String) {
        val deleteFavoriteRadiofromArrayInFirestore =
            favoriteFirestoreViewModel.deleteFavoriteRadioFromArrayinFirestore(radioUid)
        deleteFavoriteRadiofromArrayInFirestore.observe(this) {
            RadioFunction.interatialadsShow(this@MainActivity)
            // if (it != null)  if (it.data!!)  prod name array updated
            if (it.e != null) {
                // prod bame array not updated
                RadioFunction.dynamicToast(this, it.e!!)
            }
        }
    }

    fun showSoftKeyboard(view: View) {



        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         //   imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)


            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }
    }

    fun hideSoftKeyboard(view: View) {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

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
            val list = RadioFunction.moveItemToFirst(
                array = favList as MutableList<RadioEntity>,
                item = radioRoom
            )
            simpleMediaViewModel.loadData(list as MutableList<RadioEntity>)
            simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)

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

    private fun opensearchfrag(/* navController: NavController*/) {
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
       // val navController = Navigation.findNavController(this@MainActivity, R.id.fragment_container)
        binding.searchView.searchInputText.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                val navController = navHostFragment.navController

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.mainFragment, R.id.searchFragment -> {
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
                            }

                            else {
                                binding.searchView.searchInputText.setQuery("", false)
                                // search_input_text.setHintTextColor(resources.getColor(R.color.primaryDark))
                                binding.searchView.searchInputText.queryHint = getString(R.string.not_vaid_search)
                            }


                            //binding.searchView.ActionBarTitle.text =  "searchFragment"
                        }
                        R.id.listRadioFragment -> {

                        }
                        R.id.radiosFragment -> {

                        }
                        R.id.favoriteRadioFragment -> {

                        }
                    }
                }


                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                // Start filtering the list as user start entering the characters
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                val navController = navHostFragment.navController

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.mainFragment, R.id.searchFragment -> {
                            //binding.searchView.ActionBarTitle.text =  "searchFragment"
                        }
                        R.id.listRadioFragment -> {
                            retrofitRadioViewModel.setQueryString(p0)
                        }
                        R.id.radiosFragment -> {
                            retrofitRadioViewModel.setQueryString(p0)
                        }
                        R.id.favoriteRadioFragment -> {
                            retrofitRadioViewModel.setQueryString(p0)
                        }
                    }
                }


                return false
            }
        })



    }

    private fun googleSearch() {
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


    override fun onResume() {
        super.onResume()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

              R.id.alarmFragment, R.id.settingAlarmFragment,
                R.id.normalAlarmSetFragment, R.id.simpleAlarmSetFragment  -> {
                    binding.searchView.addTimerButton.visibility = View.INVISIBLE
                    binding.searchView.opencloseSearchButton.visibility = View.INVISIBLE
                    binding.searchView.ActionBarTitle.isEnabled=false
                    infoViewModel.putTitleText("Alarms")
                }
                R.id.fragmentSetting -> {
                    binding.searchView.opencloseSearchButton.visibility = View.INVISIBLE
                    binding.searchView.ActionBarTitle.isEnabled=false
                }
                else -> {
                    binding.searchView.ActionBarTitle.isEnabled=true
                    binding.searchView.addTimerButton.visibility = View.VISIBLE
                    binding.searchView.opencloseSearchButton.visibility = View.VISIBLE


                }


            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopService(Intent(this, SimpleMediaService::class.java))

        }

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
}
