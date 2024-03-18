package com.amirami.simapp.radiobroadcastpro.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.amirami.simapp.radiobroadcastpro.*
import com.amirami.simapp.radiobroadcastpro.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.databinding.BottomsheetfragmentMoreBinding
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts
import com.amirami.simapp.radiobroadcastpro.viewmodel.FavoriteFirestoreViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.SimpleMediaViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi @AndroidEntryPoint
class MoreBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomsheetfragmentMoreBinding? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val favoriteFirestoreViewModel: FavoriteFirestoreViewModel by activityViewModels()
    private val radioRoom: MutableList<RadioEntity> = mutableListOf()


    private lateinit var alarmActivityIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetfragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.putRadioInfo) { radioVar ->

            setInfoRadio(radioVar)
            btnsVisibility(radioVar)





                            collectLatestLifecycleFlow(lifecycleOwner = this,simpleMediaViewModel.state) { state ->

                                binding.shareImageView.setSafeOnClickListener {
                                    RadioFunction.shareRadio(
                                        context =  requireContext(),
                                        radio = radioVar,
                                        icy = state.radioState.icyState?:"",// state.icyStreamInfoState,
                                        isRec = radioVar.isRec
                                    )
                                }





               // dismiss()
            }


            binding.VistitStationhomepageIm.setSafeOnClickListener {
                RadioFunction.homepageChrome(requireActivity(), radioVar.homepage)
                dismiss()
            }


            binding.addAlarmBtn.setSafeOnClickListener {



                    val action = MoreBottomSheetFragmentDirections.actionMoreBottomSheetFragmentToAlarmFragment()
                    findNavController().navigate(action) //  NavHostFragment.findNavController(requireParentFragment()).navigate(action)
                dismiss()
            }
         }
    }

    private fun btnsVisibility(radioVar: RadioEntity) {


        if (radioVar.stationuuid == "") {
            binding.shareImageView.visibility = View.GONE
            binding.VistitStationhomepageIm.visibility = View.GONE
        } else {
            binding.shareImageView.visibility = View.VISIBLE
            binding.VistitStationhomepageIm.visibility = View.VISIBLE
        }
    }







    private fun setInfoRadio(radio: RadioEntity) {
        if (radio.stationuuid == "") {
            binding.RadioImage.setImageResource(R.drawable.rec_on)
            //  RadioFunction.loadImageInt(R.drawable.recordings, MainActivity.imagedefaulterrorurl, binding.RadioImage)
            binding.RadioNameTXview.text = getString(R.string.infoRadioname, radio.name)
            binding.RadioHomepageTXview.text = getString(R.string.inforecord, /*MainActivity.icyandState*/radio.icyState)

            binding.RadioStreamURLTXview.visibility = View.INVISIBLE
            binding.RadioCountryTXview.visibility = View.INVISIBLE
            binding.RadioLanguageTXview.visibility = View.INVISIBLE
            binding.RadioBitrateTXview.visibility = View.INVISIBLE
            binding.RadioTagsTXview.visibility = View.INVISIBLE
        } else {
            RadioFunction.loadImageString(
                requireContext(),
                radio.favicon,
                MainActivity.imagedefaulterrorurl,
                binding.RadioImage,
                Constatnts.CORNER_RADIUS_8F
            )

            binding.RadioNameTXview.text = getString(R.string.infoRadioname, radio.name)
            binding.RadioHomepageTXview.text = getString(R.string.infoHomepage, radio.homepage)

            binding.RadioStreamURLTXview.text = getString(R.string.infoRadiostreamURL, radio.streamurl)
            binding.RadioCountryTXview.text = getString(R.string.infoCountry, radio.country)
            binding.RadioStateTXview.text = getString(R.string.infoState, radio.state)
            binding.RadioLanguageTXview.text = getString(R.string.infoLanguage, radio.language)
            binding.RadioBitrateTXview.text = getString(R.string.infoBitrate, radio.bitrate)
            binding.RadioTagsTXview.text = getString(R.string.infoTags, radio.tags)

            binding.RadioStreamURLTXview.visibility = View.VISIBLE
            binding.RadioCountryTXview.visibility = View.VISIBLE
            binding.RadioLanguageTXview.visibility = View.VISIBLE
            binding.RadioBitrateTXview.visibility = View.VISIBLE
            binding.RadioTagsTXview.visibility = View.VISIBLE

            binding.RadioStreamURLTXview.setOnLongClickListener {
                //   Radio_StreamURLTXview.setOnClickListener {
                val textToCopy: String = binding.RadioStreamURLTXview.text.toString().replace("Radio Stream URL : ", "")
                val myClipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val myClip: ClipData = ClipData.newPlainText("note_copy", textToCopy)
                myClipboard.setPrimaryClip(myClip)
                DynamicToast.makeSuccess(requireContext(), "Stream URL Copied", 3).show()

                true
            }

            binding.RadioHomepageTXview.setOnLongClickListener {
                val textToCopy: String = binding.RadioHomepageTXview.text.toString().replace("Homepage : ", "")
                val myClipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val myClip: ClipData = ClipData.newPlainText("note_copy", textToCopy)
                myClipboard.setPrimaryClip(myClip)

                DynamicToast.makeSuccess(requireContext(), "Radio Homepage URL Copied", 3).show()

                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }









}
