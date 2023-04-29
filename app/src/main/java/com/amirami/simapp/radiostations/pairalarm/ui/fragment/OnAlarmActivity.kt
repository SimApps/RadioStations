package com.amirami.simapp.radiostations.pairalarm.ui.fragment

import com.amirami.simapp.radiostations.R

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.amirami.simapp.radiostations.RadioFunction.dynamicToast
import com.amirami.simapp.radiostations.RadioFunction.errorToast
import com.amirami.simapp.radiostations.RadioFunction.succesToast
import com.amirami.simapp.radiostations.databinding.ActivityOnAlarmBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.pairalarm.extensions.displayOn
import com.amirami.simapp.radiostations.pairalarm.extensions.doShortVibrateOnce
import com.amirami.simapp.radiostations.pairalarm.model.AlarmModeType
import com.amirami.simapp.radiostations.pairalarm.model.CalculatorProblem
import com.amirami.simapp.radiostations.pairalarm.util.ALARM_CODE_TEXT
import com.amirami.simapp.radiostations.pairalarm.util.NEXT_ALARM_WORKER
import com.amirami.simapp.radiostations.pairalarm.util.getCurrentHourDoubleDigitWithString
import com.amirami.simapp.radiostations.pairalarm.util.getCurrentMinuteDoubleDigitWithString
import com.amirami.simapp.radiostations.pairalarm.util.resetAllAlarms
import com.amirami.simapp.radiostations.pairalarm.util.setAlarmOnBroadcast
import com.amirami.simapp.radiostations.pairalarm.viewModel.AlarmViewModel
import com.amirami.simapp.radiostations.pairalarm.worker.NextAlarmWorker
import com.amirami.simapp.radiostations.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiostations.viewmodel.UIEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class OnAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by viewModels()
    lateinit var calculatorProblem: CalculatorProblem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        // 현재 화면이 자동으로 꺼지지 않게 유지 & 잠금화면에 액티비티 띄우기
        displayOn()

        val alarmCode = intent.getStringExtra(ALARM_CODE_TEXT)

        val backButtonCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        this.onBackPressedDispatcher.addCallback(this, backButtonCallback)

        if (alarmCode != null) {
            val goesOffAlarmData = alarmViewModel.searchAlarmCode(alarmCode.toString())
            // 현재 시간이 계속 갱신되게한다
            val handler = Handler(Looper.getMainLooper())
            val handlerTask = object : Runnable {
                override fun run() {
                    handler.postDelayed(this, 3600)

                    binding.hour.setText(getCurrentHourDoubleDigitWithString())
                    binding.min.setText(getCurrentMinuteDoubleDigitWithString())
                }
            }
            handler.post(handlerTask)
            lifecycleScope.launch {
                goesOffAlarmData.first { alarmData ->
                    simpleMediaViewModel.loadData(listOf(alarmData.radio)as MutableList<RadioEntity>)
                    simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
                    binding.hour.setText(getCurrentHourDoubleDigitWithString())
                    binding.min.setText(getCurrentMinuteDoubleDigitWithString())
                    calculatorProblem = alarmViewModel.getRandomNumberForCalculator()
                    binding.calculatorProblem = calculatorProblem
                    binding.showCalculatorProblem = alarmData.mode == AlarmModeType.CALCULATE.mode
                    binding.alarmName.text = alarmData.name + " Radio : "+ alarmData.radio.name

                    if (alarmData.quick) {
                        alarmViewModel.deleteAlarmData(alarmData)
                    }

                    // 삭제하거나 변경된 알람들을 반영한다(Noti 등)
                    val alarmTimeWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<NextAlarmWorker>().build()
                    WorkManager.getInstance(this@OnAlarmActivity)
                        .enqueueUniqueWork(
                            NEXT_ALARM_WORKER,
                            ExistingWorkPolicy.REPLACE,
                            alarmTimeWorkRequest as OneTimeWorkRequest
                        )
                    resetAllAlarms(this@OnAlarmActivity)
                    true
                }
            }

            // +10분 스누즈
            binding.tenMinutes.setOnClickListener {
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 10)
                }
                val addHour = calendar.get(Calendar.HOUR_OF_DAY)
                val addMinute = calendar.get(Calendar.MINUTE)
                setAlarmOnBroadcast(
                    this@OnAlarmActivity,
                    alarmCode.toInt(),
                    addHour,
                    addMinute
                )

                dynamicToast(
                    this@OnAlarmActivity,
                    getString(R.string.toast_ten_minute_later)
                )
                simpleMediaViewModel.onUIEvent(UIEvent.Stop)
                handler.removeMessages(0)
                finish()
            }

            binding.ok.setOnClickListener {
                simpleMediaViewModel.onUIEvent(UIEvent.Stop)
                handler.removeMessages(0)
                finish()
            }

            lifecycleScope.launch {
                alarmViewModel.answer.collectLatest {
                    when {
                        it.isBlank() -> binding.problemAnswer.text = "???"
                        it == calculatorProblem.answer -> {
                            succesToast(
                                this@OnAlarmActivity,
                                getString(R.string.toast_correct_calculator_answer)
                            )
                            simpleMediaViewModel.onUIEvent(UIEvent.Stop)
                            // TODO: 정답 맞추면 dialog로 정답인거 알려주고 ok 누르면 액티비티랑 같이 꺼지게 하기
                            finish()
                        }
                        it.length > 3 -> {
                            alarmViewModel.answer.value = ""
                            doShortVibrateOnce()
                            errorToast(
                                this@OnAlarmActivity,
                                getString(R.string.toast_wrong_calculator_answer)
                            )
                        }
                        else -> binding.problemAnswer.text = it
                    }
                }
            }

            // 숫자 버튼 클릭에 따른 행동 정의
            binding.oneButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "1"
            }
            binding.twoButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "2"
            }
            binding.threeButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "3"
            }
            binding.fourButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "4"
            }
            binding.fiveButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "5"
            }
            binding.sixButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "6"
            }
            binding.sevenButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "7"
            }
            binding.eightButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "8"
            }
            binding.nineButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "9"
            }
            binding.zeroButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "0"
            }

            binding.deleteButton.setOnClickListener {
                alarmViewModel.answer.value.let {
                    if (it.isNotBlank()) {
                        alarmViewModel.answer.value = it.dropLast(1)
                    }
                }
            }

            binding.resetButton.setOnClickListener {
                alarmViewModel.answer.value = ""
            }

        } else {
            errorToast(this, getString(R.string.on_alarm_error))
            finish()
        }
    }
}



