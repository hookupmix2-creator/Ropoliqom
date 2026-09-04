package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * سیستم صوتی هوشمند بازی با سنتز مستقیم بدون نیاز به فایلهای حجیم خارجی
 */
object SoundManager {
    var isSoundEnabled: Boolean = true
    private val audioScope = CoroutineScope(Dispatchers.Default)

    private const val SAMPLE_RATE = 22050

    fun playDiceRoll() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                // شبیه‌سازی صدای تق‌تق پرتاب تاس با پالس‌های فرکانسی متغیر
                val numClicks = 6
                for (i in 0 until numClicks) {
                    val freq = 300 + (Math.random() * 400).toInt()
                    playTone(freq, 25, 0.5f)
                    Thread.sleep(40)
                }
            } catch (e: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    fun playMoneyReceived() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                // ملودی شاد دو زمانه دریافت سود
                playTone(523, 70, 0.4f) // C5
                Thread.sleep(30)
                playTone(659, 90, 0.5f) // E5
                Thread.sleep(30)
                playTone(784, 120, 0.6f) // G5
            } catch (e: Exception) {
            }
        }
    }

    fun playMoneyPaid() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                // صدای پرداخت اجاره
                playTone(440, 80, 0.4f)
                Thread.sleep(20)
                playTone(349, 110, 0.4f)
            } catch (e: Exception) {
            }
        }
    }

    fun playBuyProperty() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                playTone(587, 80, 0.5f) // D5
                Thread.sleep(40)
                playTone(880, 140, 0.6f) // A5
            } catch (e: Exception) {
            }
        }
    }

    fun playChanceCard() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                playTone(440, 60, 0.4f)
                Thread.sleep(20)
                playTone(554, 60, 0.45f)
                Thread.sleep(20)
                playTone(659, 100, 0.5f)
            } catch (e: Exception) {
            }
        }
    }

    fun playJail() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                playTone(220, 140, 0.6f)
                Thread.sleep(40)
                playTone(165, 200, 0.7f)
            } catch (e: Exception) {
            }
        }
    }

    fun playVictory() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val notes = intArrayOf(523, 659, 784, 1046)
                for (note in notes) {
                    playTone(note, 120, 0.6f)
                    Thread.sleep(40)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun playBankrupt() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val notes = intArrayOf(400, 350, 300, 220)
                for (note in notes) {
                    playTone(note, 130, 0.5f)
                    Thread.sleep(30)
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun playTone(frequencyHz: Int, durationMs: Int, volume: Float) {
        val numSamples = (durationMs * SAMPLE_RATE) / 1000
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / SAMPLE_RATE
            val angle = 2.0 * PI * frequencyHz * time
            // اضافه کردن شیب تضعیف برای نرم شدن صدا
            val fade = if (i < numSamples * 0.1) {
                (i / (numSamples * 0.1)).toFloat()
            } else {
                (1.0f - (i - numSamples * 0.1f) / (numSamples * 0.9f))
            }
            val sampleVal = (sin(angle) * Short.MAX_VALUE * volume * fade).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()
        track.setNotificationMarkerPosition(numSamples)
        track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(t: AudioTrack) {
                t.stop()
                t.release()
            }
            override fun onPeriodicNotification(t: AudioTrack) {}
        })
    }
}
