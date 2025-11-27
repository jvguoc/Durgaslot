package com.durgasoft.slot

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

object SlotSoundManager {

    private var soundPool: SoundPool? = null
    private var spinSoundId: Int = 0
    private var loaded = false

    fun init(context: Context) {
        if (soundPool != null) return   // ya inicializado

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attrs)
            .build().apply {
                setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) loaded = true
                }
            }

        spinSoundId = soundPool!!.load(context, R.raw.spin_sound, 1)
    }

    fun playSpin() {
        val sp = soundPool ?: return
        if (!loaded) return
        sp.play(spinSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        loaded = false
    }
}
