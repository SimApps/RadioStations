package com.amirami.simapp.radiostations.alarm.ui.main

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.alarm.adapters.AlarmRcAdapter
import com.amirami.simapp.radiostations.alarm.utils.MySharedPrefrences
import com.amirami.simapp.radiostations.alarm.utils.checkAboveKitkat
import com.amirami.simapp.radiostations.alarm.utils.getBackgroundImage
import com.amirami.simapp.radiostations.alarm.utils.getStatusBarHeight
import com.amirami.simapp.radiostations.alarm.utils.gone
import com.amirami.simapp.radiostations.alarm.utils.visible
import com.amirami.simapp.radiostations.databinding.AlarmFragmentMainBinding
import com.amirami.simapp.radiostations.databinding.FragmentFavoriteBinding
import com.amirami.simapp.radiostations.ui.FavoriteRadioFragmentDirections
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.amirami.simapp.radiostations.alarm.ui.activities.CommonViewModel as CommonViewModel1


@AndroidEntryPoint
class MainAlarmFragment : BottomSheetDialogFragment(R.layout.alarm_fragment_main) {
    private lateinit var binding: AlarmFragmentMainBinding

    @Inject
    lateinit var alarmRcAdapter: AlarmRcAdapter

    private val commonViewModel: CommonViewModel1 by activityViewModels()
    @Inject
    lateinit var mySharedPrefrences: MySharedPrefrences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AlarmFragmentMainBinding.bind(view)
        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }


        commonViewModel.getAllAlarm()
        commonViewModel.alarmList.observe(viewLifecycleOwner) { alarmList ->
            alarmRcAdapter.submitList(alarmList)

        }

        commonViewModel.getAllLiveAlarm().observe(viewLifecycleOwner) { alarmList ->
            if (alarmList.isEmpty()) {
                binding.emptyListBg.visible()
            } else {
                binding.emptyListBg.gone()
            }
        }



        setHasOptionsMenu(true)
        // setSlidingBehaviour()
        setViews()
        setClickListeners()
        setSwipeToDelete()
    }


    private fun setClickListeners() {

        alarmRcAdapter.setOnAlarmCancelClickListener { alarm, isChecked ->
            if (!isChecked) {
                context?.let { alarm.cancelAlarm(it) }
            } else {
                context?.let { alarm.schedule(it) }
            }
            commonViewModel.update(alarm)
        }


        alarmRcAdapter.setAlarmDeleteClickListener { alarm, position ->
            alarmRcAdapter.notifyItemRemoved(position)
            alarmRcAdapter.notifyItemRangeChanged(position, alarmRcAdapter.currentList.size)
            commonViewModel.deleteAlarm(alarm.alarmId)
            context?.let { alarm.cancelAlarm(it) }
        }


    }


    private fun setViews() {


         binding.background.getBackgroundImage(Uri.parse(mySharedPrefrences.getBrackgroundImage()))


        binding.fragmentListalarmsAddAlarm.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_createAlarmFragment)

        }
        binding.fragmentListalarmsRecylerView.apply {
            adapter = alarmRcAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.fragmentListalarmsAddAlarm.hide()
                    } else {
                        binding.fragmentListalarmsAddAlarm.show()
                    }
                }
            })
        }




        alarmRcAdapter.setOnItemClickListener { alarm ->
            val bundle = Bundle().apply {
                putSerializable("alarm", alarm)
            }
            findNavController().navigate(R.id.action_mainFragment_to_createAlarmFragment, bundle)
        }

    }


    private fun setSlidingBehaviour() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        if (checkAboveKitkat()) {

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        binding.bottomSheet.setPadding(0, 0, 0, 0)

                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        activity?.getStatusBarHeight()?.let { bottomSheet.setPadding(0, it, 0, 0) }

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    if (slideOffset < 0.5F) {
                        binding.tempView.alpha = 0.5F
                        binding.fragmentListalarmsAddAlarm.show()
                    } else {
                        binding.tempView.alpha = (slideOffset)
                        binding.fragmentListalarmsAddAlarm.hide()
                    }
                    bottomSheet.setPadding(
                        0,
                        (slideOffset * activity?.getStatusBarHeight()!!).toInt(),
                        0,
                        0
                    )
                }
            })
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_setting -> {
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                return true
            }


            else -> {
                return false
            }
        }
    }


    private fun setSwipeToDelete() {

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT

        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mAlarm = alarmRcAdapter.currentList[position]



                alarmRcAdapter.notifyItemRemoved(position)
                alarmRcAdapter.notifyItemRangeChanged(position, alarmRcAdapter.currentList.size)
                commonViewModel._alarmList.value?.removeAt(position)
                commonViewModel.deleteAlarm(mAlarm.alarmId)
                context?.let { mAlarm.cancelAlarm(it) }


            }

            private val deleteIcon =
                ContextCompat.getDrawable(activity!!, R.drawable.ic_baseline_delete_forever_24)

            private val intrinsicWidth = deleteIcon?.intrinsicWidth
            private val intrinsicHeight = deleteIcon?.intrinsicHeight
            private val background = ColorDrawable()
            private val backgroundColor = Color.parseColor("#f44336")
            private val clearPaint =
                Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(
                        c,
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }

                // Draw the red delete background
                background.color = backgroundColor
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight!!) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon?.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteIcon?.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            private fun clearCanvas(
                c: Canvas?,
                left: Float,
                top: Float,
                right: Float,
                bottom: Float
            ) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.fragmentListalarmsRecylerView)
        }

    }

    override fun onStart() {
        super.onStart()

     //   (activity as MainActivity).setFullScreenWithBtmNav()
     //   (activity as MainActivity).setFullScreenForNotch()


    }

}


