package com.amirami.simapp.radiostations.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.MainActivity.Companion.BASE_URL
import com.amirami.simapp.radiostations.MainActivity.Companion.darkTheme
import com.amirami.simapp.radiostations.MainActivity.Companion.saveData
import com.amirami.simapp.radiostations.RadioFunction.countryCodeToName
import com.amirami.simapp.radiostations.RadioFunction.gradiancolorNestedScrollViewTransition
import com.amirami.simapp.radiostations.RadioFunction.gradiancolorTransition
import com.amirami.simapp.radiostations.RadioFunction.maintextviewColor
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.alarm.BootCompleteReceiver
import com.amirami.simapp.radiostations.alarm.Utils
import com.amirami.simapp.radiostations.alarm.Utils.cancelAlarm
import com.amirami.simapp.radiostations.databinding.FragmentSettingBinding
import com.amirami.simapp.radiostations.preferencesmanager.PreferencesViewModel
import com.amirami.simapp.radiostations.utils.exhaustive
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private lateinit var binding : FragmentSettingBinding

    private val infoViewModel: InfoViewModel by activityViewModels()
    private val preferencesViewModel : PreferencesViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding  = FragmentSettingBinding.bind(view)


        infoViewModel.putTitleText(getString(R.string.Settings))
        themeChange()




        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.favCountryTxv.text = getString(R.string.defaultCountry, countryCodeToName(preferencesViewModel.preferencesFlow.first().default_country))
            }
        }





        //  radio_servers_txv.text =prefs.getString("prefered_servers", "http://$BASE_URL")!!
        binding.radioServersTxv.text =   getString(R.string.currentserver, BASE_URL)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.dialogueEvents.collectLatest { event ->
                    when (event) {
                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDefCountryInfo -> {
                            run {
                                binding.favCountryTxv.text = getString(R.string.defaultCountry, event.country)
                            }

                        }

                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDefServerInfo -> {
                            run {
                                binding.radioServersTxv.text =   getString(R.string.currentserver, event.server)
                            }

                        }
                        is InfoViewModel.ChooseDefBottomSheetEvents.PutDefThemeInfo -> {
                            run {
                                themeChange()
                            }

                        }
                        else -> {}
                    }.exhaustive

                }

            }
        }






     /*   viewLifecycleOwner.lifecycleScope.launch {
            binding.saveData.isChecked = preferencesViewModel.preferencesFlow.first().save_data
        //    binding.saveData.isEnabled = preferencesViewModel.preferencesFlow.first().save_data
        }*/
        binding.saveData.isChecked =   saveData

        binding.saveData.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.saveData.text = getString(R.string.Disable_Load_radio_images)
                preferencesViewModel.onSaveDataChanged(isChecked)
                saveData= isChecked
            }
            else {
                binding.saveData.text = getString(R.string.Enable_Load_radio_images)
                preferencesViewModel.onSaveDataChanged(isChecked)
                saveData = isChecked


            }

        }


        binding.StaticsTxV.setSafeOnClickListener {
            retrofitRadioViewModel.getStatis()
            this@SettingFragment.findNavController().navigate(R.id.action_fragmentSetting_to_statisticBottomSheetFragment) //       NavHostFragment.findNavController(this).navigate(R.id.action_fragmentSetting_to_statisticBottomSheetFragment)
        }

        binding.LicencesTxtV.setSafeOnClickListener{
            this@SettingFragment.findNavController().navigate(R.id.action_fragmentSetting_to_licencesBottomSheetFragment) //       NavHostFragment.findNavController(this).navigate(R.id.action_fragmentSetting_to_licencesBottomSheetFragment)
        }

        binding.ContactUsTxV.setSafeOnClickListener{
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "amirami.simapp@gmail.com", null))
            startActivity(Intent.createChooser(intent, getString(R.string.Choose_Emailclient)))
        }

        binding.DisclaimerTxtV.setSafeOnClickListener{
            val action = SettingFragmentDirections.actionFragmentSettingToInfoBottomSheetFragment(getString(R.string.discaimertitle),getString(R.string.disclaimermessage))
            this@SettingFragment.findNavController().navigate(action) //       NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.batterisettings.setSafeOnClickListener{
            val action = SettingFragmentDirections.actionFragmentSettingToInfoBottomSheetFragment("BatterieOptimisation")
            this@SettingFragment.findNavController().navigate(action) //     NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }



        binding.themeTxvw.setSafeOnClickListener{
            val action = SettingFragmentDirections.actionFragmentSettingToChooseDefBottomSheetFragment("deftheme")
            this@SettingFragment.findNavController().navigate(action) //        NavHostFragment.findNavController(requireParentFragment()).navigate(action)

        }



        binding.favCountryTxv.setSafeOnClickListener{
            val action = SettingFragmentDirections.actionFragmentSettingToChooseDefBottomSheetFragment("defcountry")
            this@SettingFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)

        }

        binding.radioServersTxv.setSafeOnClickListener{
            val action = SettingFragmentDirections.actionFragmentSettingToChooseDefBottomSheetFragment("defserver")
            this@SettingFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        binding.moreappsTxvw.setSafeOnClickListener{
           moreApps()

        }

        binding.rateTxvw.setSafeOnClickListener{
            rate()

        }

        binding.removeadsTxvw.setSafeOnClickListener{
            removeAds()        }
    }




    fun themeChange(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    RadioFunction.switchColor(binding.saveData,it)

                    gradiancolorNestedScrollViewTransition(binding.containerSetting ,  4,it)
                    maintextviewColor(binding.themeTxvw,it)
                    maintextviewColor(binding.favCountryTxv,it)
                    maintextviewColor(binding.radioServersTxv,it)
                    maintextviewColor(binding.StaticsTxV,it)
                    maintextviewColor(binding.textView19,it)
                    maintextviewColor(binding.ContactUsTxV,it)
                    maintextviewColor(binding.LicencesTxtV,it)
                    maintextviewColor(binding.DisclaimerTxtV,it)
                    maintextviewColor(binding.radioServersTxv,it)
                    maintextviewColor(binding.batterisettings,it)
                    maintextviewColor(binding.removeadsTxvw,it)
                    maintextviewColor(binding.rateTxvw,it)
                    maintextviewColor(binding.moreappsTxvw,it)

                }

            }
        }

        //   PreferenceManager.getDefaultSharedPreferences(this@Activity_Setting).edit().putBoolean("Theme switecher state", isChecked).apply()
    }




    fun moreApps(){
        val uri = Uri.parse("https://play.google.com/store/apps/developer?id=AmiRami")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            (Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        )
        try
        {
            startActivity(goToMarket)
        }
        catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AmiRami")))
        }
    }
    fun removeAds(){
        val uri = Uri.parse("http://play.google.com/store/apps/details?id=com.amirami.simapp.radiobroadcastpro")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            (Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        )
        try
        {
            startActivity(goToMarket)
        }
        catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.amirami.simapp.radiobroadcastpro")))
        }
    }
    fun rate(){
        var uri = Uri.parse("market://details?id="  + requireContext().packageName)
        var goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            (Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        )
        try
        {
            startActivity(goToMarket)
        }
        catch (e: ActivityNotFoundException) {
            uri = Uri.parse("https://play.google.com/store/apps/details?id="  + requireContext().packageName)
            goToMarket = Intent(Intent.ACTION_VIEW, uri)
            startActivity(/*   Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName))*/
                goToMarket
            )
        }
    }







}
