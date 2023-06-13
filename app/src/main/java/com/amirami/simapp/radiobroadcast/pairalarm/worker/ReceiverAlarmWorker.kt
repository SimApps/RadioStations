package com.amirami.simapp.radiobroadcast.pairalarm.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amirami.simapp.radiobroadcast.pairalarm.database.dao.AlarmDAO
import com.amirami.simapp.radiobroadcast.pairalarm.ui.fragment.OnAlarmActivity
import com.amirami.simapp.radiobroadcast.pairalarm.util.ALARM_CODE_TEXT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * 알람이 울렸을 경우 해당 알람의 정보를 습득하고 지금 울릴 알람이 맞는지 확인한다
 * */
@HiltWorker
class ReceiverAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmDao: AlarmDAO,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val alarmCode = inputData.getString(ALARM_CODE_TEXT)
        //Timber.d("alarmCode: $alarmCode")
        if (alarmCode != null) {
            val targetAlarmData = alarmDao.searchAlarmDataWithAlarmCode(alarmCode.toString())
            targetAlarmData.first().let { alarmData ->
             //   Timber.d("Called alarm data: $alarmData")
                val todayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
               // Timber.d("today week: $todayWeek")

                when (todayWeek) {
                    1 -> {
                        if (alarmData.Sun && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    2 -> {
                        if (alarmData.Mon && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    3 -> {
                        if (alarmData.Tue && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    4 -> {
                        if (alarmData.Wed && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    5 -> {
                        if (alarmData.Thu && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    6 -> {
                        if (alarmData.Fri && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    7 -> {
                        if (alarmData.Sat && alarmData.alarmIsOn) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                }
            }
        }
        return Result.success()
    }
}

fun openOnAlarmActivity(context: Context, alarmCode: String) {

    val onAlarmActivity = Intent(context, OnAlarmActivity::class.java)
    onAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    onAlarmActivity.putExtra(ALARM_CODE_TEXT, alarmCode)
    context.startActivity(onAlarmActivity)
}
