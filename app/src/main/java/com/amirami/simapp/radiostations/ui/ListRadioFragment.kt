package com.amirami.simapp.radiostations.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction.errorToast
import com.amirami.simapp.radiostations.RadioFunction.getRecordedFiles
import com.amirami.simapp.radiostations.RadioFunction.gradiancolorTransitionConstraint
import com.amirami.simapp.radiostations.RadioFunction.icyandStateWhenPlayRecordFiles
import com.amirami.simapp.radiostations.RadioFunction.indexesOf
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.RadioFunction.shortformateDate
import com.amirami.simapp.radiostations.adapter.RadioListAdapterVertical
import com.amirami.simapp.radiostations.adapter.RecordedFilesAdapter
import com.amirami.simapp.radiostations.databinding.FragmentListradioBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.model.RecordInfo
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.utils.ManagePermissions
import com.amirami.simapp.radiostations.utils.exhaustive
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class ListRadioFragment : Fragment(R.layout.fragment_listradio), RadioListAdapterVertical.OnItemClickListener, RecordedFilesAdapter.OnItemClickListener {
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private lateinit var radioAdapterHorizantal: RadioListAdapterVertical
    private lateinit var recordedFilesAdapter: RecordedFilesAdapter
    private lateinit var binding: FragmentListradioBinding
    val argsFrom: ListRadioFragmentArgs by navArgs()

    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 12322
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListradioBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.recordEvents.collectLatest { event ->
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
            }
        }

        binding.itemErrorMessage.btnRetry.setSafeOnClickListener {
            if (argsFrom.msg != "Empty") {
                retrofitRadioViewModel.changeBseUrl()
                infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)

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
            setUpRv()
            binding.floatingActionAddDownload.visibility = View.INVISIBLE
        } else if (argsFrom.msg == resources.getString(R.string.Recordings)) {
            Exoplayer.Observer.subscribeImageRecord("floatingActionAddDownload", binding.floatingActionAddDownload)

            binding.floatingActionAddDownload.visibility = View.VISIBLE

            // fabColor(floating_action_addDownload)

            binding.floatingActionAddDownload.setSafeOnClickListener {
                if (MainActivity.isDownloadingCustomurl) {
                    MainActivity.customdownloader?.cancelDownload()
                } else {
                    if (!Exoplayer.is_downloading) {
                        val action = ListRadioFragmentDirections.actionListRadioFragmentToAddDialogueBottomSheetFragment(true)
                        NavHostFragment.findNavController(requireParentFragment()).navigate(action)
                    } else DynamicToast.makeError(requireContext(), getString(R.string.cantadddownloedwhenrecord), 6).show()
                }
            }

            setUpRecord()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    gradiancolorTransitionConstraint(binding.contentlistradio, 0, it)
                }
            }
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value) setUpRecord()
                else {
                    Log.d("kksjq", "not")
                    errorToast(requireContext(), getString(R.string.PermissionsNotgranted))
                    MainActivity.customdownloader?.cancelDownload()
                }
            }
        }

    private fun setUpRv() {
        if (argsFrom.msg == getString(R.string.countries)) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    retrofitRadioViewModel.responseListCountrieRadio.collectLatest { response ->
                        when (response.status) {
                            Status.SUCCESS -> {
                                if (response.data != null) {
                                    hideProgressBar()
                                    binding.itemErrorMessage.root.visibility = View.INVISIBLE
                                    //     radioAdapterHorizantal.radioListdffer = response.data as List<RadioVariables>
                                    radioAdapterHorizantal.setItems(response.data as MutableList<RadioVariables>)
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
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    retrofitRadioViewModel.responseRadioList.collectLatest { response ->
                        when (response.status) {
                            Status.SUCCESS -> {
                                if (response.data != null) {
                                    hideProgressBar()
                                    binding.itemErrorMessage.root.visibility = View.INVISIBLE
                                    //     radioAdapterHorizantal.radioListdffer = response.data as List<RadioVariables>
                                    radioAdapterHorizantal.setItems(response.data as MutableList<RadioVariables>)
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

    override fun onItemClick(radio: RadioVariables) {
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

    override fun onRecItemClick(recordInfo: RecordInfo) {
        Exoplayer.initializePlayer(
            requireContext(),
            true,
            recordInfo.uri!!
            /* if(recordInfo.uri!=null) recordInfo.uri!! else Uri.parse("")*/
        )

        val radioVariables = RadioVariables()
        radioVariables.url_resolved = recordInfo.uri.toString()

        if (recordInfo.name.contains("_ _", true) && recordInfo.name.contains("___", true)) {
            radioVariables.name = recordInfo.name.substring(0, recordInfo.name.indexesOf("_ _", true)[0])

            radioVariables.homepage = recordInfo.name.substring(
                recordInfo.name.indexesOf("_ _", true)[0] + 3,
                recordInfo.name.indexesOf("___", true)[0]
            ) + " " +
                shortformateDate(
                    recordInfo.name.substring(
                        recordInfo.name.indexesOf("___", true)[0] + 3,
                        recordInfo.name.length - 4
                    )
                ) + ".mp3"
        } else {
            radioVariables.name = recordInfo.name
            radioVariables.homepage = ""
        }

        // DynamicToast.makeSuccess(requireContext(), "refresh frag PLAYER", 9).show()
        Exoplayer.startPlayer()
        Exoplayer.Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles("", radioVariables.homepage))
        Exoplayer.Observer.changeText("text view", icyandStateWhenPlayRecordFiles("", radioVariables.homepage))
        // Exoplayer.Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles("",radioVariables.homepage))

        infoViewModel.putRadiopalyerInfo(radioVariables)
    }

    override fun onRecMoreItemClick(recordInfo: RecordInfo, position: Int) {
        val action = ListRadioFragmentDirections.actionListRadioFragmentToInfoBottomSheetFragment(argsFrom.msg, position.toString(), recordInfo.name)
        NavHostFragment.findNavController(requireParentFragment()).navigate(action)
    }

    private fun setUpRecord() {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf<String>(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            listOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)

        if (managePermissions.isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            managePermissions.checkPermissions()
        } else {
            //  if (RadioFunction.allPermissionsGranted(requireContext())) {
            Log.d("kksjq", "1")
            if (getRecordedFiles(requireContext()).size == 0) {
                binding.listViewCountriesLiradio.visibility = View.GONE
                binding.whenemptyrecordImage.visibility = View.VISIBLE
            } else {
                setupRecordedFilesRV()
                recordedFilesAdapter.setItems(getRecordedFiles(requireContext()))
                binding.whenemptyrecordImage.visibility = View.GONE
                binding.listViewCountriesLiradio.visibility = View.VISIBLE
            }
           /* } else {
                Log.d("kksjq", "2")
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }  */
        }
    }
}
