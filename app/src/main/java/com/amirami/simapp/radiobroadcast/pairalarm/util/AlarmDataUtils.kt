package com.amirami.simapp.radiobroadcast.pairalarm.util

import android.media.MediaPlayer

class AlarmMusic {
    companion object {
        private var musicIndex: MediaPlayer? = null

        fun getCurrentMusic() = musicIndex

        fun setCurrentMusic(selectedMusic: MediaPlayer?) {
            musicIndex = selectedMusic
        }
    }
}