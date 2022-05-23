package com.amirami.simapp.radiostations.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                binding.RadioBitrateTXviewadd.setBackgroundColor(RadioFunction.parseColor("#00000000"))
                binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#00000000"))
                binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#00000000"))

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

                    RadioFunction.interatial_ads_show(requireContext())
                }

                else if (binding.RadioNameTXviewadd.text.toString().isNotEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty() && binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    )
                )  {

                    binding.RadioBitrateTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewadd.hint = getString(R.string.enternumber)

                }
                else if(binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                    binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }

                else if(binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty() && binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    )
                ){
                    binding.RadioBitrateTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewadd.hint = getString(R.string.enternumber)

                    binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                    binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }

                else if (binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ) &&binding.RadioNameTXviewadd.text.toString().isEmpty() ) {
                    binding.RadioBitrateTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewadd.hint = getString(R.string.enternumber)
                    binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }

                else if ( binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ) && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioBitrateTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewadd.hint = getString(R.string.enternumber)
                    binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }
                else if(binding.RadioNameTXviewadd.text.toString().isEmpty()){
                    binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }
                else if(binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }
            }

            // download
            else {
                binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#00000000"))
                binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#00000000"))
                if(binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()) {
                    binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))

                    binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }
                else if(binding.RadioNameTXviewadd.text.toString().isEmpty()){
                    binding.RadioNameTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }
                else if(binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()){
                    binding.RadioStreamlinkTXviewadd.setBackgroundColor(RadioFunction.parseColor("#C41230"))
                }
                else if(binding.RadioNameTXviewadd.text.toString().isNotEmpty() && binding.RadioStreamlinkTXviewadd.toString().isNotEmpty()){
                    //  customurltodownload= StreamURLTXview.text.toString()
                    if("https" in binding.RadioStreamlinkTXviewadd.text.toString()){
                        RadioFunction.getCutomDownloader(requireActivity(), binding.RadioNameTXviewadd.text.toString(), binding.RadioStreamlinkTXviewadd.text.toString())
                    }
                    else{
                        RadioFunction.getCutomDownloader(requireActivity(), binding.RadioNameTXviewadd.text.toString(), binding.RadioStreamlinkTXviewadd.text.toString().replace(Regex("http"), "https"))
                    }
                    MainActivity.customdownloader?.download()

                   dismiss()
                    RadioFunction.interatial_ads_show(requireContext())

                }
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