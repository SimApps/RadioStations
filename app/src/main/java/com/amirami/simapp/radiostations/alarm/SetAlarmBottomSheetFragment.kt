package com.amirami.simapp.radiostations.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.alarm.Utils.cancelAlarm
import com.amirami.simapp.radiostations.databinding.BottomsheetAddalarmBinding
import com.amirami.simapp.radiostations.ui.SetTimerBottomSheetFragment
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SetAlarmBottomSheetFragment : BottomSheetDialogFragment(){

    var hourPicker: NumberPicker? = null
    var munitPicker: NumberPicker? = null
    var timeInMilliSeconds: Long = 0
    var hour = 20
    var minute = 50

    private val infoViewModel: InfoViewModel by activityViewModels()

    private var _binding: BottomsheetAddalarmBinding? = null

    val argsFrom: SetAlarmBottomSheetFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddalarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorLinearlayoutTransition(binding.popupcountdownalarm, 0,it)
            RadioFunction.maintextviewColor(binding.textView,it)
            RadioFunction.maintextviewColor(binding.textView2,it)
            RadioFunction.maintextviewColor(binding.buttonStartCancelalarm,it)


            RadioFunction.setNumberPickerTextColor(binding.hourPickers,it)
            RadioFunction.setNumberPickerTextColor(binding.minutPickers,it)

        }




        viewsVisibility()




        val rightNow = Calendar.getInstance()
        //  hour =  rightNow.get(Calendar.HOUR_OF_DAY) //(System.currentTimeMillis().toInt() / (1000 * 60 * 60) % 24)//androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("hour", 8)
        //  minute = rightNow.get(Calendar.MINUTE)//(System.currentTimeMillis().toInt() / (1000 * 60) % 60)// androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("minute", 30)


        hourPicker = _binding!!.hourPickers
        hourPicker!!.minValue = 0
        hourPicker!!.maxValue = 23
        hourPicker!!.value =  rightNow.get(Calendar.HOUR_OF_DAY)
        hourPicker!!.wrapSelectorWheel = true
        // hourPicker!!.setOnValueChangedListener(this)

        munitPicker = _binding!!.minutPickers
        munitPicker!!.minValue = 0
        munitPicker!!.maxValue = 59
        munitPicker!!.value = rightNow.get(Calendar.MINUTE)
        munitPicker!!.wrapSelectorWheel = true

        _binding!!.buttonStartCancelalarm.setSafeOnClickListener {
            if(androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("radioURL", "Empty")
                =="Empty"){
                minute = munitPicker!!.value
                hour = hourPicker!!.value
            //    androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putInt("hour", hour).apply()
            //    androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putInt("minute", minute).apply()


                val calendar = Calendar.getInstance()

            //    calendar.set(Calendar.DAY_OF_YEAR+1, hour/*hourOfDay*/)
                calendar.set(Calendar.HOUR_OF_DAY, hour/*hourOfDay*/)
                calendar.set(Calendar.MINUTE, minute/*minuteOfHour*/)
                calendar.set(Calendar.SECOND, 0)


                val sdf = SimpleDateFormat("MMM d''yy HH:mm:ss", Locale.getDefault())// SimpleDateFormat("MMM d''yy", Locale.getDefault()) SimpleDateFormat("dd-MM'-yyyy HH:mm:ss", Locale.getDefault())
                val formattedDate = sdf.format(calendar.time)
                val date = sdf.parse(formattedDate)
                timeInMilliSeconds = date!!.time


                //setAlarm
                if (timeInMilliSeconds.toInt() != 0) {
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .edit()
                        .putString("radioURL", argsFrom.stationurl).apply()

                    enableBootReceiver()

                    Utils.setAlarm(requireContext(), timeInMilliSeconds)
                }
              //  DynamicToast.makeSuccess(requireContext()," Alarm is set at : $hour : $minute",9).show()

                _binding!!.infoalarm.text = " Alarm is set at : ${shortformateDate(timeInMilliSeconds)}"


            }
            else {
                // Intent to start the Broadcast Receiver
                val broadcastInten = Intent(requireContext(), AlarmReceiver::class.java)
                // Utils.setAlarm(context, timeOfAlarm)
                val pInten = PendingIntent.getBroadcast(
                    requireContext(),
                    Utils.requestalarmId, broadcastInten,/* PendingIntent.FLAG_NO_CREATE*/0
                )
                val alarmMg = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (pInten != null) {
                    cancelAlarm(requireContext())
                   diableBootReceiver()
                }


            }

            viewsVisibility()

            RadioFunction.interatial_ads_show(requireContext())
        }
    }

    private fun enableBootReceiver() {
        val receiver = ComponentName(requireContext(), BootCompleteReceiver::class.java)

        requireContext().packageManager?.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun diableBootReceiver() {
        val receiver = ComponentName(requireContext(), BootCompleteReceiver::class.java)

        requireContext().packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


    private fun viewsVisibility(){
        if(androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("radioURL", "Empty")=="Empty"){
           /* _binding!!.textView.visibility = View.VISIBLE
            _binding!!.textView2.visibility = View.VISIBLE
            _binding!!.hourPickers.visibility = View.VISIBLE
            _binding!!.minutPickers.visibility = View.VISIBLE*/
            _binding!!.buttonStartCancelalarm.text = getString(R.string.Start_alarm)
            _binding!!.infoalarm.visibility = View.GONE
        }
        else {
            _binding!!.infoalarm.text = " Alarm is set at : ${shortformateDate(androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getLong("timeInMilli", 1))}"
            _binding!!.infoalarm.visibility = View.VISIBLE

            _binding!!.buttonStartCancelalarm.text = getString(R.string.cancel_alarm)
           /* _binding!!.textView.visibility = View.GONE
            _binding!!.textView2.visibility = View.GONE
            _binding!!.hourPickers.visibility = View.GONE
            _binding!!.minutPickers.visibility = View.GONE*/
        }
    }

    private fun shortformateDate(Date:Long):String{
        // SimpleDateFormat("d/MM/yyyy", Locale.getDefault()).format(Date())
        // SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

        return SimpleDateFormat("MMM d''yy HH:mm", Locale.getDefault()).format(Date)

    }
    fun <T> SetAlarmBottomSheetFragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }

}