package com.amirami.simapp.radiobroadcast.pairalarm.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.amirami.simapp.radiobroadcast.R
import com.amirami.simapp.radiobroadcast.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcast.RadioFunction.warningToast
import com.amirami.simapp.radiobroadcast.pairalarm.extensions.clearKeyBoardFocus
import com.amirami.simapp.radiobroadcast.pairalarm.extensions.setOnSingleClickListener
import com.amirami.simapp.radiobroadcast.pairalarm.ui.dialog.SimpleDialog
import com.amirami.simapp.radiobroadcast.pairalarm.util.AlarmAnimation
import com.amirami.simapp.radiobroadcast.pairalarm.util.getAddedTime
import com.amirami.simapp.radiobroadcast.pairalarm.util.makeAlarmData
import com.amirami.simapp.radiobroadcast.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.amirami.simapp.radiobroadcast.databinding.FargmentSimpleAlarmSetBinding
import com.amirami.simapp.radiobroadcast.viewmodel.InfoViewModel

@UnstableApi @AndroidEntryPoint
class SimpleAlarmSetFragment : Fragment(R.layout.fargment_simple_alarm_set) {
    private lateinit var binding: FargmentSimpleAlarmSetBinding
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val infoViewModel: InfoViewModel by activityViewModels()




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FargmentSimpleAlarmSetBinding>(view)?.let { binding = it } ?: return
        binding.lifecycleOwner = this



        // UI 초기화
        lifecycleScope.launch {
            launch {
                alarmViewModel.getAlarmData("Empty").collectLatest {
                    binding.alarmData = it
                    alarmViewModel.currentAlarmBell.value = it.bell
                    alarmViewModel.currentAlarmMode.value = it.mode
                   // Timber.d("selected alarmData: $it")
                }
            }
            launch {
                // 시간
                alarmViewModel.currentAlarmHour.collectLatest {
                    if (it >= 10) {
                        binding.plusHourText.setText(it.toString())
                    } else {
                        binding.plusHourText.setText("0$it")
                    }
                }
            }
            launch {
                // 분
                alarmViewModel.currentAlarmMin.collectLatest {
                    if (it >= 60) {
                        alarmViewModel.currentAlarmMin.value -= 60
                        alarmViewModel.currentAlarmHour.value += 1
                    }
                    if (it >= 10) {
                        binding.plusMinText.setText(it.toString())
                    } else {
                        binding.plusMinText.setText("0$it")
                    }
                }
            }
            launch {
                // bellDialog에서 변경한 bellIndex를 갱신한다
                alarmViewModel.currentAlarmBell.collectLatest { bellIndex ->
                    if (binding.alarmData != null) {
                        binding.alarmData = binding.alarmData?.copy(bell = bellIndex)
                    }
                }
            }
        }



        binding.resetCounter.setOnSingleClickListener {
            alarmViewModel.currentAlarmMin.value = 0
            alarmViewModel.currentAlarmHour.value = 0
        }

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            clearKeyBoardFocus(binding.rootLayout)
        }

        // 분 +에 대한 조작
        binding.min60Button.setOnClickListener {
            alarmViewModel.currentAlarmHour.value += 1
        }
        binding.min30Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 30
        }
        binding.min15Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 15
        }
        binding.min10Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 10
        }
        binding.min5Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 5
        }
        binding.min1Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 1
        }

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                binding.alarmData = seekBar?.progress?.let { binding.alarmData!!.copy(volume = it) }
            }
        })

        // 모드 버튼을 눌렀을 때
        binding.selectModeButton.setOnSingleClickListener {
            // ** 항목 선택 Dialog 설정
            SimpleDialog.showAlarmModeDialog(requireContext(),
                clickedItemPosition = alarmViewModel.currentAlarmMode.value,
                positive = { dialogInterface ->
                    val alert = dialogInterface as AlertDialog
                    // 선택된 아이템의 position에 따라 행동 조건 넣기
                    when (alert.listView.checkedItemPosition) {
                        // Normal 클릭 시
                        0 -> {
                            alarmViewModel.currentAlarmMode.value = 0
                        }
                        // Calculate 클릭 시
                        1 -> {
                            alarmViewModel.currentAlarmMode.value = 1
                        }
                    }
                    binding.alarmData =
                        binding.alarmData?.copy(mode = alarmViewModel.currentAlarmMode.value)
                })
        }



        // 진동 버튼을 눌렀을 때
        binding.imageVibration.apply {
            setOnClickListener {
                when (binding.alarmData!!.vibration) {
                    0 -> {
                        binding.alarmData = binding.alarmData!!.copy(vibration = 1)
                    }
                    1 -> {
                        binding.alarmData = binding.alarmData!!.copy(vibration = 2)
                        AlarmAnimation.swing(this).start()
                    }
                    2 -> {
                        binding.alarmData = binding.alarmData!!.copy(vibration = 0)
                    }
                }
            }
        }

        // 볼륨 이미지를 클릭했을 때
        binding.imageVolume.setOnClickListener {
            if (binding.alarmData!!.volume > 0) {
                binding.alarmData = binding.alarmData!!.copy(volume = 0)
            } else {
                binding.alarmData = binding.alarmData!!.copy(volume = 100)
            }
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.putRadioInfo) { radioVar ->

            binding.radioNameNormalTxt.text = radioVar.name
            binding.saveButton.setOnSingleClickListener {

                if (alarmViewModel.currentAlarmHour.value > 0 || alarmViewModel.currentAlarmMin.value > 0) {
                    val dateData = getAddedTime(
                        hour = alarmViewModel.currentAlarmHour.value,
                        min = alarmViewModel.currentAlarmMin.value
                    )
                    val alarmDataa = binding.alarmData!!.copy(
                           radio = radioVar
                    )
                    val alarmData = makeAlarmData(
                        calendar = dateData,
                        alarmName = binding.alarmNameEditText.text.toString(),
                        alarmData = alarmDataa,
                        radio = radioVar

                    )
                    alarmViewModel.insertAlarmData(alarmData)

                    requireActivity().supportFragmentManager.popBackStack()

                } else {
                    warningToast(requireContext(), getString(R.string.toast_set_minimum_time))
                }
            }

        }
         //   Log.d("yyhgth",radioVar.name)



        binding.alarmNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                setTextForAlarmName(s.toString())
            }
        })
        binding.alarmNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                clearKeyBoardFocus(binding.rootLayout)
            }
        }
    }

    private fun setTextForAlarmName(newText: String) {
        binding.alarmData = binding.alarmData?.copy(name = newText)
    }




}
