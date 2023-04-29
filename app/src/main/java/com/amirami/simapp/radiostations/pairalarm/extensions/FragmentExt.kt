package com.amirami.simapp.radiostations.pairalarm.extensions

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.pairalarm.model.Failure
import com.amirami.simapp.radiostations.pairalarm.util.DENIED
import com.amirami.simapp.radiostations.pairalarm.util.EXPLAINED
import com.google.android.material.snackbar.Snackbar

fun Fragment.getPermissionActivityResultLauncher(
    allGranted: () -> Unit,
    notGranted: () -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // 단순히 권한이 거부 되었을 때
                }
                map[EXPLAINED]?.let {
                    // 권한 요청이 완전히 막혔을 때(주로 앱 상세 창 열기)
                    notGranted()
                }
            }
            else -> {
                // 모든 권한이 허가 되었을 때
                allGranted()
            }
        }
    }
}

fun Fragment.showErrorSnackBar(error: Failure) {
    view?.let { Snackbar.make(it, R.string.some_error, Snackbar.LENGTH_SHORT) }
        .also { it?.show() }
    //Timber.e("error: ${error.error.message}")
}


fun Fragment.displayOn() {
    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        requireActivity().setShowWhenLocked(true)
        requireActivity().setTurnScreenOn(true)
        (requireActivity().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).apply {
            requestDismissKeyguard(requireActivity(), null)
        }
    } else {
        requireActivity().window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }
}

// 배경 화면을 클릭하면 현재 Focus되어있는거 클리어하기
fun Fragment.clearKeyBoardFocus(rootView: View) {
    val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)

    rootView.requestFocus()
}

// 에러 메시지 표시
fun Fragment.showErrorSnackBar(view: View, error: Failure) {
    this.let { Snackbar.make(view, R.string.some_error, Snackbar.LENGTH_SHORT) }
        .also { it.show() }
    //Timber.e("error: ${error.error.message}")
}

fun Fragment.doShortVibrateOnce(){
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).run {
            defaultVibrator
        }
    } else {
        requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}