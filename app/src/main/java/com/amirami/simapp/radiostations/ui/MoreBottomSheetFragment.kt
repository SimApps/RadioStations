package com.amirami.simapp.radiostations.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.RadioFunction.dynamicToast
import com.amirami.simapp.radiostations.RadioFunction.errorToast
import com.amirami.simapp.radiostations.RadioFunction.maintextviewColor
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.BottomsheetfragmentMoreBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.utils.Constatnts
import com.amirami.simapp.radiostations.viewmodel.FavoriteFirestoreViewModel
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RadioRoomViewModel
import com.amirami.simapp.radiostations.viewmodel.SimpleMediaViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@UnstableApi @AndroidEntryPoint
class MoreBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomsheetfragmentMoreBinding? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val radioRoomViewModel: RadioRoomViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val favoriteFirestoreViewModel: FavoriteFirestoreViewModel by activityViewModels()
    private val radioRoom: MutableList<RadioEntity> = mutableListOf()

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

        setTheme()


        collectLatestLifecycleFlow(infoViewModel.putRadioInfo) { radioVar ->
            setInfoRadio(radioVar)
            btnsVisibility(radioVar)
            if (radioVar.fav) {
                binding.likeImageView.setImageResource(R.drawable.ic_liked)
            } else {
                binding.likeImageView.setImageResource(R.drawable.ic_like)
            }


            binding.likeImageView.setSafeOnClickListener {

                if (!radioVar.fav && radioVar.stationuuid != "") {
                    binding.likeImageView.setImageResource(R.drawable.ic_liked)
                    addFavoriteRadioIdInArrayFirestore(radioVar.stationuuid)
                } else if (radioVar.fav) {
                    binding.likeImageView.setImageResource(R.drawable.ic_like)
                    deleteFavoriteRadioFromArrayinfirestore(radioVar.stationuuid)
                }
                radioRoomViewModel.setFavRadio(radioVar)

            }

            binding.shareImageView.setSafeOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        simpleMediaViewModel.icyStreamInfoState.collectLatest { icy ->

                            RadioFunction.shareRadio(
                                requireContext(),
                                radioVar,
                                icy
                            )
                        }
                    }
                }




                dismiss()
            }

            binding.addalarmimageView.setSafeOnClickListener {
                infoViewModel.putRadioalarmInfo(radioVar)
                val action = MoreBottomSheetFragmentDirections.actionMoreBottomSheetFragmentToMainAlarmFragment()
                this@MoreBottomSheetFragment.findNavController().navigate(action)
                // Utils.cancelAlarm(requireActivity())
                //   dismiss()
            }

            binding.VistitStationhomepageIm.setSafeOnClickListener {
                RadioFunction.homepageChrome(requireActivity(), radioVar.homepage)
                dismiss()
            }

            binding.gotorecordfilesImVw.setSafeOnClickListener {
                if (RadioFunction.getRecordedFiles(requireContext()).size == 0) {
                    DynamicToast.makeError(requireContext(), getString(R.string.Record_folder_empty), 3).show()
                } else {
                    if (MainActivity.firstTimeopenRecordfolder) {
                        MainActivity.firstTimeopenRecordfolder = false
                        if (!requireActivity().isFinishing) {
                            val action = MoreBottomSheetFragmentDirections.actionMoreBottomSheetFragmentToInfoBottomSheetFragment(
                                getString(R.string.Keep_in_mind),
                                getString(R.string.recordmessage)
                            )
                            this@MoreBottomSheetFragment.findNavController().navigate(action) //    NavHostFragment.findNavController(requireParentFragment()).navigate(action)
                        }
                    }
// Request code for selecting a PDF document.

                    RadioFunction.openRecordFolder(requireContext())
                }
            }
        }
    }

    private fun btnsVisibility(radioVar: RadioEntity) {
        if (radioVar.moreinfo == "fromplayer") binding.likeImageView.visibility = View.GONE
        else binding.likeImageView.visibility = View.VISIBLE

        if (radioVar.stationuuid == "") {
            binding.shareImageView.visibility = View.GONE
            binding.VistitStationhomepageIm.visibility = View.GONE
        } else {
            binding.shareImageView.visibility = View.VISIBLE
            binding.VistitStationhomepageIm.visibility = View.VISIBLE
        }
    }



    private fun addFavoriteRadioIdInArrayFirestore(radioUid: String) {
        val addFavoritRadioIdInArrayFirestore =
            favoriteFirestoreViewModel.addFavoriteRadioidinArrayFirestore(
                radioUid,
                RadioFunction.getCurrentDate()
            )
        addFavoritRadioIdInArrayFirestore.observe(viewLifecycleOwner) {
            // if (it != null)  if (it.data!!)  prod name array updated
            RadioFunction.interatialadsShow(requireContext())
            if (it.e != null) {
                // prod bame array not updated
                errorToast(requireContext(), it.e!!)
            }
        }
    }

    private fun deleteFavoriteRadioFromArrayinfirestore(radioUid: String) {
        val deleteFavoriteRadiofromArrayInFirestore = favoriteFirestoreViewModel.deleteFavoriteRadioFromArrayinFirestore(radioUid)
        deleteFavoriteRadiofromArrayInFirestore.observe(viewLifecycleOwner) {
            RadioFunction.interatialadsShow(requireContext())
            // if (it != null)  if (it.data!!)  prod name array updated
            if (it.e != null) {
                // prod bame array not updated
                dynamicToast(requireContext(), it.e!!)
            }
        }
    }

    private fun setTheme() {
        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorNestedScrollViewTransitionseconcolor(binding.linearLayoutHolder, 1, it)
            maintextviewColor(binding.RadioNameTXview, it)
            maintextviewColor(binding.RadioHomepageTXview, it)
            maintextviewColor(binding.RadioStreamURLTXview, it)
            maintextviewColor(binding.RadioCountryTXview, it)
            maintextviewColor(binding.RadioLanguageTXview, it)
            maintextviewColor(binding.RadioBitrateTXview, it)
            maintextviewColor(binding.RadioTagsTXview, it)
        }
    }

    private fun setInfoRadio(radio: RadioEntity) {
        if (radio.stationuuid == "") {
            binding.RadioImage.setImageResource(R.drawable.rec_on)
            //  RadioFunction.loadImageInt(R.drawable.recordings, MainActivity.imagedefaulterrorurl, binding.RadioImage)
            binding.RadioNameTXview.text = getString(R.string.infoRadioname, radio.name)
            binding.RadioHomepageTXview.text = getString(R.string.inforecord, /*MainActivity.icyandState*/radio.homepage)

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
    private fun <T> BottomSheetDialogFragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }

   /* fun <T> BottomSheetDialogFragment.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(collect)
            }
        }
    }*/
}
