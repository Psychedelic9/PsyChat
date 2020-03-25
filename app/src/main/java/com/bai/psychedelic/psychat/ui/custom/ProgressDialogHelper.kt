package com.bai.psychedelic.psychat.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.databinding.AppDialogProgressBinding
import com.bai.psychedelic.psychat.utils.MyLog


/**
 * 加载中弹窗，永远只显示一个
 */
object ProgressDialogHelper {

    /** Dialog 弹窗对象 */
    private var dialog: Dialog? = null

    /**
     * 显示弹窗
     *
     * @param context 要显示的界面 [Context] 对象
     * @param cancelable 能否取消 默认为 **false** 不可以取消
     */
    fun show(context: AppCompatActivity, cancelable: Boolean = false) {
        // 显示前先隐藏
        dismiss()
        // 加载布局、DataBinding对象
        val binding = DataBindingUtil.inflate<AppDialogProgressBinding>(
                LayoutInflater.from(context),
                R.layout.app_dialog_progress, null, false
        )
        binding.progressImage.setImageResource(R.drawable.loading_animlist)
        val animationDrawable = binding.progressImage.drawable as AnimationDrawable
        animationDrawable.start()
        // 初始化 Dialog
        dialog = Dialog(context, R.style.app_progress_dialog)
        // 设置能否取消
        dialog!!.setCancelable(cancelable)
        // 设置点击弹窗外不能取消
        dialog!!.setCanceledOnTouchOutside(false)
        // 设置弹窗布局
        dialog!!.setContentView(binding.root)
        // 显示
        dialog!!.show()
    }

    /**
     * 隐藏弹窗
     */
    fun dismiss() {
        try {
            // 隐藏弹窗
            dialog?.dismiss()
            // 移除引用
            dialog = null
        } catch (e: Exception) {
            MyLog.e(e.printStackTrace().toString(), "PROGRESS_DIALOG_DISMISS_ERROR")
        }
    }

}