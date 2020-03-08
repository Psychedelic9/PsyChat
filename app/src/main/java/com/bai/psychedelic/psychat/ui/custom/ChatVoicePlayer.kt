package com.bai.psychedelic.psychat.ui.custom

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMVoiceMessageBody

import java.io.IOException

/**
 * Created by zhangsong on 17-10-20.
 */

class ChatVoicePlayer private constructor(cxt: Context) {
    companion object {
        private const val TAG = "ChatVoicePlayer"

        private var instance: ChatVoicePlayer? = null

        fun get(context:Context):ChatVoicePlayer{
            if (instance == null){
                synchronized(ChatVoicePlayer::class.java){
                    if (instance == null){
                        instance = ChatVoicePlayer(context)
                    }
                }
            }
            return instance!!
        }
    }
    private val audioManager: AudioManager
    val player: MediaPlayer
    /**
     * May null, please consider.
     *
     * @return
     */
    var currentPlayingId: String? = null
        private set

    private var onCompletionListener: MediaPlayer.OnCompletionListener? = null

    val isPlaying: Boolean
        get() = player.isPlaying

    fun play(msg: EMMessage, listener: MediaPlayer.OnCompletionListener) {
        if (msg.body !is EMVoiceMessageBody) return
        if (player.isPlaying) {
            stop()
        }

        currentPlayingId = msg.msgId
        onCompletionListener = listener

        try {
            setSpeaker()
            val voiceBody = msg.body as EMVoiceMessageBody
            player.setDataSource(voiceBody.localUrl)
            player.prepare()
            player.setOnCompletionListener {
                stop()
                currentPlayingId = null
                onCompletionListener = null
            }
            player.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun stop() {
        player.stop()
        player.reset()

        /**
         * This listener is to stop the voice play animation currently, considered the following 3 conditions:
         *
         * 1.A new voice item is clicked to play, to stop the previous playing voice item animation.
         * 2.The voice is play complete, to stop it's voice play animation.
         * 3.Press the voice record button will stop the voice play and must stop voice play animation.
         *
         */
        if (onCompletionListener != null) {
            onCompletionListener!!.onCompletion(player)
        }
    }

    init {
        val baseContext = cxt.applicationContext
        audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        player = MediaPlayer()
    }

    private fun setSpeaker() {
        //TODO:Setting中是否打开麦克风
        if (true) {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = true
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        } else {
            audioManager.isSpeakerphoneOn = false// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.mode = AudioManager.MODE_IN_CALL
            player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
        }
    }


}
