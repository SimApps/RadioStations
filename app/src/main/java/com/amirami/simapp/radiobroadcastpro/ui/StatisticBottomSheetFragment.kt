package com.amirami.simapp.radiobroadcastpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import com.amirami.simapp.radiobroadcastpro.MainActivity
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcastpro.databinding.StatisticsDialogueBinding
import com.amirami.simapp.radiobroadcastpro.model.Status
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.RetrofitRadioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi @AndroidEntryPoint
class StatisticBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: StatisticsDialogueBinding? = null
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val infoViewModel: InfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatisticsDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemErrorMessage.btnRetry.setOnClickListener {
            retrofitRadioViewModel.changeBseUrl()
            infoViewModel.putDefServerInfo(MainActivity.BASE_URL)

            retrofitRadioViewModel.getStatis()
            //      retrofitRadioViewModel.getStatis()
        }



        collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.statsresponse) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        binding.itemErrorMessage.root.visibility = View.INVISIBLE
                        binding.StationNbrTxV.text = getString(R.string.statNbrStation, (response.data as JsonObject).get("stations").toString())
                        binding.stationsBrokentxV.text = getString(R.string.statNbrStationBroken, (response.data as JsonObject).get("stations_broken").toString())
                        binding.tagsTxV.text = getString(R.string.statNbrTags, (response.data as JsonObject).get("tags").toString())
                        binding.languagesTxV.text = getString(R.string.statNbrLanguage, (response.data as JsonObject).get("languages").toString())
                        binding.countriesTxV.text = getString(R.string.statNbrCountries, (response.data as JsonObject).get("countries").toString())

                        hideProgressBar()
                    } else showErrorConnection(response.message!!)
                }
                Status.ERROR -> {
                    hideProgressBar()
                    showErrorConnection(response.message!!)
                }
                Status.LOADING -> {
                    displayProgressBar()
                }
            }
        }

        /*  lifecycleScope.launchWhenCreated {
              retrofitRadioViewModel.stateFlowTrigger.flowWithLifecycle(requireActivity().lifecycle, Lifecycle.State.STARTED)
                  .distinctUntilChanged()
                  .collectLatest {

                  }
          }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showErrorConnection(msg: String) {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = msg
    }
    private fun displayProgressBar() {
        binding.spinKits.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.spinKits.visibility = View.GONE
    }


}