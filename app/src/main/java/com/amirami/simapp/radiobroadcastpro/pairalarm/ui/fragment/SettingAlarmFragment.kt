package com.amirami.simapp.radiobroadcastpro.pairalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import autoCleared
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.databinding.FragmentSettingAlarmBinding
import com.amirami.simapp.radiobroadcastpro.pairalarm.groupieitem.SettingContentItem
import com.amirami.simapp.radiobroadcastpro.pairalarm.groupieitem.SpacerItem
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.SettingContentType
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.SettingContents
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SettingAlarmFragment : Fragment(R.layout.fragment_setting_alarm) {
    private var binding: FragmentSettingAlarmBinding by autoCleared()
    private val settingRecyclerAdapter = GroupieAdapter()
    private val job by lazy { Job() }
    private val infoViewModel: InfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentSettingAlarmBinding>(view)?.let { binding = it } ?: return

        binding.lifecycleOwner = this
        val settingItemList = SettingContents.values().map { it }

        binding.settingRecycler.apply {
            adapter = settingRecyclerAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        setupSettingData(settingItemList)


    }

    private fun setupSettingData(settingItemList: List<SettingContents>) {
        when {
            settingItemList.size == 1 -> {
                settingItemList.map { settingItem ->
                    SettingContentItem(
                        requireContext(),
                        settingItem,
                        SettingContentType.SINGLE,
                        Dispatchers.Main,
                        job
                    )
                        .also { settingRecyclerAdapter.add(it) }
                }
            }
            settingItemList.size > 1 -> {
                settingItemList.mapIndexed { index, data ->
                    if (data == SettingContents.BLANK) {
                        setupItem()
                    } else {
                        when {
                            // 첫 번째 항목이거나 직전 값이 빈칸일 경우
                            index == 0 || (settingItemList[index - 1] == SettingContents.BLANK) -> {
                                if (settingItemList.size == 2) {
                                    setupItem(data, SettingContentType.FIRST)
                                } else {
                                    setupItem(data, SettingContentType.FIRST)
                                }
                            }
                            // 마지막 항목이거나 이후 값이 빈칸일 경우
                            index == settingItemList.size - 1 || (settingItemList[index + 1] == SettingContents.BLANK) -> {
                                setupItem(data, SettingContentType.LAST)
                            }
                            else -> {
                                setupItem(data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupItem(data: SettingContents? = null, contentType: SettingContentType? = null) {
        if (data == null) {
            SpacerItem.xnormal().also { settingRecyclerAdapter.add(it) }
        } else {
            SettingContentItem(requireContext(), data, contentType, Dispatchers.Main, job)
                .also { settingRecyclerAdapter.add(it) }
        }
    }
}
