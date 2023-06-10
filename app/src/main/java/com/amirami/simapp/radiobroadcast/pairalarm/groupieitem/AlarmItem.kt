package com.amirami.simapp.radiobroadcast.pairalarm.groupieitem

import android.annotation.SuppressLint
import android.content.Context
import com.amirami.simapp.radiobroadcast.R
import com.amirami.simapp.radiobroadcast.databinding.AlarmItemBinding
import com.amirami.simapp.radiobroadcast.pairalarm.database.table.AlarmData
import com.amirami.simapp.radiobroadcast.pairalarm.extensions.lastClickTime
import com.amirami.simapp.radiobroadcast.pairalarm.extensions.setOnSingleClickListener
import com.amirami.simapp.radiobroadcast.pairalarm.ui.dialog.SimpleDialog
import com.amirami.simapp.radiobroadcast.pairalarm.viewModel.AlarmViewModel
import com.xwray.groupie.databinding.BindableItem

class AlarmItem(
    val context: Context,
    val alarmData: AlarmData,
    private val alarmViewModel: AlarmViewModel,
    private val listener: OnItemClickListener
) : BindableItem<AlarmItemBinding>(alarmData.hashCode().toLong()) {

    @SuppressLint("SetTextI18n")
    override fun bind(binding: AlarmItemBinding, position: Int) {
        binding.alarmData = alarmData

        binding.root.setOnSingleClickListener {
            openNormalAlarmActivity()
        }

        // 삭제 버튼 클릭
        binding.deleteImage.setOnSingleClickListener {
            SimpleDialog.showSimpleDialog(
                context,
                title = context.getString(R.string.dialog_delete_title),
                message = context.getString(R.string.dialog_delete_content),
                positive = {
                    alarmViewModel.deleteAlarmData(alarmData)
                }
            )
        }

        // on/off
        binding.onOffSwitch.isChecked = alarmData.alarmIsOn

        binding.onOffSwitch.setOnCheckedChangeListener { _, isChecked ->
            // 한 번만 클릭되는 기능을 넣지 않으면 혼자서 여러번 클릭됨
            if (lastClickTime < System.currentTimeMillis() - 500) {
                lastClickTime = System.currentTimeMillis()
                alarmData.alarmIsOn = isChecked
                alarmViewModel.updateAlarmData(alarmData)
               // Timber.d("update alarmData: $alarmData")
            }
        }
        binding.executePendingBindings()
    }

    private fun openNormalAlarmActivity() {
        listener.onItemClick(alarmData.alarmCode)
    }

    override fun getLayout(): Int {
        return R.layout.alarm_item
    }


    interface OnItemClickListener {
        fun onItemClick(alarmCode: String)
    }
}
