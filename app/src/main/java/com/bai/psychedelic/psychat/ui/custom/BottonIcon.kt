package com.bai.psychedelic.psychat.ui.custom

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bai.psychedelic.psychat.R

class BottonIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private val mContext: Context = context
    private var bottomIcon: Int = 0
    private var bottomIconSelect: Int = 0
    private var bottomString: String? = null
    private var mView: View? = null
    private var mImageView: ImageView? = null
    private var mImageViewSelect: ImageView? = null
    private var mTextView: TextView? = null
    private val COLORDEFULT:Int = Color.parseColor("#ff000000")
    private val COLORSELECT:Int = Color.parseColor("#ff45c01a")
    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BottomIcon)
        bottomIcon = typedArray.getResourceId(R.styleable.BottomIcon_bottom_image, R.drawable.icon_chat_user)
        bottomIconSelect =
            typedArray.getResourceId(R.styleable.BottomIcon_bottom_image_select, R.drawable.icon_chat_user)
        bottomString = typedArray.getString(R.styleable.BottomIcon_bottom_text)
        typedArray.recycle()
        mView = LayoutInflater.from(context).inflate(R.layout.bottom_icon, this, false)
        mImageView = mView?.findViewById(R.id.bottom_layout_icon)
        mImageViewSelect = mView?.findViewById(R.id.bottom_layout_icon_select)
        mTextView = mView?.findViewById(R.id.bottom_layout_text)
        mImageView?.setImageResource(bottomIcon)
        mImageViewSelect?.setImageResource(bottomIconSelect)
        mTextView?.text = bottomString
        setProgress(0f)
        addView(mView)
    }

     fun setProgress(progress: Float) {
        mImageView?.alpha = (1 - progress)
        mImageViewSelect?.alpha = (progress)
        mTextView?.setTextColor(ArgbEvaluator().evaluate(progress,COLORDEFULT,COLORSELECT)as Int)
    }
}