package com.amirami.simapp.radiostations.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.MainActivity.Companion.darkTheme
import com.amirami.simapp.radiostations.MainActivity.Companion.defaultCountry
import com.amirami.simapp.radiostations.MainActivity.Companion.imagedefaulterrorurl
import com.amirami.simapp.radiostations.RadioFunction.loadImageString
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioAdapterHorizantal
import com.amirami.simapp.radiostations.adapter.RadioFavoriteAdapterHorizantal
import com.amirami.simapp.radiostations.adapter.TagsAdapterHorizantal
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiostations.databinding.FragmentMainBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.utils.Constatnts
import com.amirami.simapp.radiostations.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RadioRoomViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiostations.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiostations.viewmodel.UIEvent
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), RadioAdapterHorizantal.OnItemClickListener, TagsAdapterHorizantal.OnItemClickListener, RadioFavoriteAdapterHorizantal.OnItemClickListener {
    // private lateinit var fragmentClass: Class<*>
    private lateinit var adViewSmallActivitymain: NativeAdView

    private var currentNativeAdActivityMain: NativeAd? = null

    // lateinit var adView: NativeAdView
    private lateinit var adViewBigActivityMain: NativeAdView

    // Initializing an empty ArrayList to be filled with animals
    private val popularTagList: ArrayList<String> = ArrayList()
    private val popularImagetagList: ArrayList<Int> = ArrayList()
    private lateinit var binding: FragmentMainBinding
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val radioRoomViewModel: RadioRoomViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val dataViewModel: DataViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()


    private lateinit var radioAdapterLastPlayedRadioHorizantal: RadioFavoriteAdapterHorizantal
    private lateinit var tagsAdapterHorizantal: TagsAdapterHorizantal
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        getPref()
        getLocalcountryName()
        infoViewModel.putTitleText(getString(R.string.Radio))

        btnClick()
        adsLooad()
        setUpTagsRv()
        getLastPlayedRadio()
    }
    private fun setUpTagsRv() {
        if (popularTagList.size == 0 && popularImagetagList.size == 0) {
            addtagpopulat()
            addimagetagpopulat()
        }
        tagsAdapterHorizantal = TagsAdapterHorizantal(popularTagList, popularImagetagList, this)

        binding.localPopularTagsRecycleView.apply {
            adapter = tagsAdapterHorizantal
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)

            setHasFixedSize(true)
        }
    }
    private fun addtagpopulat() {
        popularTagList.add(getString(R.string.Languages))
        popularTagList.add(getString(R.string.News))
        popularTagList.add(getString(R.string.Pop))
        popularTagList.add(getString(R.string.Hits))
        popularTagList.add(getString(R.string.Rock))
        popularTagList.add(getString(R.string.Electronic))
        popularTagList.add(getString(R.string.Country))
        popularTagList.add(getString(R.string.Reggae))
        popularTagList.add(getString(R.string.Latin))
        popularTagList.add(getString(R.string.States))
        popularTagList.add(getString(R.string.Codecs))
    }

    private fun addimagetagpopulat() {
        popularImagetagList.add(R.drawable.languages)
        popularImagetagList.add(R.drawable.news)
        popularImagetagList.add(R.drawable.pop)
        popularImagetagList.add(R.drawable.hits)
        popularImagetagList.add(R.drawable.rock)
        popularImagetagList.add(R.drawable.electronic)
        popularImagetagList.add(R.drawable.country)
        popularImagetagList.add(R.drawable.reggae)
        popularImagetagList.add(R.drawable.latin)
        popularImagetagList.add(R.drawable.states)
        popularImagetagList.add(R.drawable.codec)
    }

    private fun getLocalcountryName() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //  retrofitRadioViewModel.getLocalRadio(preferencesViewModel.preferencesFlow.first().default_country)
                loadImageString(
                    requireContext(),
                    COUNTRY_FLAGS_BASE_URL + dataViewModel.getDefaultCountr().lowercase(Locale.ROOT),
                    imagedefaulterrorurl,
                    binding.localradiomview,
                    Constatnts.CORNER_RADIUS_32F
                )
            }
        }
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

    override fun onResume() {
        super.onResume()
        setupTheme()
    }

    private fun nativeAds1() {
        fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
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
                RadioFunction.nativeadstexViewColor(
                    adView.headlineView as TextView,
                    adView.advertiserView as TextView,
                    adView.bodyView as TextView,
                    adView.priceView as TextView,
                    adView.storeView as TextView,
                    darkTheme
                )

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
                resources.getString(R.string.native_Advanced_adUnitId)/*"ca-app-pub-5900899997553420/8708850645"*/
            )

            builder.forNativeAd { unifiedNativeAd ->
                // OnUnifiedNativeAdLoadedListener implementation.
                //     val adView = layoutInflater.inflate(R.layout.ad_unified, null) as UnifiedNativeAdView

                populateUnifiedNativeAdView(unifiedNativeAd, adViewBigActivityMain)

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
    private fun setupTheme() {
        lifecycleScope.launchWhenStarted {
            infoViewModel.putTheme.collectLatest {
                RadioFunction.gradiancolorNativeAdslayout(binding.adsFrame, 0)
                RadioFunction.gradiancolorNativeAdslayout(binding.adsFrames, 0)
                RadioFunction.gradiancolorConstraintLayoutTransition(binding.Radiomain, 4, it)

                // RadioFunction.maintextviewColor(binding.countryTxtVw,it)
                RadioFunction.maintextviewColor(binding.recentrecentlyplayedTxV, it)
                RadioFunction.maintextviewColor(binding.tagsTxtVw, it)
            }
        }
        if (MainActivity.systemTheme) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
                Configuration.UI_MODE_NIGHT_NO -> darkTheme = false
            }
            dataViewModel.saveDarkTheme(darkTheme)
        }
    }

    private fun btnClick() {
        binding.btnAllcountry.setSafeOnClickListener {
            if (canNavigate()) {
                val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.countries))
                findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }
        }
        binding.localradiomview.setSafeOnClickListener {
            if (canNavigate()) {
                retrofitRadioViewModel.getRadios(defaultCountry, "Empty")
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(defaultCountry)
                findNavController().navigate(action) // NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }
        }

        binding.viewAllFavRadio.setSafeOnClickListener {
            if (canNavigate()) {
                val action = MainFragmentDirections.actionMainFragmentToFavoriteRadioFragment()
                findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

        }

        binding.btnTopClicks.setSafeOnClickListener {
            if (canNavigate()) {
                retrofitRadioViewModel.getRadios("Top clicks", "300")
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment("Top clicks", "300")
                findNavController().navigate(action) //   NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

        }

        binding.btnTopVotes.setSafeOnClickListener {
            if (canNavigate()) {
                retrofitRadioViewModel.getRadios("Top Votes", "300")
                val action = MainFragmentDirections.actionMainFragmentToRadiosFragment("Top Votes", "300")
                findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

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
            if (canNavigate()) {
                retrofitRadioViewModel.getListRadios(getString(R.string.tags))
                val action = MainFragmentDirections.actionMainFragmentToListRadioFragment(getString(R.string.tags))
                findNavController().navigate(action) //    NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

        }
    }

    private fun adsLooad() {
        adViewSmallActivitymain = View.inflate(requireContext(), R.layout.ads_small, null) as NativeAdView
        adViewBigActivityMain = View.inflate(requireContext(), R.layout.ads_big, null) as NativeAdView

        nativeAds1()
        RadioFunction.nativeSmallAds(requireContext(), binding.adsFrame, adViewSmallActivitymain)
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

                simpleMediaViewModel.loadData(radio)
               // simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)

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

    override fun onItemTagsClick(item: String, image: Int) {
        //   val intentActivityRadio = Intent(context, RadiosFragment::class.java)
        // val intentActivityListRadio = Intent(context, ListRadioFragment::class.java)
        if (canNavigate()) {
            when (item) {
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

                    retrofitRadioViewModel.getRadios(item, "Empty")
                    val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(item)
                    this@MainFragment.findNavController().navigate(action) //  findNavController().navigate(action)
                }
            }

        }
    }

    override fun onItemFavClick(radioRoom: RadioEntity) {
        try {
            simpleMediaViewModel.loadData(radioRoom)
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

    private fun getLastPlayedRadio() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                radioRoomViewModel.lastListnedList.collectLatest {list ->
                    if (list.isNotEmpty()) {
                        radioAdapterLastPlayedRadioHorizantal = RadioFavoriteAdapterHorizantal(this@MainFragment)
                        binding.lastPlayedRadioRv.apply {
                            adapter = radioAdapterLastPlayedRadioHorizantal
                            layoutManager = GridLayoutManager(requireContext(), 1, LinearLayoutManager.HORIZONTAL, false)
                            setHasFixedSize(true)
                        }
                        radioAdapterLastPlayedRadioHorizantal.setItems(list)

                        binding.lastPlayedRadioRv.visibility = View.VISIBLE
                        binding.recentrecentlyplayedTxV.visibility = View.VISIBLE
                    } else {
                        binding.recentrecentlyplayedTxV.visibility = View.GONE
                        binding.lastPlayedRadioRv.visibility = View.GONE
                    }
                }
            }
        }

    }


    private fun canNavigate() : Boolean = findNavController().currentDestination?.id == R.id.mainFragment
}
