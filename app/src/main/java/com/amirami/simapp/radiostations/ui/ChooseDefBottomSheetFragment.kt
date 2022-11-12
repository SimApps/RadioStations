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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.MainActivity.Companion.darkTheme
import com.amirami.simapp.radiostations.MainActivity.Companion.systemTheme
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioListAdapterVertical
import com.amirami.simapp.radiostations.databinding.BottomsheetChooseDefDialogueBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.preferencesmanager.PreferencesViewModel
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class ChooseDefBottomSheetFragment : BottomSheetDialogFragment(), RadioListAdapterVertical.OnItemClickListener {
    private var _binding: BottomsheetChooseDefDialogueBinding? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val preferencesViewModel: PreferencesViewModel by activityViewModels()
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

        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorTransitionBottomSheet(binding.defcountryRelativelay, 4, it)
            RadioFunction.secondarytextviewColor(binding.choosedefTitle, it)
            RadioFunction.buttonColor(binding.btnDark, it)
            RadioFunction.buttonColor(binding.btnLight, it)
            RadioFunction.buttonColor(binding.btnSysThme, it)
        }

        setupRadioLisRV()

        binding.itemErrorMessage.btnRetry.setSafeOnClickListener {
            retrofitRadioViewModel.changeBseUrl()
            infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)
            when (argsFrom.msg) {
                "defcountry" -> retrofitRadioViewModel.getListCountrieRadios() // retrofitRadioViewModel.getListRadios(getString(R.string.Countries))
                "defserver" -> retrofitRadioViewModel.getListservers()
                else -> binding.itemErrorMessage.root.visibility = View.INVISIBLE
            }
        }

        when (argsFrom.msg) {
            "defcountry" -> {
                setUpListCountryRv()
                binding.toggleButtonGroup.visibility = View.GONE
                binding.spinKitCountryFav.visibility = View.VISIBLE
                binding.listViewCountries.visibility = View.VISIBLE
                binding.choosedefTitle.text = getString(R.string.choose_countrie)
                // retrofitRadioViewModel.getListCountrieRadios()
            }
            "defserver" -> {
                setUpRv()
                binding.toggleButtonGroup.visibility = View.GONE
                binding.spinKitCountryFav.visibility = View.VISIBLE
                binding.listViewCountries.visibility = View.VISIBLE
                binding.choosedefTitle.text = getString(R.string.choose_server)
                retrofitRadioViewModel.getListservers()
            }
            "deftheme" -> {
                binding.itemErrorMessage.root.visibility = View.INVISIBLE
                binding.choosedefTitle.text = getString(R.string.choose_theme)
                binding.toggleButtonGroup.visibility = View.VISIBLE
                binding.spinKitCountryFav.visibility = View.GONE
                binding.listViewCountries.visibility = View.GONE

                if (systemTheme) binding.toggleButtonGroup.check(R.id.btnSysThme)
                else {
                    if (darkTheme) binding.toggleButtonGroup.check(R.id.btnDark)
                    else binding.toggleButtonGroup.check(R.id.btnLight)
                }

                binding.toggleButtonGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

                    if (isChecked) {
                        when (checkedId) {
                            R.id.btnDark -> {
                                darkTheme = true
                                systemTheme = false
                                //  DynamicToast.makeError(requireContext(), "ssss", 9).show()
                                dismiss()
                            }
                            R.id.btnLight -> {
                                darkTheme = false
                                systemTheme = false
                                //   DynamicToast.makeError(requireContext(), "ffff", 9).show()
                                dismiss()
                            }
                            R.id.btnSysThme -> {
                                systemTheme = true
                                val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                                when (isNightTheme) {
                                    Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
                                    Configuration.UI_MODE_NIGHT_NO -> darkTheme = false
                                }
                                //     DynamicToast.makeError(requireContext(), "rrrr", 9).show()
                                dismiss()
                            }
                        }
                    } else {
                        if (toggleButtonGroup.checkedButtonId == View.NO_ID) {
                            if (systemTheme) toggleButtonGroup.check(R.id.btnSysThme)
                            else {
                                if (darkTheme) toggleButtonGroup.check(R.id.btnDark)
                                else toggleButtonGroup.check(R.id.btnLight)
                            }
                        }
                    }
                    preferencesViewModel.onDarkThemeChanged(darkTheme)
                    preferencesViewModel.onSystemThemeChanged(systemTheme)
                    infoViewModel.putPutDefThemeInfo(darkTheme, systemTheme)

                    infoViewModel.putThemes(darkTheme)
                }
            }
        }
    }

    override fun onItemClick(radio: RadioVariables) {
        if (radio.ip != "") {
            try {
                //    if(globalserversJson[position]!=""){
                preferencesViewModel.onChoosenServerChanged("http://" + radio.ip)

                MainActivity.BASE_URL = "http://" + radio.ip
                infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)

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
                preferencesViewModel.onDefaultCountryChanged(radio.name)
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
        collectLatestLifecycleFlow(retrofitRadioViewModel.responseListCountrieRadio) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        hideProgressBar()
                        binding.itemErrorMessage.root.visibility = View.INVISIBLE
                        // radioAdapterVertical.radioListdffer = response.data as List<RadioVariables>
                        radioAdapterVertical.setItems(response.data as MutableList<RadioVariables>)
                    } else showErrorConnection(response.message!!)
                }
                Status.ERROR -> {
                    retrofitRadioViewModel.changeBseUrl()
                    infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)
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
        collectLatestLifecycleFlow(retrofitRadioViewModel.responseRadioList) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    if (response.data != null) {
                        hideProgressBar()
                        binding.itemErrorMessage.root.visibility = View.INVISIBLE
                        // radioAdapterVertical.radioListdffer = response.data as List<RadioVariables>
                        radioAdapterVertical.setItems(response.data as MutableList<RadioVariables>)
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
    private fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
}
