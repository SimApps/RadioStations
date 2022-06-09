package com.amirami.simapp.radiostations.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.BottomsheetfragmentAdddialogueBinding
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RadioRoomViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddDialogueBottomSheetFragment : BottomSheetDialogFragment() {
    private val infoViewModel: InfoViewModel by activityViewModels()
    private var _binding: BottomsheetfragmentAdddialogueBinding? = null
    private val radioRoomViewModel: RadioRoomViewModel by activityViewModels()
    val argsFrom: AddDialogueBottomSheetFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetfragmentAdddialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorTransitionBottomSheet(binding.containeraddoDialogue,4,it)
        }


        if(argsFrom.addDouwnload){
            binding.RadioTagsTXviewaddLayout.visibility = View.GONE
            binding.RadioBitrateTXviewaddLayout.visibility = View.GONE
            binding.RadioLanguageTXviewaddLayout.visibility = View.GONE
            binding.RadioCountryTXviewaddLayout.visibility = View.GONE
            binding.RadioHomepageTXviewaddLayout.visibility = View.GONE
            binding.RadioImagelinkTXviewaddLayout.visibility = View.GONE
        }






      binding.addBtn.setSafeOnClickListener {
            if(!argsFrom.addDouwnload){
                val sdfDate =  binding.RadioNameTXviewadd.toString() + SimpleDateFormat("MMM d yy_HH-mm-ss", Locale.getDefault())
                binding.RadioBitrateTXviewaddLayout.error=null
                binding.RadioNameTXviewaddLayout.error=null
                binding.RadioStreamlinkTXviewaddLayout.error=null


                if ((binding.RadioNameTXviewadd.text.toString().isNotEmpty() &&
                            binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty() &&
                            binding.RadioBitrateTXviewadd.text.toString().isEmpty()) || (binding.RadioNameTXviewadd.text.toString().isNotEmpty() &&
                            binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty() &&
                            binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ))) {

                    val radioroom = RadioRoom(
                        sdfDate,
                        binding.RadioNameTXviewadd.text.toString(),
                        binding.RadioBitrateTXviewadd.text.toString(),
                        binding.RadioHomepageTXviewadd.text.toString(),
                        "https://qtxasset.com/styles/breakpoint_xl_880px_w/s3/fiercebiotech/1607691764/connor-wells-534089-unsplash.jpg/connor-wells-534089-unsplash.jpg?IxnhKzf6LZYze4g.sUsGbEiQZd5tCaJN&itok=gmQPxIfy",
                        binding.RadioTagsTXviewadd.text.toString(),
                        binding.RadioCountryTXviewadd.text.toString(),
                        "",
                        //var RadiostateDB: String?,
                        binding.RadioLanguageTXviewadd.text.toString(),
                        if("https" in binding.RadioStreamlinkTXviewadd.text.toString()) binding.RadioStreamlinkTXviewadd.text.toString()
                        else  binding.RadioStreamlinkTXviewadd.text.toString().replace(Regex("http"), "https"),
                        true

                    )

                    radioRoomViewModel.upsertRadio(radioroom, "Radio added")
                  //  RadioFunction.recreateActivityCompat(RadioFunction.unwrap(requireContext()))
                    dismiss()

                    RadioFunction.interatialadsShow(requireContext())
                }

                else if (binding.RadioNameTXviewadd.text.toString().isNotEmpty()
                    && binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty()
                    && binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()))  {
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)

                }
                else if(binding.RadioNameTXviewadd.text.toString().isEmpty()
                    && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){

                    binding.RadioNameTXviewaddLayout.error  = getString(R.string.Cant_beEmpty)
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                }

                else if(binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty() && binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString())){
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)

                    binding.RadioNameTXviewaddLayout.error  = getString(R.string.Cant_beEmpty)
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                }

                else if (binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ) &&binding.RadioNameTXviewadd.text.toString().isEmpty() ) {
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)
                    binding.RadioNameTXviewaddLayout.error  = getString(R.string.Cant_beEmpty)
                }

                else if ( binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ) && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                }
                else if(binding.RadioNameTXviewadd.text.toString().isEmpty()){
                    binding.RadioNameTXviewaddLayout.error  = getString(R.string.Cant_beEmpty)
                }
                else if(binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                }
            }

            // download
            else {
                binding.RadioNameTXviewaddLayout.error=null
                binding.RadioStreamlinkTXviewaddLayout.error=null
                if(binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()) {
                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                }
                else if(binding.RadioNameTXviewadd.text.toString().isEmpty()){
                    binding.RadioNameTXviewaddLayout.error  = getString(R.string.Cant_beEmpty)
                }
                else if(binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioStreamlinkTXviewaddLayout.error= getString(R.string.Cant_beEmpty)
                }
                else if(binding.RadioNameTXviewadd.text.toString().isNotEmpty() && binding.RadioStreamlinkTXviewadd.toString().isNotEmpty()){
                    if (RadioFunction.allPermissionsGranted(requireContext())){
                        //  customurltodownload= StreamURLTXview.text.toString()
                        if("https" in binding.RadioStreamlinkTXviewadd.text.toString()){
                            RadioFunction.getCutomDownloader(requireActivity(), binding.RadioNameTXviewadd.text.toString(), binding.RadioStreamlinkTXviewadd.text.toString())
                        }
                        else{
                            RadioFunction.getCutomDownloader(requireActivity(), binding.RadioNameTXviewadd.text.toString(), binding.RadioStreamlinkTXviewadd.text.toString().replace(Regex("http"), "https"))
                        }
                        MainActivity.customdownloader?.download()

                        dismiss()
                        RadioFunction.interatialadsShow(requireContext())
                    }
                    else requestMultiplePermissions.launch(arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE))


                }
            }
        }
    }
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.value) {

                    if ("https" in MainActivity.GlobalRadiourl) RadioFunction.getCutomDownloader(
                        requireContext(),
                        MainActivity.GlobalRadioName,
                        MainActivity.GlobalRadiourl
                    )
                    else RadioFunction.getCutomDownloader(
                        requireContext(),
                        MainActivity.GlobalRadioName,
                        MainActivity.GlobalRadiourl.replace(
                            Regex(resources.getString(R.string.http)),
                            resources.getString(R.string.https)
                        )
                    )

                    MainActivity.customdownloader?.download()
                    DynamicToast.makeSuccess(
                        requireContext(),
                        getString(R.string.Permissionsgranted),
                        9
                    ).show()

                } else {
                    DynamicToast.makeError(
                        requireContext(),
                        getString(R.string.PermissionsNotgranted),
                        9
                    ).show()
                    MainActivity.customdownloader?.cancelDownload()
                }

            }
        }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    fun <T> AddDialogueBottomSheetFragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
}