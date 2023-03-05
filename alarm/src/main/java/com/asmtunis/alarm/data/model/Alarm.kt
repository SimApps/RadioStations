package com.asmtunis.alarm.data.model

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.asmtunis.alarm.broadCastReceiver.AlarmBroadCastReceiver
import com.asmtunis.alarm.utils.Constants.ALARM_OBJ
import com.asmtunis.alarm.utils.Constants.BUNDLE_ALARM_OBJ
import com.asmtunis.alarm.utils.TimePickerUtil
import com.asmtunis.alarm.utils.checkAboveLollipop
import com.asmtunis.alarm.utils.toast
import java.io.Serializable
import java.util.*

@Entity(tableName = "alarm_table")
data class Alarm(
    @PrimaryKey
    val alarmId: Int,
    var hour: Int,
    var minute: Int,
    var started: Boolean,
    val recurring: Boolean,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    var title: String,
    val tone: String,
    val vibrate: Boolean,

    ) : Serializable {


    fun schedule(context: Context) {




        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadCastReceiver::class.java)
        val bundle = Bundle().apply {
            this.putSerializable(ALARM_OBJ, this@Alarm)
        }
        intent.putExtra(BUNDLE_ALARM_OBJ, bundle)


        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // if alarm time is passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis()) {

            context.toast(
                "Alarm scheduled for tomorrow at: ${
                    TimePickerUtil.getFormattedTime(
                        hour,
                        minute
                    )
                }"
            )

            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        }

        if (!recurring) {

            context.toast("One time alarm set for ${TimePickerUtil.getFormattedTime(hour, minute)}")

            if (checkAboveLollipop()) {


                alarmManager.setAlarmClock(
                    AlarmClockInfo(
                        calendar.timeInMillis, PendingIntent.getActivity(
                            context, 0, Intent(
                                context,
                                MainActivity::class.java
                            ), 0
                        )
                    ),
                    pendingIntent
                )


            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            }


        } else {
            context.toast(
                "Recurring time alarm set for ${
                    TimePickerUtil.getFormattedTime(
                        hour,
                        minute
                    )
                }"
            )
            val RUN_DAILY: Long = 24 * 60 * 60 * 1000

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                RUN_DAILY,
                pendingIntent
            )

        }
        this.started = true
    }

    fun cancelAlarm(context: Context) {



        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadCastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0)
        alarmManager.cancel(pendingIntent)
        this.started = false
        context.toast("Alarm cancelded for ${TimePickerUtil.getFormattedTime(hour, minute)}")

    }

}