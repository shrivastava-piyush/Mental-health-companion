package com.wellness.companion.ui.atmosphere

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.wellness.companion.ui.MoodCategory
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AtmosphereManager(private val context: Context) {
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var volume = 0.4f
    private var baseFrequencies = floatArrayOf(110.0f, 164.81f, 220.0f, 329.63f)
    private var noiseAlpha = 0.015f

    fun start() {
        if (job?.isActive == true) return
        
        job = scope.launch {
            val sampleRate = 44100
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_FLOAT
            )
            
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
                .setAudioFormat(AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack.play()
            
            val floatBuffer = FloatArray(bufferSize / 4)
            var phases = FloatArray(baseFrequencies.size) { 0f }
            var lowPassState = 0f
            var globalPhase = 0f

            while (isActive) {
                for (i in floatBuffer.indices) {
                    val white = Random.nextFloat() * 2f - 1f
                    lowPassState = lowPassState + noiseAlpha * (white - lowPassState)
                    var sample = lowPassState * 0.02f
                    
                    for (j in baseFrequencies.indices) {
                        val lfo = sin(j.toFloat() + globalPhase * 0.00001f) * 0.4f + 0.6f
                        sample += sin(phases[j]) * (0.012f / baseFrequencies.size) * lfo
                        phases[j] += (2.0f * PI.toFloat() * baseFrequencies[j]) / sampleRate
                        if (phases[j] > 2.0f * PI.toFloat()) phases[j] -= 2.0f * PI.toFloat()
                    }
                    
                    floatBuffer[i] = sample * volume
                    globalPhase += 1.0f
                }
                audioTrack.write(floatBuffer, 0, floatBuffer.size, AudioTrack.WRITE_BLOCKING)
            }
            
            audioTrack.stop()
            audioTrack.release()
        }
    }

    fun adaptTo(category: MoodCategory) {
        when (category) {
            MoodCategory.POSITIVE -> {
                baseFrequencies = floatArrayOf(220.0f, 329.63f, 440.0f, 659.25f)
                noiseAlpha = 0.01f
            }
            MoodCategory.NEGATIVE -> {
                baseFrequencies = floatArrayOf(87.31f, 130.81f, 174.61f, 261.63f)
                noiseAlpha = 0.025f
            }
            MoodCategory.NEUTRAL -> {
                baseFrequencies = floatArrayOf(110.0f, 164.81f, 220.0f, 329.63f)
                noiseAlpha = 0.015f
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
