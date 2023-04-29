package com.amirami.simapp.radiostations.pairalarm.util

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.pairalarm.database.table.Weekend
import com.amirami.simapp.radiostations.pairalarm.model.AlarmMode
import com.amirami.simapp.radiostations.pairalarm.model.BellType
import com.google.android.material.button.MaterialButton


object BindingAdapter {
    /**
     *  시간을 NumberPicker에 세팅한다
     *
     *  @param hour 시간(24시간 포맷)
     */
    @JvmStatic
    @BindingAdapter("hour")
    fun NumberPicker.setHour(hour: Int) {
        this.value = if (hour > 12) {
            hour - 12
        } else {
            hour
        }
    }

    @JvmStatic
    @BindingAdapter("hour")
    fun TextView.setHour(hour: Int) {
        text = if (hour >= 10) hour.toString() else "0$hour"
    }

    @JvmStatic
    @BindingAdapter("min")
    fun TextView.setMin(min: Int) {
        text = if (min >= 10) min.toString() else "0$min"
    }

    /**
     *  오전/오후를 NumberPicker에 세팅한다
     *
     *  @param hourForAMPM 시간(24시간 포맷)
     */
    @JvmStatic
    @BindingAdapter("hourForAMPM", "minForAMPM", "manualChange")
    fun NumberPicker.setAMPM(hourForAMPM: Int, minForAMPM: Int, manualChange: Boolean = false) {
        if (!manualChange){
            this.value = when {
                this.value == 1 -> 1
                hourForAMPM > 12 -> 1
                hourForAMPM == 12 && minForAMPM > 0 -> 1
                else -> 0
            }
        }
    }

    /**
     *  볼륨에 따라 이미지를 셋팅한다
     *
     *  @param volume 시간(24시간 포맷)
     */
    @JvmStatic
    @BindingAdapter("volume")
    fun ImageView.setVolumeImage(volume: Int) {
        if (volume == 0) {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.volume_mute
                )
            )
        } else {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.volume_icon
                )
            )
        }
    }

    /**
     *  벨 인덱스에 따른 텍스트를 붙인다
     *
     *  @param bellIndex 벨 인덱스
     */
    @JvmStatic
    @BindingAdapter("bellIndex")
    fun TextView.setBellText(bellIndex: Int) {
        this.text = when (bellIndex) {
            0 -> BellType.WALKING.title
            1 -> BellType.PINAOMAN.title
            2 -> BellType.HAPPYTOWN.title
            3 -> BellType.LONELY.title
            else -> BellType.WALKING.title
        }
    }

    /**
     *  알람 모드에 따른 텍스트를 붙인다
     *
     *  @param alarmMode 알람 인덱스
     */
    @JvmStatic
    @BindingAdapter("alarmMode")
    fun TextView.setAlarmMode(alarmMode: Int) {
        this.text = when (alarmMode) {
            0 -> AlarmMode.NORMAL.mode
            1 -> AlarmMode.CALCULATION.mode
            else -> AlarmMode.NORMAL.mode
        }
    }

    @JvmStatic
    @BindingAdapter("vibrateMode")
    fun ImageView.setVibrateMode(vibrateMode: Int) {
        when (vibrateMode) {
            0 -> this.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_no_vib
                )
            )
            1 -> this.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_vib_1))
            2 -> this.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_vib_2))
        }
    }

    @JvmStatic
    @BindingAdapter("alarmCode")
    fun MaterialButton.saveOrUpdateText(alarmCode: String?) {
        this.text = if (alarmCode.isNullOrBlank()) {
            context.getString(R.string.save)
        } else {
            context.getString(R.string.update)
        }
    }

    @JvmStatic
    @BindingAdapter("week", "weekClicked")
    fun TextView.setWeekTextColor(week: Weekend, weekClicked: Boolean) {
        when (week) {
            Weekend.WEEK -> {
                if (weekClicked) {
                    setTextColor(Color.DKGRAY)
                    return
                }
            }
            Weekend.SAT -> {
                if (weekClicked) {
                    setTextColor(context.getColor(R.color.light_blue))
                    return
                }
            }
            Weekend.SUN -> {
                if (weekClicked) {
                    setTextColor(Color.RED)
                    return
                }
            }
        }
        setTextColor(context.getColor(R.color.new_subTextColor))
    }

    @JvmStatic
    @BindingAdapter("week", "weekClicked")
    fun MaterialButton.setColorStroke(week: Weekend, weekClicked: Boolean) {
        rippleColor = ColorStateList.valueOf(Color.TRANSPARENT)
        when (week) {
            Weekend.SAT -> {
                if (weekClicked) {
                    setStrokeColorResource(R.color.light_blue)
                    return
                }
            }
            Weekend.SUN -> {
                if (weekClicked) {
                    setStrokeColorResource(R.color.red)
                    return
                }
            }
            Weekend.WEEK -> {
                if (weekClicked) {
                    setStrokeColorResource(R.color.deep_yellow)
                    return
                }
            }
        }
        setStrokeColorResource(R.color.background)
    }

    @JvmStatic
    @BindingAdapter("visible")
    fun View.setVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("setMarginTop")
    fun View.setMarginTop(marginTop: Float) {
        val layoutParams = layoutParams as MarginLayoutParams
        updateLayoutParams<MarginLayoutParams> {
            topMargin = marginTop.toInt()
        }
        this.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("setMarginBottom")
    fun View.setMarginBottom(marginBottom: Float) {
        val layoutParams = layoutParams as MarginLayoutParams
        updateLayoutParams<MarginLayoutParams> {
            bottomMargin = marginBottom.toInt()
        }
        this.layoutParams = layoutParams
    }
}