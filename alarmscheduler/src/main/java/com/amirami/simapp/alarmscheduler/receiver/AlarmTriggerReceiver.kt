package com.amirami.simapp.alarmscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.alarmscheduler.ServiceLocator
import com.amirami.simapp.alarmscheduler.applicationScope
import com.amirami.simapp.alarmscheduler.constant.Constant
import com.amirami.simapp.alarmscheduler.error.ErrorHandler
import com.amirami.simapp.alarmscheduler.error.ExceptionFactory
import com.amirami.simapp.alarmscheduler.extension.toMap
import com.amirami.simapp.alarmscheduler.logger.LogMessage
import com.amirami.simapp.alarmscheduler.logger.Logger
import com.amirami.simapp.alarmscheduler.model.DataPayload
import kotlinx.coroutines.launch

internal class AlarmTriggerReceiver : BroadcastReceiver() {

    private val alarmSchedulerImpl = ServiceLocator.provideAlarmSchedulerImpl()
    private val alarmStateDataSource = ServiceLocator.provideAlarmStateDataSource()

    override fun onReceive(context: Context, intent: Intent) {
        Logger.info(LogMessage.onBroadcastReceiverOnReceiveInvoked(this))

        val alarmType = intent.getIntExtra(Constant.ALARM_TYPE, Constant.VALUE_NOT_ASSIGN)
        val alarmId = intent.getIntExtra(Constant.ALARM_ID, Constant.VALUE_NOT_ASSIGN)
        val bundle = intent.getBundleExtra(Constant.ALARM_CUSTOM_DATA)
        Logger.info(LogMessage.onAlarmTriggerReceiverOnReceive(alarmType, alarmId, bundle))

        if (alarmType == Constant.VALUE_NOT_ASSIGN || alarmId == Constant.VALUE_NOT_ASSIGN) {
            return
        }
        val alarmTaskFactory = alarmSchedulerImpl.getAlarmTaskFactory()
        if (alarmTaskFactory == null) {
            ErrorHandler.handleError(ExceptionFactory.nullAlarmTaskFactory())
            return
        }

        Logger.info(LogMessage.onCreateAlarmTask(alarmType, alarmId))
        try {
            val alarmTask = alarmTaskFactory.createAlarmTask(alarmType)
            alarmTask.onAlarmFires(context,alarmId, DataPayload(bundle.toMap()))
        } catch (throwable: Throwable) {
            ErrorHandler.handleError(ExceptionFactory.failedToCreateAlarmTask(throwable))
        }
        applicationScope.launch {
            try {
                alarmStateDataSource.removeAlarm(alarmId)
            } catch (exception: Throwable) {
                ErrorHandler.handleError(ExceptionFactory.failedToRemoveAlarmState(exception))
            }
        }
    }
}