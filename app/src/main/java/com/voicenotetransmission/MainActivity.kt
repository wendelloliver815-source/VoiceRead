package com.voicenotetransmission

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

class MainActivity : ComponentActivity() {

    private var recorder: MediaRecorder? = null
    private lateinit var audioFile: File

    private val microphonePermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startRecording()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            startRecording()
                        } else {
                            microphonePermission.launch(
                                Manifest.permission.RECORD_AUDIO
                            )
                        }
                    }
                ) {
                    Text("Start Recording")
                }

                Button(
                    onClick = {
                        stopRecording()
                    }
                ) {
                    Text("Stop Recording")
                }
            }
        }
    }

    private fun startRecording() {
        audioFile = File(
            externalCacheDir,
            "voice_note_${System.currentTimeMillis()}.m4a"
        )

        recorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }

        recorder = null
    }

    override fun onDestroy() {
        recorder?.release()
        recorder = null
        super.onDestroy()
    }
}
