package com.amirami.simapp.radiostations.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiostations.RadioFunction.getRecordedFiles
import com.amirami.simapp.radiostations.RadioFunction.indexesOf
import com.amirami.simapp.radiostations.RadioFunction.moveItemToFirst
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.RadioFunction.shortformateDate
import com.amirami.simapp.radiostations.adapter.RadioListAdapterVertical
import com.amirami.simapp.radiostations.adapter.RecordedFilesAdapter
import com.amirami.simapp.radiostations.databinding.FragmentListradioBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.utils.ManagePermissions
import com.amirami.simapp.radiostations.utils.exhaustive
import com.amirami.simapp.radiostations.viewmodel.DownloaderViewModel
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiostations.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiostations.viewmodel.UIEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@UnstableApi @AndroidEntryPoint
class ListRadioFragment : Fragment(R.layout.fragment_listradio), RadioListAdapterVertical.OnItemClickListener, RecordedFilesAdapter.OnItemClickListener {
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val downloaderViewModel: DownloaderViewModel by activityViewModels()

    private lateinit var radioAdapterHorizantal: RadioListAdapterVertical
    private lateinit var recordedFilesAdapter: RecordedFilesAdapter
    private lateinit var binding: FragmentListradioBinding
    val argsFrom: ListRadioFragmentArgs by navArgs()

    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 12322
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListradioBinding.bind(view)


                    collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.recordEvents) { event ->
                        when (event) {
                            is InfoViewModel.RecordInfoEvents.UpdateRecList -> {
                                run {
                                    if (event.update) {
                                        RadioFunction.deleteRecordedItem(event.position, requireContext())
                                        setUpRecord()
                                    }
                                }
                            }
                        }.exhaustive
                    }


                    collectLatestLifecycleFlow(lifecycleOwner = this,downloaderViewModel.downloadState) { downloadState ->
                        if (argsFrom.msg == resources.getString(R.string.Recordings)) {
                            binding.floatingActionAddDownload.setSafeOnClickListener {
                                if (downloadState.isDownloading) {
                                    downloaderViewModel.cancelDownloader()
                                } else {
                                    val action = ListRadioFragmentDirections.actionListRadioFragmentToAddDialogueBottomSheetFragment(true)
                                    NavHostFragment.findNavController(requireParentFragment()).navigate(action)

                                }
                            }

                        }

        }

        binding.itemErrorMessage.btnRetry.setSafeOnClickListener {
            if (argsFrom.msg != "Empty") {
                retrofitRadioViewModel.changeBseUrl()
                infoViewModel.putDefServerInfo(MainActivity.BASE_URL)

                //   retrofitRadioViewModel.getListRadios(argsFrom.msg)
                retrofitRadioViewModel.getListCountrieRadios()
            } else binding.itemErrorMessage.root.visibility = View.INVISIBLE
        }

        //  set layout height begin
        MainActivity.scale = this@ListRadioFragment.resources.displayMetrics.density
//  set layout height end

        infoViewModel.putTitleText(argsFrom.msg.replaceFirstChar { it.uppercase() })

        if (argsFrom.msg != resources.getString(R.string.Recordings)) {
            setupRadioLisRV()

                        collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.queryString) { queryString ->
                            setUpRv(queryString)
                        }



            binding.floatingActionAddDownload.visibility = View.INVISIBLE
        } else if (argsFrom.msg == resources.getString(R.string.Recordings)) {

            binding.floatingActionAddDownload.visibility = View.VISIBLE

            // fabColor(floating_action_addDownload)


            setUpRecord()
        }


    }



    private fun setUpRv(queryString : String?) {
        if (argsFrom.msg == getString(R.string.countries)) {

                        collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.responseListCountrieRadio) { response ->
                            when (response.status) {
                                Status.SUCCESS -> {
                                    if (response.data != null) {
                                        hideProgressBar()
                                        binding.itemErrorMessage.root.visibility = View.INVISIBLE
                                        //     radioAdapterHorizantal.radioListdffer = response.data as List<RadioVariables>



                                        val countryList = response.data as MutableList<RadioEntity>
                                        radioAdapterHorizantal.setItems(countryList.filter {
                                            RadioFunction.countryCodeToName(it.name).contains(queryString.toString() ,
                                                ignoreCase = true) }as MutableList<RadioEntity>)


                                    } else showErrorConnection(response.message!!)
                                }
                                Status.ERROR -> {
                                    hideProgressBar()
                                    showErrorConnection(response.message!!)
                                    binding.itemErrorMessage.root.visibility = View.VISIBLE
                                }
                                Status.LOADING -> {
                                    displayProgressBar()
                                }
                            }

                        }

        }
        else {


                        collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.responseRadioList) { response ->
                            when (response.status) {
                                Status.SUCCESS -> {
                                    if (response.data != null) {
                                        hideProgressBar()
                                        binding.itemErrorMessage.root.visibility = View.INVISIBLE

                                        val countryList = response.data as MutableList<RadioEntity>
                                        radioAdapterHorizantal.setItems(countryList.filter { it.name.contains(queryString.toString(),
                                            ignoreCase = true) }as MutableList<RadioEntity>)

                                    } else showErrorConnection(response.message!!)
                                }
                                Status.ERROR -> {
                                    hideProgressBar()
                                    showErrorConnection(response.message!!)
                                    binding.itemErrorMessage.root.visibility = View.VISIBLE
                                }
                                Status.LOADING -> {
                                    displayProgressBar()
                                }
                            }

                        }
                    }

    }
    fun showErrorConnection(msg: String) {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = msg
    }
    private fun displayProgressBar() {
        binding.spinKitlistRadio.visibility = View.VISIBLE
        binding.listViewCountriesLiradio.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.spinKitlistRadio.visibility = View.GONE
        binding.listViewCountriesLiradio.visibility = View.VISIBLE
    }

    private fun setupRadioLisRV() {
        radioAdapterHorizantal = RadioListAdapterVertical(this)
        binding.listViewCountriesLiradio.apply {
            adapter = radioAdapterHorizantal
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }

    private fun setupRecordedFilesRV() {
        recordedFilesAdapter = RecordedFilesAdapter(this)
        binding.listViewCountriesLiradio.apply {
            adapter = recordedFilesAdapter
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }

    override fun onItemClick(radio: RadioEntity) {
        try {
            retrofitRadioViewModel.getRadios(argsFrom.msg, radio.name)
            val action = ListRadioFragmentDirections.actionListRadioFragmentToRadiosFragment(argsFrom.msg, radio.name)
            NavHostFragment.findNavController(requireParentFragment()).navigate(action)
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

    override fun onRecItemClick(recordInfo: RadioEntity) {



        val list =  moveItemToFirst(
            array = getRecordedFiles(requireContext()),
            item = recordInfo.copy(isRec = true)
        )
        simpleMediaViewModel.loadData(radio = list)
        simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
    }

    override fun onRecMoreItemClick(recordInfo: RadioEntity, position: Int) {
        val action = ListRadioFragmentDirections.actionListRadioFragmentToInfoBottomSheetFragment(argsFrom.msg, position.toString(), recordInfo.name)
        NavHostFragment.findNavController(requireParentFragment()).navigate(action)
    }

    private fun setUpRecord() {
                    collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.queryString) { queryString ->
                        val filterdRecordList = getRecordedFiles(requireContext()).filter { it.name.contains(queryString.toString(), ignoreCase = true) } as MutableList<RadioEntity>
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (filterdRecordList.size == 0) {
                                binding.listViewCountriesLiradio.visibility = View.GONE
                                binding.whenemptyrecordImage.visibility = View.VISIBLE
                            } else {
                                setupRecordedFilesRV()
                                recordedFilesAdapter.setItems(filterdRecordList)
                                binding.whenemptyrecordImage.visibility = View.GONE
                                binding.listViewCountriesLiradio.visibility = View.VISIBLE
                            }

                        } else {
                            val list=   listOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )

                            // Initialize a new instance of ManagePermissions class
                            managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)

                            if (managePermissions.isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
                                managePermissions.checkPermissions()
                            } else {
                                //  if (RadioFunction.allPermissionsGranted(requireContext())) {
                                if (filterdRecordList.size == 0) {
                                    binding.listViewCountriesLiradio.visibility = View.GONE
                                    binding.whenemptyrecordImage.visibility = View.VISIBLE
                                } else {
                                    setupRecordedFilesRV()
                                    recordedFilesAdapter.setItems(filterdRecordList)
                                    binding.whenemptyrecordImage.visibility = View.GONE
                                    binding.listViewCountriesLiradio.visibility = View.VISIBLE
                                }
                            }
                        }
                    }





    }



}
