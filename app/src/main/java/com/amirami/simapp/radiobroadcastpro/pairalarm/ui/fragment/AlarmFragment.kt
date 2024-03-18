package com.amirami.simapp.radiobroadcastpro.pairalarm.ui.fragment

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import autoCleared
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.RadioFunction.warningToast
import com.amirami.simapp.radiobroadcastpro.databinding.FragmentAlarmBinding
import com.amirami.simapp.radiobroadcastpro.pairalarm.broadcast.AlarmReceiver
import com.amirami.simapp.radiobroadcastpro.pairalarm.extensions.getPermissionActivityResultLauncher
import com.amirami.simapp.radiobroadcastpro.pairalarm.extensions.setFadeVisible
import com.amirami.simapp.radiobroadcastpro.pairalarm.extensions.setOnSingleClickListener
import com.amirami.simapp.radiobroadcastpro.pairalarm.extensions.showErrorSnackBar
import com.amirami.simapp.radiobroadcastpro.pairalarm.groupieitem.AlarmItem
import com.amirami.simapp.radiobroadcastpro.pairalarm.service.AlarmForeground
import com.amirami.simapp.radiobroadcastpro.pairalarm.ui.dialog.SimpleDialog
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.NEXT_ALARM_NOTIFICATION_TEXT
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.cancelAlarmNotification
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.getNextAlarm
import com.amirami.simapp.radiobroadcastpro.pairalarm.util.resetAllAlarms
import com.amirami.simapp.radiobroadcastpro.pairalarm.viewModel.AlarmViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@UnstableApi @AndroidEntryPoint
class AlarmFragment : Fragment(R.layout.fragment_alarm), AlarmItem.OnItemClickListener {
    private var binding: FragmentAlarmBinding by autoCleared()
     private lateinit var serviceIntent: Intent
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val permissionRequest = getPermissionActivityResultLauncher(
        allGranted = {
            // 모든 권한이 확인되어 있을 때
            if (checkOverlayPermission()) {
                val action = AlarmFragmentDirections.actionAlarmFragmentToSimpleAlarmSetFragment()
                this@AlarmFragment.findNavController().navigate(action)
            }
        },
        notGranted = {
            // 1개라도 허락되지 않은 권한이 있을 때
            SimpleDialog.showSimpleDialog(
                requireContext(),
                getString(R.string.dialog_permission_title),
                getString(R.string.dialog_notification_permission_message),
                positive = { },
                negative = {
                    warningToast(
                        requireContext(),
                        getString(R.string.dialog_notification_permission_message)
                    )
                }
            )
        }
    )

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 오버레이 권한 설정에서 돌아왔을 때
          //  Timber.d("resultCode: ${result.resultCode}")
            // 오버레이 권한은 설정 후 Back키로 돌아오기 때문에 RESULT_CANCELED가 찍힌다
            if (result.resultCode == Activity.RESULT_CANCELED) {
                SimpleDialog.showSimpleDialog(
                    requireContext(),
                    getString(R.string.dialog_permission_title),
                    getString(R.string.dialog_overlay_message),
                    positive = { checkOverlayPermission() },
                    negative = {
                        warningToast(
                            requireContext(),
                            getString(R.string.dialog_permission_overlay_no)
                        )
                    }
                )
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentAlarmBinding>(view)?.let { binding = it } ?: return
        binding.lifecycleOwner = this


        val receiver = ComponentName(requireActivity(), AlarmReceiver::class.java)

        requireContext().packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // TODO: 나중에 화면 사이즈에 맞게 숫자 바뀌게 하기


        // Groupie - RecyclerView 정의
        val alarmRecyclerAdapter = GroupieAdapter()
        binding.alarmRecycler.run {
            adapter = alarmRecyclerAdapter
            layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // 처음은 발동하지 않게
                    if (dy != 0) {
                        binding.fabLayout.visibility = View.GONE
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == SCROLL_STATE_IDLE) {
                        binding.fabLayout.setFadeVisible(true, 700, 0)
                    }
                }
            })
        }


        serviceIntent = Intent(requireContext(), AlarmForeground::class.java)


        // Groupie - RecyclerView 데이터 입력
        viewLifecycleOwner.lifecycleScope.launch {
            alarmViewModel.getAllAlarmData().collect { alarmDataList ->

              //  Timber.d("AlarmData: $alarmDataList")
                alarmDataList.map { AlarmItem(requireContext(), it, alarmViewModel,this@AlarmFragment) }
                    .also { alarmRecyclerAdapter.update(it) }

                if (alarmDataList.isEmpty()) {
                    binding.whenemptyfavImage.visibility = View.VISIBLE
                    requireContext().stopService(serviceIntent)
                    cancelAlarmNotification(requireContext())
                } else {
                    binding.whenemptyfavImage.visibility = View.GONE
                    serviceIntent.putExtra(NEXT_ALARM_NOTIFICATION_TEXT, getNextAlarm(alarmDataList))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireContext().startForegroundService(serviceIntent)
                    }

                    resetAllAlarms(requireContext(), alarmDataList)
                }
            }
        }

        // FAB의 간격 조절
        val metrics = this.resources.displayMetrics
        val interval = when {
            // mdpi
            metrics.densityDpi <= 160 -> 55f
            // hdpi
            metrics.densityDpi <= 240 -> 105f
            // xhdpi
            metrics.densityDpi <= 320 -> 155f
            // xxhdpi
            metrics.densityDpi <= 480 -> 205f
            // xxxhdpi
            else -> 255f

        }

            // binding.fabLayout.animationSize = interval
        //   binding.fabLayout.fabAnimateDuration = interval.toInt()
        // 일반 알람 설정
        binding.fab2.setOnSingleClickListener {
             val action = AlarmFragmentDirections.actionAlarmFragmentToNormalAlarmSetFragment()
             this@AlarmFragment.findNavController().navigate(action)

       //     alarmActivityIntent = Intent(activity, NormalAlarmSetFragment::class.java)
           checkEssentialPermission()
        }

        // 간단 알람 설정
        binding.fab3.setOnSingleClickListener {
            val action = AlarmFragmentDirections.actionAlarmFragmentToSimpleAlarmSetFragment()
            this@AlarmFragment.findNavController().navigate(action)

       //     alarmActivityIntent = Intent(activity, SimpleAlarmSetFragment::class.java)


             checkEssentialPermission()
        }

        binding.fab4.setOnSingleClickListener {
            val action = AlarmFragmentDirections.actionAlarmFragmentToSettingAlarmFragment()
            this@AlarmFragment.findNavController().navigate(action)
        }

        alarmViewModel.failure.observe(viewLifecycleOwner) {
            showErrorSnackBar(it)
        }



    }

    // 필요한 권한 확인
    // TODO: Dialog로 어떤 권한이 왜 필요한지 설명하기
    private fun checkEssentialPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkOverlayPermission()) {
                permissionRequest.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
            }
        } else {
            if (checkOverlayPermission()) {
             //   val action = AlarmFragmentDirections.actionAlarmFragmentToSimpleAlarmSetFragment()
             //   this@AlarmFragment.findNavController().navigate(action)

              //  requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    // 오버레이 권한 확인
    private fun checkOverlayPermission(): Boolean {
        // 권한이 ok가 아닐 때
        return if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            resultLauncher.launch(intent)
            false
        } else {
            true
        }
    }

    override fun onItemClick(alarmCode: String) {
        val action = AlarmFragmentDirections.actionAlarmFragmentToNormalAlarmSetFragment(alarmCode)
        this@AlarmFragment.findNavController().navigate(action)
    }
}
