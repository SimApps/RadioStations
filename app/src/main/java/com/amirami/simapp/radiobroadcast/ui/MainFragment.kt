package com.amirami.simapp.radiobroadcast.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiobroadcast.*
import com.amirami.simapp.radiobroadcast.MainActivity.Companion.defaultCountry
import com.amirami.simapp.radiobroadcast.MainActivity.Companion.imagedefaulterrorurl
import com.amirami.simapp.radiobroadcast.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcast.RadioFunction.loadImageString
import com.amirami.simapp.radiobroadcast.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcast.RadioFunction.warningToast
import com.amirami.simapp.radiobroadcast.adapter.RadioAdapterHorizantal
import com.amirami.simapp.radiobroadcast.adapter.RadioFavoriteAdapterHorizantal
import com.amirami.simapp.radiobroadcast.adapter.Tags
import com.amirami.simapp.radiobroadcast.adapter.TagsAdapterHorizantal
import com.amirami.simapp.radiobroadcast.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiobroadcast.databinding.FragmentMainBinding
import com.amirami.simapp.radiobroadcast.model.RadioEntity
import com.amirami.simapp.radiobroadcast.utils.Constatnts
import com.amirami.simapp.radiobroadcast.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import com.amirami.simapp.radiobroadcast.utils.connectivity.internet.NetworkViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.UIEvent
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*


@UnstableApi @AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), RadioAdapterHorizantal.OnItemClickListener, TagsAdapterHorizantal.OnItemClickListener, RadioFavoriteAdapterHorizantal.OnItemClickListener {
    // private lateinit var fragmentClass: Class<*>
    private lateinit var adViewSmallActivitymain: NativeAdView

    private var currentNativeAdActivityMain: NativeAd? = null

    // lateinit var adView: NativeAdView
    private lateinit var adViewBigActivityMain: NativeAdView

    // Initializing an empty ArrayList to be filled with animals
    private val tagList: ArrayList<Tags> = ArrayList()
    private lateinit var binding: FragmentMainBinding
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val dataViewModel: DataViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val networkViewModel: NetworkViewModel by activityViewModels()



    private lateinit var radioAdapterLastPlayedRadioHorizantal: RadioFavoriteAdapterHorizantal
    private lateinit var tagsAdapterHorizantal: TagsAdapterHorizantal

    lateinit var appUpdateManager: AppUpdateManager

    private val REQUEST_APP_UPDATE = 560
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        if (result.resultCode == REQUEST_APP_UPDATE) {
            if (result.resultCode == Activity.RESULT_CANCELED) {
                RadioFunction.errorToast(requireContext(), "Update canceled!")
                //  checkUpdate()
            } else if (result.resultCode != com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                RadioFunction.errorToast(requireContext(), result.resultCode.toString())
                checkUpdate()
            } else if (result.resultCode != Activity.RESULT_OK) {
                RadioFunction.errorToast(requireContext(), result.resultCode.toString())
                checkUpdate()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        getPref()
        getLocalcountryName()
        infoViewModel.putTitleText(getString(R.string.Radio))

        collectLatestLifecycleFlow(lifecycleOwner = this,networkViewModel.isConnected) { isConnected ->
            btnClick(isConnected)
        }

        adsLooad()
        setUpTagsRv()
        getRadioList()

        appUpdateManager = AppUpdateManagerFactory.create(requireContext())

    }
    private fun setUpTagsRv() {
        if (tagList.size == 0) {
            addtagpopulat()
        }
        tagsAdapterHorizantal = TagsAdapterHorizantal(tagList, this)

        binding.localPopularTagsRecycleView.apply {
            adapter = tagsAdapterHorizantal
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)

            setHasFixedSize(true)
        }
    }
    private fun addtagpopulat() {

        tagList.add(
            Tags(name = getString(R.string.Languages),
            image =R.drawable.languages)
        )

        tagList.add(
            Tags(name = getString(R.string.News),
                image =R.drawable.news)
        )

        tagList.add(
            Tags(name = getString(R.string.Pop),
                image =R.drawable.pop)
        )


        tagList.add(
            Tags(name = getString(R.string.Hits),
                image =R.drawable.hits)
        )


        tagList.add(
            Tags(name = getString(R.string.Rock),
                image =R.drawable.rock)
        )

        tagList.add(
            Tags(name = getString(R.string.Electronic),
                image =R.drawable.electronic)
        )

        tagList.add(
            Tags(name = getString(R.string.Country),
                image =R.drawable.country)
        )

        tagList.add(
            Tags(name = getString(R.string.Reggae),
                image =R.drawable.reggae)
        )

        tagList.add(
            Tags(name = getString(R.string.Latin),
                image =R.drawable.latin)
        )
        tagList.add(
            Tags(name = getString(R.string.States),
                image =R.drawable.states)
        )

        tagList.add(
            Tags(name = getString(R.string.Codecs),
                image =R.drawable.codec)
        )



    }



    private fun getLocalcountryName() {

                loadImageString(
                    requireContext(),
                    COUNTRY_FLAGS_BASE_URL + dataViewModel.getDefaultCountr().lowercase(Locale.ROOT)+".svg",
                    imagedefaulterrorurl,
                    binding.localradiomview,
                    Constatnts.CORNER_RADIUS_32F
                )
            }


    override fun onDestroy() {
        super.onDestroy()
        MainActivity.currentNativeAd?.destroy()

        if (::adViewSmallActivitymain.isInitialized) {
            adViewSmallActivitymain.destroy()
        }
        if (::adViewBigActivityMain.isInitialized) {
            adViewBigActivityMain.destroy()
        }
    }



    private fun nativeAds1() {
        fun populateUnifiedNativeAdView(nativeAd: NativeAd,
                                        adView: NativeAdView
        ) {
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAdActivityMain?.destroy()
            currentNativeAdActivityMain = nativeAd

            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            adView.mediaView = adView.findViewById(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            if (adView.headlineView != null) {


                // The headline is guaranteed to be in every UnifiedNativeAd.
                (adView.headlineView as TextView).text = nativeAd.headline

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                if (nativeAd.body == null) {
                    adView.bodyView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    adView.bodyView!!.visibility = View.VISIBLE
                    (adView.bodyView as TextView).text = nativeAd.body
                }

                if (nativeAd.callToAction == null) {
                    adView.callToActionView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    adView.callToActionView!!.visibility = View.VISIBLE
                    (adView.callToActionView as Button).text = nativeAd.callToAction
                }

                if (nativeAd.icon == null) {
                    adView.iconView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    (adView.iconView as ImageView).setImageDrawable(
                        nativeAd.icon!!.drawable
                    )
                    adView.iconView!!.visibility = View.VISIBLE
                }

                if (nativeAd.price == null) {
                    adView.priceView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    adView.priceView!!.visibility = View.VISIBLE
                    (adView.priceView as TextView).text = nativeAd.price
                }

                if (nativeAd.store == null) {
                    adView.storeView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    adView.storeView!!.visibility = View.VISIBLE
                    (adView.storeView as TextView).text = nativeAd.store
                }

                if (nativeAd.starRating == null) {
                    adView.starRatingView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                    adView.starRatingView!!.visibility = View.VISIBLE
                }

                if (nativeAd.advertiser == null) {
                    adView.advertiserView!!.visibility = View.INVISIBLE // INVISIBLE
                } else {
                    (adView.advertiserView as TextView).text = nativeAd.advertiser
                    adView.advertiserView!!.visibility = View.VISIBLE
                }
            }
            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            /*  val vc = nativeAd.videoController

              // Updates the UI to say whether or not this ad has a video asset.


             if (vc.hasVideoContent()) {
                  videostatus_text.text = String.format(
                      Locale.getDefault(),
                      "Video status: Ad contains a %.2f:1 video asset.",
                      vc.aspectRatio)

                  // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                  // VideoController will call methods on this object when events occur in the video
                  // lifecycle.
                  vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                      override fun onVideoEnd() {
                          // Publishers should allow native ads to complete video playback before
                          // refreshing or replacing them with another ad in the same UI location.
                          refresh_button.isEnabled = true
                          videostatus_text.text = "Video status: Video playback has ended."
                          super.onVideoEnd()
                      }
                  }
              } else {
                  videostatus_text.text = "Video status: Ad does not contain a video asset."
                  refresh_button.isEnabled = true
              }*/
        }

        fun refreshAd() {
            // refresh_button.isEnabled = false

            val builder = AdLoader.Builder(
                requireContext(),
                resources.getString(R.string.native_Advanced_big_adUnitId)/*"ca-app-pub-5900899997553420/8708850645"*/
            )

            builder.forNativeAd { unifiedNativeAd ->
                // OnUnifiedNativeAdLoadedListener implementation.
                //     val adView = layoutInflater.inflate(R.layout.ad_unified, null) as UnifiedNativeAdView

                populateUnifiedNativeAdView(
                    unifiedNativeAd,
                    adViewBigActivityMain
                )

                binding.adsFrames.removeAllViews()

                //    binding.adsFrames.removeView(binding.adsFrame)
                binding.adsFrames.addView(adViewBigActivityMain)
            }

            val videoOptions = VideoOptions.Builder()
                // .setStartMuted(start_muted_checkbox.isChecked)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {

                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the interstitial ad is closed.
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
        refreshAd()
    }

    private fun getPref() {

            MainActivity.saveData = dataViewModel.getSaveData()

            MainActivity.firstTimeOpened = dataViewModel.getFirstOpen()

            if (!dataViewModel.getFirstOpen()) {
                val action = MainFragmentDirections.actionMainFragmentToInfoBottomSheetFragment("BatterieOptimisation")
                this@MainFragment.findNavController().navigate(action) //   NavHostFragment.findNavController(requireParentFragment()).navigate(action)
                dataViewModel.saveFirstTimeopenRecordFolder(true)
            }

            MainActivity.BASE_URL = dataViewModel.getChoosenServer()
            if (dataViewModel.getChoosenServer() !in MainActivity.server_arraylist) {
                val randomNumber: Int = Random().nextInt(MainActivity.server_arraylist.size - 1)
                MainActivity.BASE_URL = MainActivity.server_arraylist[randomNumber] // "http://91.132.145.114"   remove this line when all user dont use 2.2.35
            }

            //  binding.localcountryTxtVw.text= RadioFunction.countryCodeToName(preferencesViewModel.preferencesFlow.first().default_country)
            defaultCountry = dataViewModel.getDefaultCountr()

            MainActivity.firstTimeopenRecordfolder = dataViewModel.getFirstTimeopenRecordFolder()

            MainActivity.BASE_URL = dataViewModel.getChoosenServer()
            if (dataViewModel.getChoosenServer() !in MainActivity.server_arraylist) {
                val randomNumber: Int = Random().nextInt(MainActivity.server_arraylist.size - 1)
                MainActivity.BASE_URL = MainActivity.server_arraylist[randomNumber] // "http://91.132.145.114"   remove this line when all user dont use 2.2.35
            }

    }


    private fun btnClick(isConnected : Boolean) {


        binding.btnAllcountry.setSafeOnClickListener {
            if (!canNavigate())  return@setSafeOnClickListener

            if(isConnected){
                val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.countries))
                findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)

            }
            else warningToast(requireContext(), getString(R.string.verify_connection))

        }
        binding.localradiomview.setSafeOnClickListener {
            if (!canNavigate())  return@setSafeOnClickListener

            if(isConnected){
                retrofitRadioViewModel.getRadios(defaultCountry, "Empty")
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(defaultCountry)
                findNavController().navigate(action) // NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }
            else warningToast(requireContext(), getString(R.string.verify_connection))
        }

        binding.viewAllFavRadio.setSafeOnClickListener {
            if (canNavigate()) {
                val action = MainFragmentDirections.actionMainFragmentToFavoriteRadioFragment()
                findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

        }

        binding.btnTopClicks.setSafeOnClickListener {
            if (!canNavigate())  return@setSafeOnClickListener

            if(isConnected){
                retrofitRadioViewModel.getRadios("Top clicks", "300")
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment("Top clicks", "300")
                findNavController().navigate(action) //   NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }
            else warningToast(requireContext(), getString(R.string.verify_connection))
        }

        binding.btnTopVotes.setSafeOnClickListener {
            if (!canNavigate())  return@setSafeOnClickListener

            if(isConnected){
                retrofitRadioViewModel.getRadios("Top Votes", "300")
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment("Top Votes", "300")
                findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }
            else warningToast(requireContext(), getString(R.string.verify_connection))

        }
        binding.btnRecodedFiles.setSafeOnClickListener {
            if (canNavigate()) {
                retrofitRadioViewModel.getListRadios(getString(R.string.Recordings))
                val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.Recordings))
                findNavController().navigate(action) //   NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

        }
        /*    binding.recentTxtVw.setSafeOnClickListener {
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(getString(R.string.recents),"300")
                NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

         */
        binding.tagsTxtVw.setSafeOnClickListener {
            if (!canNavigate())  return@setSafeOnClickListener

            if(isConnected){
                retrofitRadioViewModel.getListRadios(getString(R.string.tags))
                val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.tags))
                findNavController().navigate(action) //    NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }
            else warningToast(requireContext(), getString(R.string.verify_connection))

        }
    }

    private fun adsLooad() {
        adViewSmallActivitymain = View.inflate(requireContext(), R.layout.ads_small, null) as NativeAdView
        adViewBigActivityMain = View.inflate(requireContext(), R.layout.ads_big, null) as NativeAdView

        nativeAds1()
        RadioFunction.nativeSmallAds(
            requireContext(),
            binding.adsFrame,
            adViewSmallActivitymain
        )
    }

    override fun onItemClick(radio: RadioEntity) {
        if (radio.stationuuid == "") {
            if (canNavigate()) {
                try {

                    //   RadioFunction.countryCodeToName(radio.name)
                    retrofitRadioViewModel.getRadios(getString(R.string.countries), radio.name)
                    val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(getString(R.string.countries), radio.name)
                    this@MainFragment.findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
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

        } else {
            try {

                simpleMediaViewModel.loadData(listOf(radio)as MutableList<RadioEntity>)
                 simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)

                //    jsonLocalRadioCall = api.addclick(idListJson[holder.absoluteAdapterPosition]!!)
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
    }

    override fun onItemTagsClick(item: Tags) {
        //   val intentActivityRadio = Intent(context, RadiosFragment::class.java)
        // val intentActivityListRadio = Intent(context, ListRadioFragment::class.java)
        if (canNavigate()) {
            when (item.name) {
                getString(R.string.Languages) -> {
                    retrofitRadioViewModel.getListRadios(getString(R.string.languages))
                    val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.languages))
                    this@MainFragment.findNavController().navigate(action) //  findNavController().navigate(action)
                }
                getString(R.string.States) -> {
                    retrofitRadioViewModel.getListRadios(getString(R.string.states))
                    val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.states))
                    this@MainFragment.findNavController().navigate(action) //  findNavController().navigate(action)
                }
                getString(R.string.Codecs) -> {
                    retrofitRadioViewModel.getListRadios(getString(R.string.codecs))
                    val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.codecs))
                    this@MainFragment.findNavController().navigate(action) //  findNavController().navigate(action)
                }
                else -> {
                    //  intentActivityRadio.putExtra("TagName", items[position])

                    retrofitRadioViewModel.getRadios(item.name, "Empty")
                    val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(item.name)
                    this@MainFragment.findNavController().navigate(action) //  findNavController().navigate(action)
                }
            }

        }
    }

    override fun onItemFavClick(radioRoom: RadioEntity) {
        try {

            simpleMediaViewModel.loadData(listOf(radioRoom)as MutableList<RadioEntity>)
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

    override fun onMoreItemFavClick(radio: RadioEntity) = Unit

    private fun getRadioList() {

                    collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.radioList) { list ->
                        val ladtListned = list.filter { it.isLastListned }
                        if (ladtListned.isNotEmpty()) {
                            radioAdapterLastPlayedRadioHorizantal = RadioFavoriteAdapterHorizantal(this@MainFragment)
                            binding.lastPlayedRadioRv.apply {
                                adapter = radioAdapterLastPlayedRadioHorizantal
                                layoutManager = GridLayoutManager(requireContext(), 1, LinearLayoutManager.HORIZONTAL, false)
                                setHasFixedSize(true)
                            }
                            radioAdapterLastPlayedRadioHorizantal.setItems(ladtListned)

                            binding.lastPlayedRadioRv.visibility = View.VISIBLE
                            binding.recentrecentlyplayedTxV.visibility = View.VISIBLE
                        } else {
                            binding.recentrecentlyplayedTxV.visibility = View.GONE
                            binding.lastPlayedRadioRv.visibility = View.GONE
                        }
                    }



    }


    private fun canNavigate() : Boolean = findNavController().currentDestination?.id == R.id.mainFragment



    private fun checkUpdate() {
        // appUpdateManager = AppUpdateManagerFactory.create(requireContext())

        /*
// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            //   DynamicToast.makeWarning(requireContext(),  appUpdateInfo.updateAvailability().toString(), 9).show()
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                //   && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >=0// DAYS_FOR_FLEXIBLE_UPDATE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.

                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    // AppUpdateType.IMMEDIATE,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    requireActivity() /* Functions.unwrap(context)*/,
                    // Include a request code to later monitor this update request.
                    1)

                appUpdateManager.registerListener(listener)
            }
            else {
                appUpdateManager.unregisterListener(listener)
                Log.d(ContentValues.TAG, "No Update available")
            }
        }

*/

        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            when {
                state.installStatus() == InstallStatus.DOWNLOADED -> {
                    appUpdateManager.completeUpdate()
                }
                state.installStatus() == InstallStatus.INSTALLED -> {
                    // if (appUpdateManager != null) {
                    appUpdateManager.unregisterListener(installStateUpdatedListener!!)
                    // }
                }
                else -> {
                    //  Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus())
                }
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            //    Log.d("TAG", "here")
            // Checks that the platform will allow the specified type of update.
            if ((
                        appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                        ) ||
                (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
            ) {
                // Request the update.
                try {
                    //    Log.d("TAG", "here")
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        requireActivity(),
                        REQUEST_APP_UPDATE
                    )

                    startForResult.launch(Intent(requireContext(), FavoriteRadioFragment::class.java))
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }

        appUpdateManager.registerListener(installStateUpdatedListener!!)
    }

    override fun onResume() {
        checkUpdate()
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        appUpdateManager.unregisterListener(installStateUpdatedListener!!)
    }

}
