package com.amirami.simapp.alarmscheduler.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.alarmscheduler.event.EventDispatcher
import com.amirami.simapp.alarmscheduler.event.ScheduleExactAlarmPermissionGrantedEvent
import com.amirami.simapp.alarmscheduler.logger.LogMessage
import com.amirami.simapp.alarmscheduler.logger.Logger
import com.amirami.simapp.alarmscheduler.service.RescheduleAlarmService

internal class ScheduleExactAlarmPermissionGrantedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Logger.info(LogMessage.onBroadcastReceiverOnReceiveInvoked(this))
        if (intent.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) {
            RescheduleAlarmService.startService(context)
            EventDispatcher.dispatchEvent(ScheduleExactAlarmPermissionGrantedEvent)
        }
    }
}