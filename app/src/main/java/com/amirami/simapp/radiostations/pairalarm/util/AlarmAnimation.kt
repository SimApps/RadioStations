package com.amirami.simapp.radiostations.pairalarm.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AlarmAnimation {
    private lateinit var animatorSet: AnimatorSet

    fun swing(view: View): AnimatorSet{
        animatorSet = AnimatorSet()
        val centerToLeft = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -30f).setDuration(50)
        val leftToRight = ObjectAnimator.ofFloat(view, View.ROTATION, -30f, 30f).setDuration(100)
        val rightToLeft = ObjectAnimator.ofFloat(view, View.ROTATION, 30f, -30f).setDuration(100)
        val leftToCenter = ObjectAnimator.ofFloat(view, View.ROTATION, -30f, 0f).setDuration(50)

        animatorSet.playSequentially(centerToLeft, leftToRight, rightToLeft, leftToCenter)

        return animatorSet
    }

    fun jump(view: View): AnimatorSet{
        animatorSet = AnimatorSet()
        val goingUp = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -30f).setDuration(100)
        val goingDown = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, 0f).setDuration(200)

        animatorSet.playSequentially(goingUp, goingDown)

        return animatorSet
    }
}
