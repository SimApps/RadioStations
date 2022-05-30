package com.amirami.simapp.radiostations.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.YesNoDialogueBinding
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoBottomSheetFragment: BottomSheetDialogFragment() {
    private var _binding: YesNoDialogueBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YesNoDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding get() = _binding!!
    val argsFrom: InfoBottomSheetFragmentArgs by navArgs()
    private val infoViewModel: InfoViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var go=resources.getString(R.string.GO)

        collectLatestLifecycleFlow(infoViewModel.putTheme) {
            RadioFunction.gradiancolorLinearlayoutTransitionBottomSheet(binding.infobtmsheetlayout,0,it)
            RadioFunction.maintextviewColor(binding.messageTxtVw,it)
        }


        if(argsFrom.title== getString(R.string.discaimertitle) || argsFrom.title==getString(R.string.Keep_in_mind)){
            binding.TitleTxtVw.text =argsFrom.title
            binding.messageTxtVw.text =argsFrom.msg
            //binding.btnNon.text = getString(R.string.Exit)
            binding.yesnoDivider.visibility = View.GONE
            binding.verticalDividerDialogue.visibility = View.GONE
            binding.btnOui.visibility = View.GONE
            binding.btnNon.visibility = View.GONE
        }
        else if (argsFrom.title=="BatterieOptimisation"){

            // Display a message on alert dialog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = requireContext().packageName
                val pm: PowerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    binding.messageTxtVw.text =resources.getString(R.string.batterieoptimisationmessages)

                }
                else {
                    binding.messageTxtVw.text =resources.getString(R.string.batterieoptimisationmessageson)
                    go=resources.getString(R.string.Exit)
                    binding.btnOui.visibility = View.GONE
                    binding.yesnoDivider.visibility = View.GONE
                    binding.verticalDividerDialogue.visibility = View.GONE
                }
            }
            else{
                binding.messageTxtVw.text =resources.getString(R.string.nobatterieoptimisationmessageso)
            }

            binding.btnOui.text =go
            binding.TitleTxtVw.visibility = View.GONE
            binding.verticalDividerDialogue.visibility = View.GONE
            binding.btnNon.visibility = View.GONE
        }
        else if (argsFrom.title==getString(R.string.Recordings)){
            binding.TitleTxtVw.visibility = View.VISIBLE
            binding.TitleTxtVw.text =getString(R.string.deleterecording)
            binding.messageTxtVw.text = argsFrom.recordname
            //binding.btnNon.text = getString(R.string.Exit)
            binding.yesnoDivider.visibility = View.VISIBLE
            binding.verticalDividerDialogue.visibility = View.VISIBLE
            binding.btnOui.visibility = View.VISIBLE
            binding.btnNon.visibility = View.VISIBLE
        }
        else if(argsFrom.title=="signinOut"){
            binding.TitleTxtVw.visibility = View.VISIBLE
            binding.btnOui.visibility = View.VISIBLE
            binding.btnNon.visibility = View.VISIBLE
            binding.yesnoDivider.visibility = View.VISIBLE
            binding.verticalDividerDialogue.visibility = View.VISIBLE
            binding.TitleTxtVw.text = getString(R.string.DisconnectMsg)
        }

        binding.btnOui.setSafeOnClickListener{
            if (argsFrom.title=="BatterieOptimisation"){
                // Do something when user press the positive button

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if(go==resources.getString(R.string.Exit)){
                        dismiss()
                    }
                    else{
                        startActivityForResult(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), 0)
                        dismiss()
                    }
                }
            }
            else if(argsFrom.title==getString(R.string.Recordings)){
                if(RadioFunction.getRecordedFiles(requireContext()).size>0 && argsFrom.msg.toInt()!=-2) {
                    infoViewModel.putUpdateRecordInfo(true,argsFrom.msg.toInt())

                }
            }
            else if(argsFrom.title=="signinOut"){
                infoViewModel.putLogInInfo("signinOut")

            }
            dismiss()
        }

        binding.btnNon.setSafeOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    fun <T> InfoBottomSheetFragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
}