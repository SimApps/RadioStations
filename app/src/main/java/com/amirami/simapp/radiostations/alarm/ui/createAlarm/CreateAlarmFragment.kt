package com.amirami.simapp.radiostations.alarm.ui.createAlarm

import android.app.Activity
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.amirami.simapp.radiostations.alarm.data.model.Alarm
import com.amirami.simapp.radiostations.alarm.ui.activities.CommonViewModel
import com.amirami.simapp.radiostations.alarm.utils.TimePickerUtil
import com.amirami.simapp.radiostations.alarm.utils.gone
import com.amirami.simapp.radiostations.alarm.utils.toast
import com.amirami.simapp.radiostations.alarm.utils.visible
import com.amirami.simapp.radiostations.databinding.FragmentCreateAlarmBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.random.Random


private const val ARG_ALARM = "alarm"

@AndroidEntryPoint
class CreateAlarmFragment : BottomSheetDialogFragment() {

    private var alarm: Alarm? = null
    lateinit var binding: FragmentCreateAlarmBinding
    private var hour: Int = 0
    private var minute: Int = 0

    private var isVibrate: Boolean = false
    lateinit var tone: String
    lateinit var ringtone: Ringtone

    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            alarm = it.getSerializable(ARG_ALARM) as Alarm
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateAlarmBinding.inflate(layoutInflater, container, false)
        tone = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
            .toString()
        ringtone = RingtoneManager.getRingtone(context, Uri.parse(tone))

        setData()
        setClickListeners()

        return binding.root
    }

    private fun setClickListeners() {

        binding.fragmentCreatealarmRecurring.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.fragmentCreatealarmRecurringOptions.visible()
            } else {
                binding.fragmentCreatealarmRecurringOptions.gone()
            }
        }




        binding.fragmentCreatealarmScheduleAlarm.setOnClickListener {
            if (alarm != null) {
            //    context?.toast("1")
               updateAlarm()
            } else {
             //   context?.toast("2")
                 scheduleAlarm()
            }

            commonViewModel.getAllAlarm()
             findNavController().navigateUp()
        }

        binding.fragmentCreatealarmCardSound.setOnClickListener {
            Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alert sound for alarm")
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone))
                startActivityForResult(this, 5)
            }
        }

        binding.itemAlarmSound.setOnClickListener {
            Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alert sound for alarm")
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone))
                startActivityForResult(this, 5)
            }
        }

        binding.fragmentCreatealarmVibrateSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isVibrate = isChecked
        }
        binding.fragmentCreatealarmCancelAlarm.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.timePickerButton.setOnClickListener {
            if (alarm != null)
                timePickerDialog(alarm!!.hour, alarm!!.minute)
            else
                timePickerDialog(null, null)
        }

    }


    private fun timePickerDialog(h: Int?, m: Int?) {

        val hr = h ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val mi = m ?: Calendar.getInstance().get(Calendar.MINUTE)


        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hr)
            .setMinute(mi)
            .setTitleText("Select time")
            .build();
        timePickerDialog.show(requireActivity().supportFragmentManager, "fragment_tag");
        timePickerDialog.addOnPositiveButtonClickListener {

            hour = timePickerDialog.hour
            minute = timePickerDialog.minute

            binding.timePickerButton.text = TimePickerUtil.getFormattedTime(hour, minute)
        }
        timePickerDialog.addOnNegativeButtonClickListener {
            hour = timePickerDialog.hour
            minute = timePickerDialog.minute
            binding.timePickerButton.text = TimePickerUtil.getFormattedTime(hour, minute)
        }


    }


    private fun scheduleAlarm() {

        val alarmTitle: String = (binding.alarmTitle.text).toString()
        val alarmId = Random(System.currentTimeMillis()).nextInt(Int.MAX_VALUE)
        val alarm = Alarm(
            alarmId = alarmId,
            hour = hour,
            minute = minute,
            started = true,
            recurring = binding.fragmentCreatealarmRecurring.isChecked,
            monday = binding.fragmentCreatealarmCheckMon.isChecked,
            tuesday = binding.fragmentCreatealarmCheckTue.isChecked,
            wednesday = binding.fragmentCreatealarmCheckWed.isChecked,
            thursday = binding.fragmentCreatealarmCheckThu.isChecked,
            friday = binding.fragmentCreatealarmCheckFri.isChecked,
            saturday = binding.fragmentCreatealarmCheckSat.isChecked,
            sunday = binding.fragmentCreatealarmCheckSun.isChecked,
            title = alarmTitle,
            tone = tone,
            vibrate = isVibrate
        )

         commonViewModel.insertAlarm(alarm)
        context?.let { alarm.schedule(it) }

    }

    private fun updateAlarm() {


        val alarmTitle: String = (binding.alarmTitle.text).toString()
        val updateAlarm = Alarm(
            alarmId = alarm?.alarmId!!,
            hour = hour,
            minute = minute,
            started = true,
            recurring = binding.fragmentCreatealarmRecurring.isChecked,
            monday = binding.fragmentCreatealarmCheckMon.isChecked,
            tuesday = binding.fragmentCreatealarmCheckTue.isChecked,
            wednesday = binding.fragmentCreatealarmCheckWed.isChecked,
            thursday = binding.fragmentCreatealarmCheckThu.isChecked,
            friday = binding.fragmentCreatealarmCheckFri.isChecked,
            saturday = binding.fragmentCreatealarmCheckSat.isChecked,
            sunday = binding.fragmentCreatealarmCheckSun.isChecked,
            title = alarmTitle,
            tone = tone,
            vibrate = isVibrate
        )


         commonViewModel.update(updateAlarm)
        context?.let { updateAlarm.schedule(it) }

    }

    private fun setData() {


        binding.fragmentCreatealarmSetToneName.text = ringtone.getTitle(context)

        binding.timePickerButton.text = TimePickerUtil.getFormattedTime(
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE)
        )

        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        minute = Calendar.getInstance().get(Calendar.MINUTE)


        if (alarm != null) {
            updateAlarmInfo(alarm!!)
        }


    }

    private fun updateAlarmInfo(alarm: Alarm) {

        binding.timePickerButton.text = TimePickerUtil.getFormattedTime(
            alarm.hour,
            alarm.minute
        )
        hour = alarm.hour
        minute = alarm.minute
        if (alarm.recurring) {
            binding.fragmentCreatealarmRecurring.isChecked = true
            binding.fragmentCreatealarmRecurringOptions.visible()

            if (alarm.monday) {
                binding.fragmentCreatealarmCheckMon.isChecked = true
            }
            if (alarm.tuesday) {
                binding.fragmentCreatealarmCheckTue.isChecked = true
            }
            if (alarm.wednesday) {
                binding.fragmentCreatealarmCheckWed.isChecked = true
            }
            if (alarm.thursday) {
                binding.fragmentCreatealarmCheckThu.isChecked = true
            }
            if (alarm.friday) {
                binding.fragmentCreatealarmCheckFri.isChecked = true
            }
            if (alarm.saturday) {
                binding.fragmentCreatealarmCheckSat.isChecked = true
            }
            if (alarm.sunday) {
                binding.fragmentCreatealarmCheckSun.isChecked = true
            }
        }
        tone = alarm.tone

        ringtone = RingtoneManager.getRingtone(context, Uri.parse(tone))

        binding.fragmentCreatealarmSetToneName.text = ringtone.getTitle(context)
        if (alarm.vibrate)
            binding.fragmentCreatealarmVibrateSwitch.isChecked = true
        binding.alarmTitle.setText(alarm.title)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            ringtone = RingtoneManager.getRingtone(context, uri)
            val title = ringtone.getTitle(context)
            if (uri != null) {
                tone = uri.toString()

                if (!title.isNullOrEmpty()) {
                    binding.fragmentCreatealarmSetToneName.text = title
                }
            } else {
                binding.fragmentCreatealarmSetToneName.text = ""
            }
        }
    }

}