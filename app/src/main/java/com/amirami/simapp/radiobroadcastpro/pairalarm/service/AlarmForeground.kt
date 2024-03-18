package com.amirami.simapp.radiobroadcastpro.pairalarm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.ALARM_NOTI_ID
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NEXT_ALARM_NOTIFICATION_TEXT
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.getNotificationBuilder

/**
 * 포그라운드 서비스가 브로드캐스팅이 강제로 취소되는 것을 막아줌
 * */
class AlarmForeground : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      //  Timber.d("Service activated")

        startForeground(
            ALARM_NOTI_ID,
            getNotificationBuilder(
                this,
                intent?.getStringExtra(NEXT_ALARM_NOTIFICATION_TEXT) ?: ""
            ).build()
        )

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
      //  Timber.d("Service die")
    }
}