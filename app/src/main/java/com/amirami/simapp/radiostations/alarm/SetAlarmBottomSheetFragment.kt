package com.amirami.simapp.radiostations.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.indexesOf
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.RadioFunction.shortformateDate
import com.amirami.simapp.radiostations.alarm.Utils.cancelAlarm
import com.amirami.simapp.radiostations.alarm.Utils.diableBootReceiver
import com.amirami.simapp.radiostations.alarm.Utils.enableBootReceiver
import com.amirami.simapp.radiostations.databinding.BottomsheetAddalarmBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SetAlarmBottomSheetFragment : BottomSheetDialogFragment(){

    private var hourPicker: NumberPicker? = null
    private var munitPicker: NumberPicker? = null
    private var timeInMilliSeconds: Long = 0
    private var hour = 20
    private var minute = 50

    private val infoViewModel: InfoViewModel by activityViewModels()

    private var _binding: BottomsheetAddalarmBinding? = null

    private val radioAlarmRoomViewModel: RadioAlarmRoomViewModel by activityViewModels()
    private val immutableFlag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0

    private lateinit var radioVariable:RadioVariables
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
            RadioFunction.gradiancolorConstraintLayoutTransitionBottomsheet(binding.popupcountdownalarm, 0,it)
            RadioFunction.maintextviewColor(binding.textView,it)
            RadioFunction.maintextviewColor(binding.textView2,it)
            RadioFunction.maintextviewColor(binding.buttonStartCancelalarm,it)


            RadioFunction.setNumberPickerTextColor(binding.hourPickers,it)
            RadioFunction.setNumberPickerTextColor(binding.minutPickers,it)

        }
        collectLatestLifecycleFlow(infoViewModel.putRadioAlarmInfo) {
            it.also { radioVariable = it }
        }


        getAlarmRadioRoom()







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

    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

private fun formatRcordDescriptionName(description:String):String{
  return  if ( description.contains("___",true)){

            description.substring(0,
                description.indexesOf("___", true)[0])  + " "+
                   shortformateDate(description.substring(description.indexesOf("___", true)[0] +3,
                        description.length
                    )) + ".mp3"
    }
    else {
        description
    }
}
    private fun viewsVisibility(radioAlarmEmpty: MutableList<RadioAlarmRoom>){
        if(radioAlarmEmpty.isEmpty()){
           /* _binding!!.textView.visibility = View.VISIBLE
            _binding!!.textView2.visibility = View.VISIBLE
            _binding!!.hourPickers.visibility = View.VISIBLE
            _binding!!.minutPickers.visibility = View.VISIBLE*/
            _binding!!.buttonStartCancelalarm.text = getString(R.string.Start_alarm)
            _binding!!.infoalarm.visibility = View.GONE
        }
        else {
            _binding!!.infoalarm.text =  getString(R.string.radioAlarmIsSetAT,
                shortformateDate(androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getLong("timeInMilli", 1)),
                if(radioAlarmEmpty[0].radiouid!="") getString(R.string.RadioStationName) else   getString(R.string.RecordedStationName),
                radioAlarmEmpty[0].name + if(radioAlarmEmpty[0].radiouid!="")  "" else formatRcordDescriptionName(radioAlarmEmpty[0].homepage))
            _binding!!.infoalarm.visibility = View.VISIBLE

            _binding!!.buttonStartCancelalarm.text = getString(R.string.cancel_alarm)
           /* _binding!!.textView.visibility = View.GONE
            _binding!!.textView2.visibility = View.GONE
            _binding!!.hourPickers.visibility = View.GONE
            _binding!!.minutPickers.visibility = View.GONE*/
        }
    }


    private fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }


    private fun getAlarmRadioRoom() {
       // radioAlarmRoomViewModel.deleteAll("")
        radioAlarmRoomViewModel.getAll().observe(this) { list ->
//errorToast(requireContext(),list.isEmpty().toString())

            viewsVisibility(list)
            _binding!!.buttonStartCancelalarm.setSafeOnClickListener {
                MainActivity.fromAlarm=false
                if(list.isEmpty()){
                    minute = munitPicker!!.value
                    hour = hourPicker!!.value


                    val calendar = Calendar.getInstance()

                    //    calendar.set(Calendar.DAY_OF_YEAR+1, hour/*hourOfDay*/)
                    calendar.set(Calendar.HOUR_OF_DAY, hour/*hourOfDay*/)
                    calendar.set(Calendar.MINUTE, minute/*minuteOfHour*/)
                    calendar.set(Calendar.SECOND, 0)


                   // val sdf = SimpleDateFormat("MMM d''yy HH:mm:ss", Locale.getDefault())
                    val sdf = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.getDefault())

                    // SimpleDateFormat("MMM d''yy", Locale.getDefault())
                    val formattedDate = sdf.format(calendar.time)
                    val date = sdf.parse(formattedDate)
                    timeInMilliSeconds = date!!.time


                    if (timeInMilliSeconds.toInt() != 0) {
                        val radioroom = RadioAlarmRoom(
                            radioVariable.stationuuid,
                            radioVariable.name,
                            radioVariable.bitrate,
                            radioVariable.homepage,
                            radioVariable.favicon,
                            radioVariable.tags,
                            radioVariable.country,
                            radioVariable.state,
                            //var RadiostateDB: String?,
                            radioVariable.language,
                            radioVariable.url_resolved,
                            radioVariable.moreinfo
                        )
                        radioAlarmRoomViewModel.upsertRadioAlarm(radioroom, "Radio added")

                        enableBootReceiver(requireContext())

                        Utils.setAlarm(requireContext(), timeInMilliSeconds)
                    }
                }
                else {
                    // Intent to start the Broadcast Receiver
                    val broadcastInten = Intent(requireContext(), AlarmReceiver::class.java)
                    // Utils.setAlarm(context, timeOfAlarm)
                    val pInten = PendingIntent.getBroadcast(
                        requireContext(),
                        Utils.requestalarmId,
                        broadcastInten,
                        immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val alarmMg = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (pInten != null) {
                        radioAlarmRoomViewModel.deleteAll("")
                        cancelAlarm(requireContext())
                        diableBootReceiver(requireContext())
                    }


                }

                viewsVisibility(list)
                RadioFunction.interatialadsShow(requireContext())
            }



        }

    }
}