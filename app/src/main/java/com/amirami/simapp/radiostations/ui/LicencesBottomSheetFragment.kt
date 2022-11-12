package com.amirami.simapp.radiostations.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.BottomSheetLicencesBinding
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LicencesBottomSheetFragment : BottomSheetDialogFragment() {
    private val infoViewModel: InfoViewModel by activityViewModels()

    private var _binding: BottomSheetLicencesBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLicencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorTransitionBottomSheet(binding.licenceContainer, 0, it)

            RadioFunction.secondarytextviewColor(binding.textView7, it)
            RadioFunction.maintextviewColor(binding.flatIcontxview, it)
            RadioFunction.maintextviewColor(binding.androidxTxtV, it)
            RadioFunction.maintextviewColor(binding.retrofitTxtV, it)
            RadioFunction.maintextviewColor(binding.coilTxtV, it)
            RadioFunction.maintextviewColor(binding.jetpackTxtV, it)
            RadioFunction.maintextviewColor(binding.ExoPlayerTxtV, it)
            RadioFunction.maintextviewColor(binding.KeyboardVisibilityEventTxtV, it)
            RadioFunction.maintextviewColor(binding.GooglePlayServicesTxtV, it)
            RadioFunction.maintextviewColor(binding.FirebaseAndroidTxtV, it)
            RadioFunction.maintextviewColor(binding.RecyVFastScrollTxtV, it)
            RadioFunction.maintextviewColor(binding.downloadLiberaryTxV, it)
            RadioFunction.maintextviewColor(binding.radioBrowserTxtV, it)
            RadioFunction.maintextviewColor(binding.lottiesTxtV, it)
        }

        binding.androidxTxtV.setSafeOnClickListener {
            val url = "https://developer.android.com/jetpack/androidx"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.retrofitTxtV.setSafeOnClickListener {
            val url = "https://square.github.io/retrofit/"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.coilTxtV.setSafeOnClickListener {
            val url = "https://coil-kt.github.io/coil/#license"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.lottiesTxtV.setSafeOnClickListener {
            val url = "https://lottiefiles.com/"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.jetpackTxtV.setSafeOnClickListener {
            val url = "https://developer.android.com/jetpack"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.ExoPlayerTxtV.setSafeOnClickListener {
            val url = "https://github.com/google/ExoPlayer"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.KeyboardVisibilityEventTxtV.setSafeOnClickListener {
            val url = "https://github.com/yshrsmz/KeyboardVisibilityEvent"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.GooglePlayServicesTxtV.setSafeOnClickListener {
            val url = "https://developers.google.com/android/guides/setup"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.FirebaseAndroidTxtV.setSafeOnClickListener {
            val url = "https://developers.google.com/android/guides/setup"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.flaticonLayout.setSafeOnClickListener {
            val url = "https://www.flaticon.com/authors/freepik"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.RecyVFastScrollTxtV.setSafeOnClickListener {
            val url = "https://github.com/timusus/RecyclerView-FastScroll"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.downloadLiberaryTxV.setSafeOnClickListener {
            val url = "https://github.com/alirezat775/downloader?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=7722"
            RadioFunction.homepageChrome(requireContext(), url)
        }

        binding.radioBrowserTxtV.setSafeOnClickListener {
            val url = "http://www.radio-browser.info/gui/#!/"
            RadioFunction.homepageChrome(requireContext(), url)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
}
