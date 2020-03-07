package com.bai.psychedelic.psychat.ui.custom

import android.content.Context
import android.os.Handler
import android.os.PowerManager
import android.util.AttributeSet
import android.util.TimeUtils
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.EMError
import kotlin.concurrent.thread

class RecordButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {
    private val mContext = context
    private var mDialogManager: VoiceRecorderDialogManager = VoiceRecorderDialogManager(context)
    private var mCurrentState = STATE_NORMAL

    private val mVoiceRecorder: VoiceRecorder = VoiceRecorder(Handler())
    private var mListener:VoiceRecorderCallback ?= null

    companion object {
        private const val TAG = "RecordButton"
        private const val STATE_NORMAL = 1
        private const val STATE_RECORDING = 2
        private const val STATE_WANT_TO_CANCEL = 3
        private const val DISTANCE_Y_CANCEL = 200
        @Volatile
        private var isRecording = false
    }

    fun setVoiceRecorderCallback(listener:VoiceRecorderCallback){
        mListener = listener
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        val x = event?.x
        val y = event?.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                //TODO:LongClick捕获不到，自定义计时器
                mDialogManager.showRecordingDialog()
                isRecording = true
                MyLog.d(TAG, "OnTouchEvent ACTION_DOWN")
                changeState(STATE_RECORDING)

            }
            MotionEvent.ACTION_MOVE -> {
                MyLog.d(TAG, "OnTouchEvent ACTION_MOVE")

                if (isRecording) {
                    if (wantToCancel(x?.toInt(), y?.toInt())) {
                        changeState(STATE_WANT_TO_CANCEL)
                    } else {
                        changeState(STATE_RECORDING)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                MyLog.d(TAG, "OnTouchEvent ACTION_UP")
                when (mCurrentState) {
                    STATE_RECORDING -> {
                        val recordTime = stopRecording()
                        when {
                            recordTime > 0 -> mListener?.onVoiceRecordComplete(mVoiceRecorder.voiceFilePath!!, recordTime)
                            recordTime == EMError.FILE_INVALID -> Toast.makeText(
                                mContext,
                                mContext.getString(R.string.no_record_permission_please_allow),
                                Toast.LENGTH_LONG
                            ).show()
                            else -> Toast.makeText(
                                mContext,
                                mContext.getString(R.string.record_time_too_short),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        changeState(STATE_NORMAL)
                    }
                    STATE_WANT_TO_CANCEL -> {
                        stopRecording()
                    }
                }
                reset()
            }
        }
        return true
    }


    private fun reset() {
        isRecording = false
        changeState(STATE_NORMAL)
        mDialogManager.dismissDialog()
    }

    private fun wantToCancel(x: Int?, y: Int?): Boolean {
        if (x!! < 0 || x > width) {
            MyLog.d(TAG, "wantToCancel x = $x")
            return true
        }
        if (y!! < -DISTANCE_Y_CANCEL || y > height + DISTANCE_Y_CANCEL) {
            MyLog.d(TAG, "wantToCancel y = $y height = $height")
            return true
        }
        return false
    }


    private fun stopRecording(): Int {
        keepScreenOn = false
        return mVoiceRecorder.stopRecoding()
    }

    private fun startRecording() {
        keepScreenOn = true
        mDialogManager.recording()
        mVoiceRecorder.startRecording(mContext)
        thread(true){
            while (isRecording){
                val level = mVoiceRecorder.getVoiceLevel(7)
                mDialogManager.updateVoiceLevel(level)
                Thread.sleep(100)
            }
        }
    }

    private fun changeState(state: Int) {
        MyLog.d(TAG, "changeCurrentState state= $state")
        if (mCurrentState != state) {
            mCurrentState = state
            when (mCurrentState) {
                STATE_NORMAL -> {
                    setText(R.string.press_speak)
                }
                STATE_RECORDING -> {
                    val voicePlayer = ChatVoicePlayer.get(mContext)
                    if (voicePlayer.isPlaying) {
                        voicePlayer.stop()
                    }
                    setText(R.string.release_finish)
                    if (isRecording) {
                        startRecording()
                    }
                }
                STATE_WANT_TO_CANCEL -> {
                    setText(R.string.up_stroke_cancel)
                    mDialogManager.wantToCancel()
                }

            }
        }
    }
    interface VoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath
         * 录音完毕后的文件路径
         * @param voiceTimeLength
         * 录音时长
         */
        fun onVoiceRecordComplete(voiceFilePath: String, voiceTimeLength: Int)
    }

}