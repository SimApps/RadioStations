package com.amirami.simapp.radiobroadcast.pairalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amirami.simapp.radiobroadcast.R
import com.amirami.simapp.radiobroadcast.pairalarm.eventbus.EventBus
import com.amirami.simapp.radiobroadcast.pairalarm.eventbus.InitDataEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InitAlarmDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // TODO: 여기서 데이터 읽어오는 처리 필요함
        EventBus.post(InitDataEvent(Int.MAX_VALUE, applicationContext.getString(R.string.progressInit)))
        return Result.success()
    }
}
