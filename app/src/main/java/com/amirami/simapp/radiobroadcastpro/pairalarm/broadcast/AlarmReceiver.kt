package com.amirami.simapp.radiobroadcastpro.pairalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import com.amirami.simapp.radiobroadcastpro.BuildConfig

import com.amirami.simapp.radiobroadcastpro.pairalarm.service.AlarmForeground
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.ACTION_BUTTON
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.ALARM_CODE_TEXT
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NEXT_ALARM_WORKER
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION1
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION1_REQUEST_CODE
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION2
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION2_REQUEST_CODE
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION3
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION3_REQUEST_CODE
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.RECEIVER_ALARM_WORKER
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.SharedPreference
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.resetAllAlarms
import com.amirami.simapp.radiobroadcastpro.pairalarm.worker.NextAlarmWorker
import com.amirami.simapp.radiobroadcastpro.pairalarm.worker.ReceiverAlarmWorker

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
     //   Timber.d("Broadcast is called")
        val alarmCode = intent?.getStringExtra(ALARM_CODE_TEXT)
        val actionButtonCode = intent?.getStringExtra(ACTION_BUTTON)
        if (BuildConfig.DEBUG) {
            context?.let {
                val preference = SharedPreference(it)
                when {
                    alarmCode != null -> preference.putStringData(
                        preference.getStringData(),
                        alarmCode
                    )
                    intent?.action != "" -> preference.putStringData(
                        preference.getStringData(),
                        intent?.action
                    )
                    else -> preference.putStringData(
                        preference.getStringData(),
                        "no intent & alarmCode info"
                    )
                }
            }
        }

        if (intent != null && context != null) {
            when {
                // ** 휴대폰을 재부팅 했을 때 & 앱을 업데이트 했을 때-> 모든 알람을 재설정
                intent.action == "android.intent.action.BOOT_COMPLETED" ||
                        intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
                        intent.action == "android.intent.action.MY_PACKAGE_REPLACED" ||
                        intent.action == "android.intent.action.REBOOT" ||
                        intent.action == "android.intent.action.LOCKED_BOOT_COMPLETED"
                -> {
                 //   Timber.d("reset alarm")
                    // 서비스 재시작
                    val serviceIntent = Intent(context, AlarmForeground::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    }
                    resetAllAlarms(context)
                }

                // 내가 설정한 알람이 울렸을 때
                alarmCode != null -> {
                    val workData = workDataOf(ALARM_CODE_TEXT to alarmCode)
                    val receiverAlarmWorkRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<ReceiverAlarmWorker>()
                            .setInputData(workData)
                            .build()
                    WorkManager.getInstance(context).enqueueUniqueWork(
                        RECEIVER_ALARM_WORKER,
                        ExistingWorkPolicy.REPLACE,
                        receiverAlarmWorkRequest as OneTimeWorkRequest
                    )
                }

                // Notification에 있는 actionButton을 눌렀을 때
                actionButtonCode != null -> {
                    when (actionButtonCode) {
                        NOTI_ACTION1 -> {
                            doNextWorkAlarm(context, NOTI_ACTION1_REQUEST_CODE)
                        }
                        NOTI_ACTION2 -> {
                            doNextWorkAlarm(context, NOTI_ACTION2_REQUEST_CODE)
                        }
                        NOTI_ACTION3 -> {
                            doNextWorkAlarm(context, NOTI_ACTION3_REQUEST_CODE)
                        }
                    }
                }
            }
        }
    }

    private fun doNextWorkAlarm(context: Context?, dataValue: Int) {
        context?.let {
            val alarmTimeWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<NextAlarmWorker>()
                    .setInputData(Data.Builder().putInt(ACTION_BUTTON, dataValue).build())
                    .build()
            WorkManager.getInstance(it)
                .enqueueUniqueWork(
                    NEXT_ALARM_WORKER,
                    ExistingWorkPolicy.REPLACE,
                    alarmTimeWorkRequest as OneTimeWorkRequest
                )
        }
    }
}
