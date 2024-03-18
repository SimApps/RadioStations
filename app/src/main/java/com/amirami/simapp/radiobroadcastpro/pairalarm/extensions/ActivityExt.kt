package com.amirami.simapp.radiobroadcastpro.pairalarm.extensions

import android.app.KeyguardManager
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ListenableWorker
import com.amirami.simapp.radiobroadcastpro.R

import com.google.android.material.snackbar.Snackbar

// 현재 화면이 자동으로 꺼지지 않게 유지 & 잠금화면에 액티비티 띄우기
fun AppCompatActivity.displayOn() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).apply {
            requestDismissKeyguard(this@displayOn, null)
        }
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }
}

// 배경 화면을 클릭하면 현재 Focus되어있는거 클리어하기
fun AppCompatActivity.clearKeyBoardFocus(rootView: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)

    rootView.requestFocus()
}

// 에러 메시지 표시
fun AppCompatActivity.showErrorSnackBar(view: View, error: ListenableWorker.Result.Failure) {
    this.let { Snackbar.make(view, R.string.some_error, Snackbar.LENGTH_SHORT) }
        .also { it.show() }

}

fun AppCompatActivity.doShortVibrateOnce(){
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).run {
            defaultVibrator
        }
    } else {
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}