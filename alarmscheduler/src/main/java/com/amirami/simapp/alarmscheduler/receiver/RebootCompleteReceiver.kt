package com.amirami.simapp.alarmscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.alarmscheduler.logger.LogMessage
import com.amirami.simapp.alarmscheduler.logger.Logger
import com.amirami.simapp.alarmscheduler.service.RescheduleAlarmService


internal class RebootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.info(LogMessage.onRebootCompleteReceiverOnReceive())
        RescheduleAlarmService.startService(context)
    }
}