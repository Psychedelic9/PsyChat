package com.bai.psychedelic.psychat.ui.custom

import java.io.File
import java.io.IOException
import java.util.Date

import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.os.SystemClock
import android.text.format.Time
import com.bai.psychedelic.psychat.utils.MyLog

class VoiceRecorder(private val handler: Handler) {
    internal var recorder: MediaRecorder? = null

    var isRecording = false
        private set
    private var startTime: Long = 0
    var voiceFilePath: String? = null
        private set
    var voiceFileName: String? = null
        private set
    private var file: File? = null

    /**
     * start recording to the file
     */
    fun startRecording(appContext: Context): String? {
        file = null
        try {
            // need to create recorder every time, otherwise, will got exception
            // from setOutputFile when try to reuse
            if (recorder != null) {
                recorder!!.release()
                recorder = null
            }
            recorder = MediaRecorder()
            recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder!!.setAudioChannels(1) // MONO
            recorder!!.setAudioSamplingRate(8000) // 8000Hz
            recorder!!.setAudioEncodingBitRate(64) // seems if change this to
            // 128, still got same file
            // size.
            // one easy way is to use temp file
            // file = File.createTempFile(PREFIX + userId, EXTENSION,
            // User.getVoicePath());
            voiceFileName = getVoiceFileName(EMClient.getInstance().currentUser)
            voiceFilePath = PathUtil.getInstance().voicePath.toString() + "/" + voiceFileName
            file = File(voiceFilePath!!)
            recorder!!.setOutputFile(file!!.absolutePath)
            recorder!!.prepare()
            isRecording = true
            recorder!!.start()
        } catch (e: IOException) {
            EMLog.e("voice", "prepare() failed")
        }

        Thread(Runnable {
            try {
                while (isRecording) {
                    val msg = android.os.Message()
                    msg.what = recorder!!.maxAmplitude * 13 / 0x7FFF
                    handler.sendMessage(msg)
                    SystemClock.sleep(100)
                }
            } catch (e: Exception) {
                // from the crash report website, found one NPE crash from
                // one android 4.0.4 htc phone
                // maybe handler is null for some reason
                EMLog.e("voice", e.toString())
            }
        }).start()
        startTime = Date().time
        EMLog.d("voice", "start voice recording to file:" + file!!.absolutePath)
        return if (file == null) null else file!!.absolutePath
    }

    fun getVoiceLevel(maxLevel:Int):Int{
        return try {
            val level = maxLevel * (recorder?.maxAmplitude)!! / (32768/2) + 1
            MyLog.d("getVoiceLevel","level = $level")
            level
        }catch (e:java.lang.Exception){
            1
        }
    }

    /**
     * stop the recoding
     *
     * @return seconds of the voice recorded
     */

    fun discardRecording() {
        if (recorder != null) {
            try {
                recorder!!.stop()
                recorder!!.release()
                recorder = null
                if (file != null && file!!.exists() && !file!!.isDirectory) {
                    file!!.delete()
                }
            } catch (e: IllegalStateException) {
            } catch (e: RuntimeException) {
            }

            isRecording = false
        }
    }

    fun stopRecoding(): Int {
        if (recorder != null) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null

            if (file == null || !file!!.exists() || !file!!.isFile) {
                return EMError.FILE_INVALID
            }
            if (file!!.length() == 0L) {
                file!!.delete()
                return EMError.FILE_INVALID
            }
            val seconds = (Date().time - startTime).toInt() / 1000
            EMLog.d(
                "voice",
                "voice recording finished. seconds:" + seconds + " file length:" + file!!.length()
            )
            return seconds
        }
        return 0
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        if (recorder != null) {
            recorder!!.release()
        }
    }

    private fun getVoiceFileName(uid: String): String {
        val now = Time()
        now.setToNow()
        return uid + now.toString().substring(0, 15) + EXTENSION
    }

    companion object {

        internal val PREFIX = "voice"
        internal val EXTENSION = ".amr"
    }
}