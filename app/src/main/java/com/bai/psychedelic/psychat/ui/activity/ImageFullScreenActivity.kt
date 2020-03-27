package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.databinding.ActivityImageFullScreenBinding
import com.bai.psychedelic.psychat.utils.PIC_URL_FROM_THUMBNAIL
import com.bai.psychedelic.psychat.utils.StatusBarUtil
import com.bumptech.glide.Glide
import android.transition.TransitionInflater



class ImageFullScreenActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityImageFullScreenBinding
    private lateinit var mContext: Context
    private lateinit var mUrl:String
    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, ImageFullScreenActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隐藏状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_image_full_screen)
        mContext = this
        setStatusBar()
        mUrl = intent.getStringExtra(PIC_URL_FROM_THUMBNAIL)
    }


    override fun onResume() {
        super.onResume()
        Glide.with(mContext).load(mUrl).into(mBinding.activityImageFullScreenIv)
    }

    override fun onPause() {
        super.onPause()
        mBinding.activityImageFullScreenIv.reSetState()
    }

    private fun setStatusBar() {
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true, this)
    }



}
