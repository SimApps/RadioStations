package com.amirami.simapp.radiobroadcastpro.ui

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import com.amirami.player_service.service.PlayerState
import com.amirami.simapp.radiobroadcastpro.*
import com.amirami.simapp.radiobroadcastpro.MainActivity.Companion.time
import com.amirami.simapp.radiobroadcastpro.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.databinding.CountdownTimerPopupBinding
import com.amirami.simapp.radiobroadcastpro.viewmodel.DownloaderViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.SimpleMediaViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.Int
import kotlin.String
import kotlin.getValue

@UnstableApi @AndroidEntryPoint
class SetTimerBottomSheetFragment :
    BottomSheetDialogFragment(),
    NumberPicker.OnValueChangeListener {

    private val infoViewModel: InfoViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val downloaderViewModel: DownloaderViewModel by activityViewModels()
    private var _binding: CountdownTimerPopupBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CountdownTimerPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.putTimer) {
            if (binding.timerUnitTxvw.text == getString(R.string.Minute)) {
                //     DynamicToast.makeError(requireContext(), it.toString(), 3).show()
                val days = it / 86400
                val hours = (it % 86400) / 3600
                val minutes = ((it % 86400) % 3600) / 60
                val seconds = ((it % 86400) % 3600) % 60

                val timeLeftFormatted: String = when {
                    days > 0 -> String.format(Locale.getDefault(), "%d:%d:%02d:%02d", days, hours, minutes, seconds)
                    hours > 0 -> String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
                    minutes > 0 -> String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                    else -> String.format(Locale.getDefault(), "%02d", seconds)
                }

                when (it) {
                    1 -> {
                        binding.buttonStartPause.text = resources.getString(R.string.Start)
                        binding.switchTmerData.visibility = View.VISIBLE
                        binding.numberpikerLl.visibility = View.VISIBLE
                        binding.textViewCountdown.visibility = View.GONE
                        collectLatestLifecycleFlow(lifecycleOwner = this,downloaderViewModel.downloadState) { downloadState ->
                            downloaderViewModel.cancelDownloader()
                        }

                        infoViewModel.stoptimer(true)
                    }
                    -1 -> {
                        binding.buttonStartPause.text = resources.getString(R.string.Start)
                        binding.switchTmerData.visibility = View.VISIBLE
                        binding.numberpikerLl.visibility = View.VISIBLE
                        binding.textViewCountdown.visibility = View.GONE
                    }
                    else -> {
                        binding.buttonStartPause.text = resources.getString(R.string.Reset)
                        binding.numberpikerLl.visibility = View.GONE
                        binding.textViewCountdown.visibility = View.VISIBLE
                        binding.switchTmerData.visibility = View.GONE
                        binding.textViewCountdown.text = timeLeftFormatted
                    }
                }
            }
        }

        collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.putDataConsumptionTimer) {
            //    DynamicToast.makeError(requireContext(), it.toString(), 3).show()

            if (binding.timerUnitTxvw.text == getString(R.string.MB)) {
                when {
                    it > 0L -> {
                        binding.buttonStartPause.text = getString(R.string.Reset)
                        binding.numberpikerLl.visibility = View.GONE
                        binding.textViewCountdown.visibility = View.VISIBLE
                        binding.switchTmerData.visibility = View.GONE
                        binding.textViewCountdown.text = RadioFunction.bytesIntoHumanReadable(it)
                    }
                    it == -1L -> {
                        binding.buttonStartPause.text = getString(R.string.Start)
                        binding.switchTmerData.visibility = View.VISIBLE
                        binding.numberpikerLl.visibility = View.VISIBLE
                        binding.textViewCountdown.visibility = View.GONE
                    }
                    it < 0L -> {
                        binding.buttonStartPause.text = getString(R.string.Start)
                        binding.switchTmerData.visibility = View.VISIBLE
                        binding.numberpikerLl.visibility = View.VISIBLE
                        binding.textViewCountdown.visibility = View.GONE

                        //  Exoplayer.releasePlayer(requireContext())
                    }
                }
            }
        }



        val spinnerTimerArray = resources.getStringArray(R.array.spinnerTimerArray)
        binding.numberPickers.minValue = 0
        binding.numberPickers.maxValue = spinnerTimerArray.size - 1
        binding.numberPickers.displayedValues = spinnerTimerArray

        binding.numberPickers.wrapSelectorWheel = true
        binding.numberPickers.setOnValueChangedListener(this)


/*
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switecher state", false)) {
                    switch_tmer_data?.text =resources.getString(R.string.Data)
            timer_unit_txvw?.text =resources.getString(R.string.MB)
        }
        else{
            switch_tmer_data?.text =resources.getString(R.string.Timer)
            timer_unit_txvw?.text =resources.getString(R.string.Minute)
        }

*/
        if (!time) {
            binding.switchTmerData.text = resources.getString(R.string.Data)
            binding.timerUnitTxvw.text = resources.getString(R.string.MB)
            binding.switchTmerData.isChecked = true
        } else {
            binding.switchTmerData.text = resources.getString(R.string.Timer)
            binding.timerUnitTxvw.text = resources.getString(R.string.Minute)
            binding.switchTmerData.isChecked = false
        }

        // switch_tmer_data?.isChecked = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switecher state", false)

        binding.switchTmerData.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                time = false
                binding.switchTmerData.text = resources.getString(R.string.Data)
                binding.timerUnitTxvw.text = resources.getString(R.string.MB)
            } else {
                time = true
                binding.switchTmerData.text = resources.getString(R.string.Timer)
                binding.timerUnitTxvw.text = resources.getString(R.string.Minute)
            }

            //     PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("switecher state", isChecked).apply()

            //  switch_tmer_data?.textOff switch_tmer_data?.texton
            //   DynamicToast.makeError(context, isChecked.toString(), 3).show()
        }




        binding.buttonStartPause.setSafeOnClickListener {
            collectLatestLifecycleFlow(lifecycleOwner = this,simpleMediaViewModel.isPlaying) {
                if (it == PlayerState.INITIANIAL || it == PlayerState.STOPED) {
                    DynamicToast.makeError(requireContext(), getString(R.string.Radio_is_off), 3).show()
                } else {
                    val input = spinnerTimerArray[binding.numberPickers.value]!!
                    /* val millisInput = if (binding.timerUnitTxvw.text == getString(R.string.Minute)) Long.parseLong(input) * 60000
                    else input.toLong() * 1024 * 1024*/

                    if (binding.timerUnitTxvw.text == getString(R.string.Minute) && binding.buttonStartPause.text != getString(R.string.Reset)) {
                        infoViewModel.puttimer(input.toInt() * 60)
                    } else if (binding.switchTmerData.text == getString(R.string.Data) && binding.buttonStartPause.text != getString(R.string.Reset)) {
                        infoViewModel.putDataConsumptiontimer(input.toLong() * 1024 * 1024)
                    } else if (binding.buttonStartPause.text == getString(R.string.Reset)) {
                        infoViewModel.stoptimer(false)
                        infoViewModel.stopdatatimer(false)
                    }
                    /*  else if(binding.buttonStartPause.text == getString(R.string.Reset) && binding.timerUnitTxvw.text == getString(R.string.Minute))
                         infoViewModel.stoptimer()

                      else if ( binding.switchTmerData.text == getString(R.string.Data) && binding.buttonStartPause.text == getString(R.string.Reset)){
                          //   DynamicToast.makeError(requireContext(), "it.toString()", 3).show()
                          infoViewModel.stopdatatimer()
                      }*/
                }

            }

        }
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        binding.textView6.text = "$p2"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}