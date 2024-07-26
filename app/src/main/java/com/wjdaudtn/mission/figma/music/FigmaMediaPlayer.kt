package com.wjdaudtn.mission.figma.music

import android.media.MediaPlayer

abstract class FigmaMediaPlayer {
    abstract fun play(media: MediaPlayer, position: Int)
    abstract fun pause(media: MediaPlayer)
    abstract fun stop(media: MediaPlayer)
}