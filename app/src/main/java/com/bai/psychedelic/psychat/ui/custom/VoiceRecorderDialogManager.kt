package com.bai.psychedelic.psychat.ui.custom

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.databinding.DialogRecorderBinding

class VoiceRecorderDialogManager(context: Context) {
    private val mContext = context
    private var mDialog: Dialog = Dialog(mContext, R.style.Theme_Audio_Dialog)
    private lateinit var mBinding: DialogRecorderBinding

    fun showRecordingDialog() {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.dialog_recorder,
            null,
            false
        )
        mDialog.setContentView(mBinding.root)
        mDialog.show()
    }

    fun recording(){
        if (mDialog.isShowing){
            mBinding.dialogRecorderIvRecordIcon.visibility = View.VISIBLE
                    mBinding.dialogRecorderIvVoiceLevel.visibility = View.VISIBLE
            mBinding.dialogRecorderTvLabel.visibility = View.VISIBLE

            mBinding.dialogRecorderIvRecordIcon.setImageResource(R.drawable.recorder)
            mBinding.dialogRecorderTvLabel.text = mContext.resources.getString(R.string.finger_up_to_cancel_send)
        }
    }

    fun wantToCancel() {
        if (mDialog.isShowing){
            mBinding.dialogRecorderIvRecordIcon.visibility = View.VISIBLE
            mBinding.dialogRecorderIvVoiceLevel.visibility = View.GONE
            mBinding.dialogRecorderTvLabel.visibility = View.VISIBLE

            mBinding.dialogRecorderIvRecordIcon.setImageResource(R.drawable.cancel)
            mBinding.dialogRecorderTvLabel.text = mContext.getString(R.string.loosen_fingers_cancel_send)
        }
    }

    fun tooShort() {
        if (mDialog.isShowing){
            mBinding.dialogRecorderIvRecordIcon.visibility = View.VISIBLE
            mBinding.dialogRecorderIvVoiceLevel.visibility = View.GONE
            mBinding.dialogRecorderTvLabel.visibility = View.VISIBLE

            mBinding.dialogRecorderIvRecordIcon.setImageResource(R.drawable.voice_to_short)
            mBinding.dialogRecorderTvLabel.text = mContext.getString(R.string.record_time_too_short)
        }
    }

    fun dismissDialog() {
        if (mDialog.isShowing){
            mDialog.dismiss()
        }
    }

    fun updateVoiceLevel(level: Int) {
        (mContext as AppCompatActivity).runOnUiThread {
            if (mDialog.isShowing && mBinding.dialogRecorderIvVoiceLevel.visibility == View.VISIBLE){
                mBinding.dialogRecorderIvRecordIcon.visibility = View.VISIBLE
                mBinding.dialogRecorderIvVoiceLevel.visibility = View.VISIBLE
                mBinding.dialogRecorderTvLabel.visibility = View.VISIBLE
                val resId = mContext.resources.getIdentifier("v$level","drawable",mContext.packageName)
                mBinding.dialogRecorderIvVoiceLevel.setImageResource(resId)
            }
        }

    }

}