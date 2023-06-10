package com.amirami.simapp.radiobroadcast.ui

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiobroadcast.R
import com.amirami.simapp.radiobroadcast.RadioFunction
import com.amirami.simapp.radiobroadcast.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcast.RadioFunction.errorToast
import com.amirami.simapp.radiobroadcast.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcast.adapter.RadioFavoriteAdapterVertical
import com.amirami.simapp.radiobroadcast.databinding.FragmentFavoriteBinding
import com.amirami.simapp.radiobroadcast.model.RadioEntity
import com.amirami.simapp.radiobroadcast.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.UIEvent
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@UnstableApi @AndroidEntryPoint
class FavoriteRadioFragment :
    Fragment(R.layout.fragment_favorite),
    RadioFavoriteAdapterVertical.OnItemClickListener {
    //  private var currentNativeAdFavori:  NativeAd? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()

    private val radioRoom: MutableList<RadioEntity> = mutableListOf()
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var radioFavoriteAdapterVertical: RadioFavoriteAdapterVertical

    lateinit var appUpdateManager: AppUpdateManager
    private val REQUEST_APP_UPDATE = 560
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    lateinit var filterredList: MutableList<RadioEntity>

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        if (result.resultCode == REQUEST_APP_UPDATE) {
            if (result.resultCode == RESULT_CANCELED) {
                errorToast(requireContext(), "Update canceled!")
                //  checkUpdate()
            } else if (result.resultCode != RESULT_IN_APP_UPDATE_FAILED) {
                errorToast(requireContext(), result.resultCode.toString())
                checkUpdate()
            } else if (result.resultCode != RESULT_OK) {
                errorToast(requireContext(), result.resultCode.toString())
                checkUpdate()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)

        appUpdateManager = AppUpdateManagerFactory.create(requireContext())

        infoViewModel.putTitleText(getString(R.string.Favoris))
        setupRadioLisRV()


        //  setContentView(R.layout.activity_favorite_main_activity)

        binding.floatingActionAddStream.setSafeOnClickListener {
            val action = FavoriteRadioFragmentDirections.actionFavoriteRadioFragmentToAddDialogueBottomSheetFragment(false)
            this@FavoriteRadioFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }




                    collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.queryString) {queryString ->


                    getFavRadioRoom(queryString)
                }
    }



    private fun setupRadioLisRV() {
        radioFavoriteAdapterVertical = RadioFavoriteAdapterVertical(this)
        binding.rv.apply {
            adapter = radioFavoriteAdapterVertical
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }

    private fun getFavRadioRoom(queryString : String?) {


                    collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.favList) {list ->
                        //    Log.d("MainFragment","ID ${list.map { it.id }}, Name ${list.map { it.name }}")
                        filterredList = (list.filter { it.name.contains(queryString.toString() ,
                            ignoreCase = true) }as MutableList<RadioEntity>)

                        if (filterredList.isNotEmpty()) {
                            radioRoom.clear()
                            radioRoom.addAll(filterredList)

                            //    DynamicToast.makeError(requireContext(), list.size.toString(), 3).show()
                            populateRecyclerView(filterredList)
                            binding.whenemptyfavImage.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE
                        } else {
                            binding.whenemptyfavImage.visibility = View.VISIBLE
                            binding.rv.visibility = View.INVISIBLE
                        }
                    }




    }

    private fun populateRecyclerView(radioRoom: MutableList<RadioEntity>) {
        if (radioRoom.isNotEmpty()) {
            //     radioFavoriteAdapter.radiodiffer = radioRoom
            radioFavoriteAdapterVertical.setItems(radioRoom)
            //  adapter.submitList(ArrayList(productShopingRoom))
            //  adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(radioRoom: RadioEntity) {
        try {
            val list = RadioFunction.moveItemToFirst(
                array = filterredList,
                item = radioRoom
            )
            simpleMediaViewModel.loadData(list)
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

    override fun onMoreItemClick(radio: RadioEntity) {
        Log.d("jjdnsqq","ff"+radio.toString())

        infoViewModel.putRadioInfo(radio)
        this@FavoriteRadioFragment.findNavController().navigate(R.id.action_favoriteRadioFragment_to_moreBottomSheetFragment) //      NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_favoriteRadioFragment_to_moreBottomSheetFragment)
    }

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

  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_APP_UPDATE) {
            if (resultCode == RESULT_CANCELED) {
                errorToast(requireContext(), "Update canceled!")
                //  checkUpdate()
            } else if (resultCode != RESULT_IN_APP_UPDATE_FAILED) {
                errorToast(requireContext(), resultCode.toString())
                checkUpdate()
            } else if (resultCode != RESULT_OK) {
                errorToast(requireContext(), resultCode.toString())
                checkUpdate()
            }
        }
    }
*/
    override fun onStop() {
        super.onStop()
        appUpdateManager.unregisterListener(installStateUpdatedListener!!)
    }
    override fun onResume() {
        super.onResume()
        checkUpdate()

    /*    appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    appUpdateManager.completeUpdate()                }
            }*/
    }



}
