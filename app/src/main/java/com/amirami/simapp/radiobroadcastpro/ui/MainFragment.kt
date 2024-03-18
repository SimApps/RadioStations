package com.amirami.simapp.radiobroadcastpro.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
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
import com.amirami.simapp.radiobroadcastpro.*
import com.amirami.simapp.radiobroadcastpro.MainActivity.Companion.defaultCountry
import com.amirami.simapp.radiobroadcastpro.MainActivity.Companion.imagedefaulterrorurl
import com.amirami.simapp.radiobroadcastpro.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcastpro.RadioFunction.loadImageString
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.RadioFunction.warningToast
import com.amirami.simapp.radiobroadcastpro.adapter.RadioAdapterHorizantal
import com.amirami.simapp.radiobroadcastpro.adapter.RadioFavoriteAdapterHorizantal
import com.amirami.simapp.radiobroadcastpro.adapter.Tags
import com.amirami.simapp.radiobroadcastpro.adapter.TagsAdapterHorizantal
import com.amirami.simapp.radiobroadcastpro.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiobroadcastpro.databinding.FragmentMainBinding
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import com.amirami.simapp.radiobroadcastpro.utils.connectivity.internet.NetworkViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.UIEvent
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
