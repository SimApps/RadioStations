package com.amirami.simapp.radiostations.pairalarm.groupieitem

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.databinding.SettingItemBinding
import com.amirami.simapp.radiostations.pairalarm.dataStore.DataStoreTool
import com.amirami.simapp.radiostations.pairalarm.extensions.setOnSingleClickListener
import com.amirami.simapp.radiostations.pairalarm.model.AlarmMode
import com.amirami.simapp.radiostations.pairalarm.model.AlarmVibrationOption
import com.amirami.simapp.radiostations.pairalarm.model.SettingContentType
import com.amirami.simapp.radiostations.pairalarm.model.SettingContents
import com.amirami.simapp.radiostations.pairalarm.model.getBellIndex
import com.amirami.simapp.radiostations.pairalarm.model.getBellName
import com.amirami.simapp.radiostations.pairalarm.model.getModeIndex
import com.amirami.simapp.radiostations.pairalarm.ui.dialog.SimpleDialog
import com.amirami.simapp.radiostations.pairalarm.ui.fragment.SettingFunctions
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingContentItem(
    override val context: Context,
    override val settingContents: SettingContents,
    private val settingContentType: SettingContentType?,
    override val coroutineContext: CoroutineContext,
    override val job: Job,
) : BindableItem<SettingItemBinding>(settingContents.hashCode().toLong()), DataStoreTool,
    SettingFunctions {
    private lateinit var settingDetail: String

    override fun bind(binding: SettingItemBinding, position: Int) {
        binding.title = settingContents.title
        // 레이아웃의 배경 셋팅
        when (settingContentType) {
            SettingContentType.SINGLE -> {

                binding.isLastItem = true
            }
            SettingContentType.FIRST -> {

            }
            SettingContentType.LAST -> {

                binding.isLastItem = true
            }
            else -> {

            }
        }

        launch {
            getStoredStringDataWithFlow(settingContents.title).collectLatest {
                binding.settingDetail = it
                settingDetail = it
            }
        }

        binding.root.setOnSingleClickListener {
            when (settingContents) {
              /*  SettingContents.QUICKALARM_BELL -> {
                    setQuickAlarmBell(settingContents.title)
                }*/
                SettingContents.QUICKALARM_MODE -> {
                    setQuickAlarmMode(settingContents.title)
                }
                SettingContents.QUICKALARM_MUTE -> {
                    setQuickAlarmMute(settingContents.title)
                }

                else -> {

                }
            }
        }
    }

    override fun onViewDetachedFromWindow(viewHolder: GroupieViewHolder<SettingItemBinding>) {
        super.onViewDetachedFromWindow(viewHolder)
        job.cancel()
    }



    override fun setQuickAlarmMode(key: String) {
        SimpleDialog.showAlarmModeDialog(
            context,
            clickedItemPosition = settingDetail.getModeIndex(),
            positive = { dialogInterface ->
                launch {
                    val alert = dialogInterface as AlertDialog
                    when (alert.listView.checkedItemPosition) {
                        // Normal 클릭 시
                        0 -> {
                            saveStringData(key, AlarmMode.NORMAL.mode)
                        }
                        // Calculate 클릭 시
                        1 -> {
                            saveStringData(key, AlarmMode.CALCULATION.mode)
                        }
                    }
                }
            }
        )
    }

    override fun setQuickAlarmMute(key: String) {
        launch {
            // 클릭할 때 마다 다음 모드가 저장되게 한다.
            when(settingDetail) {
                AlarmVibrationOption.Vibration.vibrationOptionName -> {
                    saveStringData(key, AlarmVibrationOption.ONCE.vibrationOptionName)
                }
                AlarmVibrationOption.ONCE.vibrationOptionName -> {
                    saveStringData(key, AlarmVibrationOption.SOUND.vibrationOptionName)
                }
                AlarmVibrationOption.SOUND.vibrationOptionName -> {
                    saveStringData(key, AlarmVibrationOption.Vibration.vibrationOptionName)
                }
                else -> {
                    saveStringData(key, AlarmVibrationOption.Vibration.vibrationOptionName)
                }
            }
        }
    }

    override fun openAppInfo() {

    }



    override fun getLayout(): Int = R.layout.setting_item
}