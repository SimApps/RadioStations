package com.amirami.simapp.radiostations.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioListAdapterVertical
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiostations.databinding.BottomsheetChooseDefDialogueBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@UnstableApi @AndroidEntryPoint
class ChooseDefBottomSheetFragment : BottomSheetDialogFragment(), RadioListAdapterVertical.OnItemClickListener {
    private var _binding: BottomsheetChooseDefDialogueBinding? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val dataViewModel: DataViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private lateinit var radioAdapterVertical: RadioListAdapterVertical
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetChooseDefDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!
    val argsFrom: ChooseDefBottomSheetFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupRadioLisRV()

        binding.itemErrorMessage.btnRetry.setSafeOnClickListener {
            retrofitRadioViewModel.changeBseUrl()
            infoViewModel.putDefServerInfo(MainActivity.BASE_URL)
            when (argsFrom.msg) {
                "defcountry" -> retrofitRadioViewModel.getListCountrieRadios() // retrofitRadioViewModel.getListRadios(getString(R.string.Countries))
                "defserver" -> retrofitRadioViewModel.getListservers()
                else -> binding.itemErrorMessage.root.visibility = View.INVISIBLE
            }
        }

        when (argsFrom.msg) {
            "defcountry" -> {
                setUpListCountryRv()
               // binding.spinKitCountryFav.visibility = View.VISIBLE
              //  binding.listViewCountries.visibility = View.VISIBLE
                binding.choosedefTitle.text = getString(R.string.choose_countrie)
                // retrofitRadioViewModel.getListCountrieRadios()
            }
            "defserver" -> {
                setUpRv()
               // binding.spinKitCountryFav.visibility = View.VISIBLE
              //  binding.listViewCountries.visibility = View.VISIBLE
                binding.choosedefTitle.text = getString(R.string.choose_server)
                retrofitRadioViewModel.getListservers()
            }
            "deftheme" -> {
                binding.itemErrorMessage.root.visibility = View.INVISIBLE
                binding.choosedefTitle.text = getString(R.string.choose_theme)
                binding.spinKitCountryFav.visibility = View.GONE
                binding.listViewCountries.visibility = View.GONE



            }
        }
    }

    override fun onItemClick(radio: RadioEntity) {
        if (radio.ip != "") {
            try {
                //    if(globalserversJson[position]!=""){

                dataViewModel.saveChoosenServer("http://" + radio.ip)
                MainActivity.BASE_URL = "http://" + radio.ip
                infoViewModel.putDefServerInfo(MainActivity.BASE_URL)

                dismiss()
                // }
                //  else DynamicToast.makeError(requireContext(), "Requested Server is Offline! Try another server ", 3).show()
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
        } else {
            try {
              dataViewModel.saveDefaultCountry(radio.name)
                MainActivity.defaultCountry = radio.name
                infoViewModel.putDefCountryInfo(RadioFunction.countryCodeToName(radio.name))
                // fav_country_txv.text = getString(R.string.defaultCountry,  RadioFunction.countryCodeToName(globalCountriesJson[position]))
                dismiss()
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
    }

    private fun setUpListCountryRv() {
        collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.responseListCountrieRadio) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        hideProgressBar()
                        binding.itemErrorMessage.root.visibility = View.INVISIBLE
                        // radioAdapterVertical.radioListdffer = response.data as List<RadioVariables>
                        radioAdapterVertical.setItems(response.data as MutableList<RadioEntity>)
                    } else showErrorConnection(response.message!!)
                }
                Status.ERROR -> {
                    retrofitRadioViewModel.changeBseUrl()
                    infoViewModel.putDefServerInfo(MainActivity.BASE_URL)
                    hideProgressBar()
                    showErrorConnection(response.message!!)
                }
                Status.LOADING -> {
                    displayProgressBar()
                }
            }
        }
    }
    private fun setUpRv() {
        collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.responseRadioList) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        hideProgressBar()
                        binding.itemErrorMessage.root.visibility = View.INVISIBLE
                        // radioAdapterVertical.radioListdffer = response.data as List<RadioVariables>
                        radioAdapterVertical.setItems(response.data as MutableList<RadioEntity>)
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
    }
    fun showErrorConnection(msg: String) {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = msg
    }
    private fun displayProgressBar() {
        binding.spinKitCountryFav.visibility = View.VISIBLE
        binding.listViewCountries.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.spinKitCountryFav.visibility = View.GONE
        binding.listViewCountries.visibility = View.VISIBLE
    }

    private fun setupRadioLisRV() {
        radioAdapterVertical = RadioListAdapterVertical(this)
        binding.listViewCountries.apply {
            adapter = radioAdapterVertical
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
