package com.example.toyota

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.*
import android.media.audiofx.BassBoost
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MicToSpeakerActivity : AppCompatActivity() {
    //Audio
    private var mOn: ImageButton? = null
    private var isOn = false
    private var isRecording = false
    private var record: AudioRecord? = null
    private var player: AudioTrack? = null
    private var manager: AudioManager? = null
    private var recordState = 0
    private var playerState = 0
    private var minBuffer = 0

    //Audio Settings
    private val source = MediaRecorder.AudioSource.CAMCORDER
    private val channel_in = AudioFormat.CHANNEL_IN_MONO
    private val channel_out = AudioFormat.CHANNEL_OUT_MONO
    private val format = AudioFormat.ENCODING_PCM_16BIT
    private var IS_HEADPHONE_AVAILBLE = true
    private val MY_PERMISSIONS_RECORD_AUDIO = 1

    @RequiresApi(api =  Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.toyota.R.layout.activity_mictospeaker)

        val ButtonP = findViewById<ImageButton>(com.example.toyota.R.id.ptoMenu)
        // set on-click listener

        ButtonP.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        //Reduce latancy
        mOn = findViewById(com.example.toyota.R.id.button)
        volumeControlStream = AudioManager.MODE_IN_COMMUNICATION
        isOn = false
        isRecording = false
        manager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        manager!!.mode = AudioManager.MODE_IN_COMMUNICATION

        //Check for headset availability
        val audioDevices = manager!!.getDevices(AudioManager.GET_DEVICES_INPUTS)
        for (deviceInfo in audioDevices) {
            if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || deviceInfo.type == AudioDeviceInfo.TYPE_USB_HEADSET) {
                IS_HEADPHONE_AVAILBLE = true
            }
        }
//        if (!IS_HEADPHONE_AVAILBLE) {
//            // get delete_audio_dialog.xml view
//            val layoutInflater = LayoutInflater.from(this@MicToSpeakerActivity)
////            val promptView: View = layoutInflater.inflate(com.example.toyota.R.layout.settings_activity, null)
//            val alertDialogBuilder: AlertDialog.Builder =
//                AlertDialog.Builder(this@MicToSpeakerActivity)
//            alertDialogBuilder.setView(promptView)
//            // setup a dialog window
//            alertDialogBuilder.setCancelable(false)
//                .setPositiveButton(
//                    "Try Again"
//                ) { _, _ -> startActivity(Intent(intent)) }
//                .setNegativeButton(
//                    "Cancel"
//                ) { dialog, _ ->
//                    startActivity(Intent(this@MicToSpeakerActivity, MenuActivity::class.java))
//                    dialog.cancel()
//                }
//            // create an alert dialog
//            val alert = alertDialogBuilder.create()
//            alert.show()
//        }
        initAudio()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun initAudio() {
        //Tests all sample rates before selecting one that works
        val sample_rate = sampleRate
        minBuffer = AudioRecord.getMinBufferSize(sample_rate, channel_in, format)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG)
                    .show()

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            }
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            == PackageManager.PERMISSION_GRANTED
        ){
            mOn!!.setOnTouchListener { _, motionEvent ->
                isOn = !isOn
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        object : Thread() {
                            override fun run() {
                                startAudio()
                            }
                        }.start()
                    }
                    MotionEvent.ACTION_UP -> {
                        endAudio()
                    }
                }
                false
            }
        }
        record = AudioRecord(source, sample_rate, channel_in, format, minBuffer)
        recordState = record!!.state
        val id = record!!.audioSessionId
        Log.d("Record", "ID: $id")
        playerState = 0
        player = AudioTrack(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
            AudioFormat.Builder().setEncoding(format).setSampleRate(sample_rate)
                .setChannelMask(channel_out).build(),
            minBuffer,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        playerState = player!!.state
        // Formatting Audio
//        if (AcousticEchoCanceler.isAvailable()) {
//            val echo = AcousticEchoCanceler.create(id)
//            echo.enabled = true
//            Log.d("Echo", "Off")
//        }
//        if (NoiseSuppressor.isAvailable()) {
//            val noise = NoiseSuppressor.create(id)
//            noise.enabled = true
//            Log.d("Noise", "Off")
//        }
//        if (AutomaticGainControl.isAvailable()) {
//            val gain = AutomaticGainControl.create(id)
//            gain.enabled = false
//            Log.d("Gain", "Off")
//        }
        val base = BassBoost(1, player!!.audioSessionId)
        base.setStrength(1000.toShort())
    }

    fun startAudio() {
        var read: Int
        var write: Int
        if (recordState == AudioRecord.STATE_INITIALIZED && playerState == AudioTrack.STATE_INITIALIZED) {
            record!!.startRecording()
            player!!.play()
            isRecording = true
            Log.d("Record", "Recording...")
        }
        while (isRecording) {
            val audioData = ShortArray(minBuffer)
            read = if (record != null) record!!.read(audioData, 0, minBuffer) else break
            Log.d("Record", "Read: $read")
            write = if (player != null) player!!.write(audioData, 0, read) else break
            Log.d("Record", "Write: $write")
        }
    }

    private fun endAudio() {
        if (record != null) {
            if (record!!.recordingState == AudioRecord.RECORDSTATE_RECORDING) record!!.stop()
            isRecording = false
            Log.d("Record", "Stopping...")
        }
        if (player != null) {
            if (player!!.playState == AudioTrack.PLAYSTATE_PLAYING) player!!.stop()
            isRecording = false
            Log.d("Player", "Stopping...")
        }
    }

    //Find a sample rate that works with the device
    private val sampleRate: Int
        get() {
            //Find a sample rate that works with the device
            for (rate in intArrayOf(8000, 11025, 16000, 22050, 44100, 48000)) {
                val buffer = AudioRecord.getMinBufferSize(rate, channel_in, format)
                if (buffer > 0) return rate
            }
            return -1
        }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }
}
