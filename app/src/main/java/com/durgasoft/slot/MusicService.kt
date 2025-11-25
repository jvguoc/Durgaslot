package com.durgasoft.slot

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    companion object {
        const val ACTION_PLAY = "com.durgasoft.slot.MUSIC_PLAY"
        const val ACTION_PAUSE = "com.durgasoft.slot.MUSIC_PAUSE"
        const val ACTION_STOP = "com.durgasoft.slot.MUSIC_STOP"
    }

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> pauseInternal()
            AudioManager.AUDIOFOCUS_LOSS -> {
                pauseInternal()
                stopSelf()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> playInternal()
            ACTION_PAUSE -> pauseInternal()
            ACTION_STOP -> stopInternal()
        }
        return START_NOT_STICKY
    }

    private fun ensurePlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.slot_theme).apply {
                isLooping = true
            }
        }
    }

    private fun playInternal() {
        val result = audioManager.requestAudioFocus(
            focusListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return

        ensurePlayer()
        if (mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
        }
    }

    private fun pauseInternal() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        audioManager.abandonAudioFocus(focusListener)
    }

    private fun stopInternal() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        audioManager.abandonAudioFocus(focusListener)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        audioManager.abandonAudioFocus(focusListener)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
