package com.amirami.simapp.radiobroadcastpro.pairalarm.worker

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.RadioFunction.errorToast
import com.amirami.simapp.radiobroadcastpro.hiltcontainer.RadioApplication.Companion.dataStore
import com.amirami.simapp.radiobroadcastpro.pairalarm.database.table.AlarmData
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.AlarmMode
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.AlarmVibrationOption
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.SettingContents
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.getModeIndex
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.getVibrationIndex
import com.amirami.simapp.radiobroadcastpro.pairalarm.repository.AlarmRepository
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.ACTION_BUTTON
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION1_REQUEST_CODE
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION2_REQUEST_CODE
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NOTI_ACTION3_REQUEST_CODE
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.cancelAlarmNotification
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.getAlarmDataFromTimeMillis
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.getNextAlarm
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.makeAlarmNotification
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.resetAllAlarms
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

/**
 * 새롭게 알람을 추가하고 DB에서 다음 알림을 찾고 바로 등록하거나
 * 그냥 DB에서 다음 알림을 찾고 바로 등록한다
 * */
@HiltWorker
class NextAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmRepository: AlarmRepository,
) : CoroutineWorker(appContext, workerParams) {
    private var quickAlarmModeIndex: Int = 0
    private var quickAlarmVibrationIndex: Int = 0
    override suspend fun doWork(): Result {
        val actionButtonPosition = inputData.getInt(ACTION_BUTTON, 0)
        if (actionButtonPosition != 0) {
            applicationContext.dataStore.data.catch { exception ->
             //   Timber.e(exception.message)
                errorToast(
                    applicationContext,
                    applicationContext.getString(R.string.toast_get_dataStore_error)
                )
            }.first().let {
                quickAlarmModeIndex =
                    it[stringPreferencesKey(SettingContents.QUICKALARM_MODE.title)]?.getModeIndex()
                        ?: AlarmMode.NORMAL.mode.getModeIndex()
                quickAlarmVibrationIndex =
                    it[stringPreferencesKey(SettingContents.QUICKALARM_MUTE.title)]?.getVibrationIndex()
                        ?: AlarmVibrationOption.SOUND.vibrationOptionName.getVibrationIndex()
            }

            when (actionButtonPosition) {
                NOTI_ACTION1_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION1_REQUEST_CODE)
                }
                NOTI_ACTION2_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION2_REQUEST_CODE)
                }
                NOTI_ACTION3_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION3_REQUEST_CODE)
                }
            }
            resetAlarmNotification()
        } else {
            resetAlarmNotification()
        }
        return Result.success()
    }

    private suspend fun makeQuickAlarm(notiActionRequestCode: Int) {
        makeAlarmDataForQuickAlarm(notiActionRequestCode)?.let {
           // Timber.d("makeQuickAlarm called")
            alarmRepository.insertAlarmData(it)
        }
    }

    private fun makeAlarmDataForQuickAlarm(notiActionRequestCode: Int): AlarmData? {
        return when (notiActionRequestCode) {
            NOTI_ACTION1_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(
                    5 * 60 * 1000,
                    quickAlarmModeIndex,
                    quickAlarmVibrationIndex
                )
            }
            NOTI_ACTION2_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(
                    15 * 60 * 1000,
                    quickAlarmModeIndex,
                    quickAlarmVibrationIndex
                )
            }
            NOTI_ACTION3_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(
                    30 * 60 * 1000,
                    quickAlarmModeIndex,
                    quickAlarmVibrationIndex
                )
            }
            else -> null
        }
    }

    private suspend fun resetAlarmNotification() {
        alarmRepository.getAllAlarm().first().let { alarmDataList ->
            val transformedNextAlarm = getNextAlarm(alarmDataList)
            if (transformedNextAlarm.isNullOrEmpty()) {
                cancelAlarmNotification(applicationContext)
            } else {
              //  Timber.d("resetAlarmNotification called")
                makeAlarmNotification(applicationContext, transformedNextAlarm.toString())
            }
            // 모든 알람의 브로드캐스트를 새롭게 지정
            resetAllAlarms(applicationContext, alarmDataList)
        }
    }
}
