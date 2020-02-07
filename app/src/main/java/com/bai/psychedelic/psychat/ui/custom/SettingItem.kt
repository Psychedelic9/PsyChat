package com.bai.psychedelic.psychat.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bai.psychedelic.psychat.R

class SettingItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mView: View? = null
    private var mImageView: ImageView? = null
    private var mTextView: TextView? = null
    private var mIcon: Int = 0
    private var mText: String? = null

    init {
        mView = LayoutInflater.from(context).inflate(R.layout.setting_item_layout, this, false)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SettingItem)
        mIcon = typedArray.getResourceId(
            R.styleable.SettingItem_setting_icon,R.mipmap.ic_launcher
        )
        mText = typedArray.getString(R.styleable.SettingItem_setting_text)
        typedArray.recycle()
        mImageView = mView?.findViewById(R.id.setting_item_iv_icon)
        mTextView = mView?.findViewById(R.id.setting_item_tv_name)
        mImageView?.setImageResource(mIcon)
        mTextView?.text = mText
        addView(mView)

    }
}