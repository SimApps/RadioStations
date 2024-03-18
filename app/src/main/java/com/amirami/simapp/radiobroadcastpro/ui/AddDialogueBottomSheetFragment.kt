package com.amirami.simapp.radiobroadcastpro.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.navArgs
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.RadioFunction
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.databinding.BottomsheetfragmentAdddialogueBinding
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.utils.ManagePermissions
import com.amirami.simapp.radiobroadcastpro.viewmodel.DownloaderViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@UnstableApi @AndroidEntryPoint
class AddDialogueBottomSheetFragment : BottomSheetDialogFragment() {
    private val infoViewModel: InfoViewModel by activityViewModels()
    private var _binding: BottomsheetfragmentAdddialogueBinding? = null
    private val downloaderViewModel: DownloaderViewModel by activityViewModels()

    val argsFrom: AddDialogueBottomSheetFragmentArgs by navArgs()

    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 12322
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



        if (argsFrom.addDouwnload) {
            binding.RadioTagsTXviewaddLayout.visibility = View.GONE
            binding.RadioBitrateTXviewaddLayout.visibility = View.GONE
            binding.RadioLanguageTXviewaddLayout.visibility = View.GONE
            binding.RadioCountryTXviewaddLayout.visibility = View.GONE
            binding.RadioHomepageTXviewaddLayout.visibility = View.GONE
            binding.RadioImagelinkTXviewaddLayout.visibility = View.GONE
        }

        binding.addBtn.setSafeOnClickListener {
            if (!argsFrom.addDouwnload) {
                val sdfDate = binding.RadioNameTXviewadd.toString() + SimpleDateFormat("MMM d yy_HH-mm-ss", Locale.getDefault())
                binding.RadioBitrateTXviewaddLayout.error = null
                binding.RadioNameTXviewaddLayout.error = null
                binding.RadioStreamlinkTXviewaddLayout.error = null

                if ((
                    binding.RadioNameTXviewadd.text.toString().isNotEmpty() &&
                        binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty() &&
                        binding.RadioBitrateTXviewadd.text.toString().isEmpty()
                    ) || (
                        binding.RadioNameTXviewadd.text.toString().isNotEmpty() &&
                            binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty() &&
                            binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && RadioFunction.isNumber(
                                binding.RadioBitrateTXviewadd.text.toString()
                            )
                        )
                ) {
                    val radioroom = RadioEntity(
                        sdfDate,
                        binding.RadioNameTXviewadd.text.toString(),
                        binding.RadioBitrateTXviewadd.text.toString(),
                        binding.RadioHomepageTXviewadd.text.toString(),
                        "https://qtxasset.com/styles/breakpoint_xl_880px_w/s3/fiercebiotech/1607691764/connor-wells-534089-unsplash.jpg/connor-wells-534089-unsplash.jpg?IxnhKzf6LZYze4g.sUsGbEiQZd5tCaJN&itok=gmQPxIfy",
                        binding.RadioTagsTXviewadd.text.toString(),
                        binding.RadioCountryTXviewadd.text.toString(),
                        "",
                        // var RadiostateDB: String?,
                        binding.RadioLanguageTXviewadd.text.toString(),
                        if ("https" in binding.RadioStreamlinkTXviewadd.text.toString()) binding.RadioStreamlinkTXviewadd.text.toString()
                        else binding.RadioStreamlinkTXviewadd.text.toString().replace(Regex("http"), "https"),
                        fav = true,

                    )

                    infoViewModel.upsertRadio(radioroom, "Radio added")
                    //  RadioFunction.recreateActivityCompat(RadioFunction.unwrap(requireContext()))
                    dismiss()

                } else if (binding.RadioNameTXviewadd.text.toString().isNotEmpty() &&
                    binding.RadioStreamlinkTXviewadd.text.toString().isNotEmpty() &&
                    binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                            binding.RadioBitrateTXviewadd.text.toString()
                        )
                ) {
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)
                } else if (binding.RadioNameTXviewadd.text.toString().isEmpty() &&
                    binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()
                ) {
                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty() && binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    )
                ) {
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)

                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ) && binding.RadioNameTXviewadd.text.toString().isEmpty()
                ) {
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)
                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (
                    binding.RadioBitrateTXviewadd.text.toString().isNotEmpty() && !RadioFunction.isNumber(
                        binding.RadioBitrateTXviewadd.text.toString()
                    ) && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()
                ) {
                    binding.RadioBitrateTXviewaddLayout.error = getString(R.string.enternumber)
                    binding.RadioBitrateTXviewadd.text?.clear()
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioNameTXviewadd.text.toString().isEmpty()) {
                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()) {
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                }
            }

            // download
            else {
                binding.RadioNameTXviewaddLayout.error = null
                binding.RadioStreamlinkTXviewaddLayout.error = null
                if (binding.RadioNameTXviewadd.text.toString().isEmpty() && binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()) {
                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioNameTXviewadd.text.toString().isEmpty()) {
                    binding.RadioNameTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioStreamlinkTXviewadd.text.toString().isEmpty()) {
                    binding.RadioStreamlinkTXviewaddLayout.error = getString(R.string.Cant_beEmpty)
                } else if (binding.RadioNameTXviewadd.text.toString().isNotEmpty() && binding.RadioStreamlinkTXviewadd.toString().isNotEmpty()) {

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
                    val url = if ("https" in binding.RadioStreamlinkTXviewadd.text.toString()) {
                      binding.RadioStreamlinkTXviewadd.text.toString()

                    } else {
                        binding.RadioStreamlinkTXviewadd.text.toString().replace(Regex("http"), "https")
                    }

                    downloaderViewModel.startDownloader (customRecName =binding.RadioNameTXviewadd.text.toString() ,customUrl= url)



                    dismiss()
                }
                  /*  if (RadioFunction.allPermissionsGranted(requireContext())) {

                    } else requestMultiplePermissions.launch(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )*/
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
