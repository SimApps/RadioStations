package com.amirami.simapp.radiostations.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.adapter.RadioListAdapterVertical
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import com.amirami.simapp.radiostations.databinding.StatisticsDialogueBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.model.Resource
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.google.gson.JsonObject
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
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
            infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)

            retrofitRadioViewModel.getStatis()
          //      retrofitRadioViewModel.getStatis()
        }

        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorConstraintBottomSheet(binding.linearL, 0,it)
            RadioFunction.maintextviewColor(binding.StationNbrTxV,it)
            RadioFunction.maintextviewColor(binding.stationsBrokentxV,it)
            RadioFunction.maintextviewColor(binding.tagsTxV,it)
            RadioFunction.maintextviewColor(binding.languagesTxV,it)
            RadioFunction.maintextviewColor(binding.countriesTxV,it)
        }



        collectLatestLifecycleFlow(retrofitRadioViewModel.statsresponse) {response ->
            when (response.status) {
                Status.SUCCESS -> {
                    if(response.data!=null){
                        binding.itemErrorMessage.root.visibility= View.INVISIBLE
                        binding.StationNbrTxV.text=getString(R.string.statNbrStation,(response.data as JsonObject).get("stations").toString())
                        binding.stationsBrokentxV.text=getString(R.string.statNbrStationBroken, (response.data as JsonObject).get("stations_broken").toString())
                        binding.tagsTxV.text=getString(R.string.statNbrTags, (response.data as JsonObject).get("tags").toString())
                        binding.languagesTxV.text=getString(R.string.statNbrLanguage,(response.data as JsonObject).get("languages").toString())
                        binding.countriesTxV.text=getString(R.string.statNbrCountries,(response.data as JsonObject).get("countries").toString())

                        hideProgressBar()
                    }
                    else showErrorConnection(response.message!!)

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
        _binding=null
    }


    fun showErrorConnection(msg:String){
        binding.itemErrorMessage.root.visibility= View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = msg
    }
    private fun displayProgressBar() {
        binding.spinKits.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.spinKits.visibility = View.GONE
    }

    fun <T> StatisticBottomSheetFragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
}