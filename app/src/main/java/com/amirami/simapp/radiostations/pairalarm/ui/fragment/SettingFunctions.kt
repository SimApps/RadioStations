package com.amirami.simapp.radiostations.pairalarm.ui.fragment

import com.amirami.simapp.radiostations.pairalarm.model.SettingContents

interface SettingFunctions {

    val settingContents: SettingContents

 //   fun setQuickAlarmBell(key: String)
    fun setQuickAlarmMode(key: String)
    fun setQuickAlarmMute(key: String)
    fun openAppInfo()
}