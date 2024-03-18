package com.amirami.simapp.radiobroadcastpro.pairalarm.extensions

import android.view.View

var lastClickTime = 0L
fun View.setOnSingleClickListener(onClick: (view: View) -> Unit) {
    setOnClickListener {
        if (lastClickTime < System.currentTimeMillis() - 500) {
            lastClickTime = System.currentTimeMillis()
            onClick(this)
        }
    }
}

fun View.setFadeVisible(visible: Boolean, fadeDuration: Long, fadeInDelay: Long) {
    val animator = animate()
    if (visible){
        animator.startDelay = fadeInDelay
        animator.alpha(1f).setDuration(fadeDuration).withEndAction { visibility = View.VISIBLE }.start()
    }else{
        animator.alpha(0f).setDuration(fadeDuration).withEndAction { visibility = View.GONE }.start()
    }
}
