package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.ui.adapter.MainPagerViewAdapter
import com.bai.psychedelic.psychat.data.viewmodel.MainViewModel
import com.bai.psychedelic.psychat.databinding.ActivityMainBinding
import com.bai.psychedelic.psychat.observer.lifecycleObserver.MainActivityObserver
import com.bai.psychedelic.psychat.ui.custom.BottonIcon
import com.bai.psychedelic.psychat.utils.*
import com.hyphenate.chat.EMClient
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mContext:Context
    private lateinit var mBinding:ActivityMainBinding
    private var mTabs = ArrayList<BottonIcon>()
    private val mEMClient: EMClient by inject()
    private lateinit var mLifecycleObserver:MainActivityObserver
    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mContext = this
        val viewModel: MainViewModel by viewModel()
        mBinding.model = viewModel

        mLifecycleObserver = MainActivityObserver(this)
        lifecycle.addObserver(mLifecycleObserver)
        setStatusBar()
        setTabs()
    }

    private fun setTabs() {
        mTabs.add(mBinding.mainViewIncludeBottom.bottonIconWechat)
        mTabs.add(mBinding.mainViewIncludeBottom.bottonIconContactList)
        mTabs.add(mBinding.mainViewIncludeBottom.bottonIconFind)
        mTabs.add(mBinding.mainViewIncludeBottom.bottonIconMe)
        mTabs[0].setProgress(1f)
        mBinding.mainViewPager.adapter =
            MainPagerViewAdapter(supportFragmentManager)
        mBinding.mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (positionOffset > 0) {
                    val left = mTabs[position]
                    val right = mTabs[position + 1]
                    left.setProgress(1 - positionOffset)
                    right.setProgress(positionOffset)
                }

            }

            override fun onPageSelected(position: Int) {
                var title: String? = null
                when (position) {
                    0 -> {
                        title = "微信"
                    }
                    1 -> {
                        title = "通讯录"
                    }
                    2 -> {
                        title = "发现"
                    }
                    3 -> {
                        title = "我"
                    }
                }
                mBinding.mainViewIncludeTop.txtTitle.text = title
                for ((index2, tab2) in mTabs.withIndex()) {
                    if (index2 == position) {
                        tab2.setProgress(1f)
                    } else {
                        tab2.setProgress(0f)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        for ((index, tab) in mTabs.withIndex()) {
            tab.setOnClickListener {
                mBinding.mainViewPager.setCurrentItem(index, false)
            }
        }
    }

    override fun onBackPressed() {
        //返回后台，不结束App
        moveTaskToBack(true)
    }
    private fun setStatusBar() {
        val resources = resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height = resources.getDimensionPixelSize(resourceId)
        val statusBarFill = mBinding.mainViewIncludeTop.statusBarFill
        val layoutParams = statusBarFill.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = height
        statusBarFill.layoutParams = layoutParams
        window.statusBarColor = resources.getColor(R.color.bar_color)
        StatusBarUtil.setStatusTextColor(true, this)
    }

    fun onTitleButtonAddClick(view: View) {
        AddFriendsActivity.actionStart(mContext)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       if (requestCode == REQUEST_PERMISSIONS){
           for ((index,permission) in permissions.withIndex()){
               if (grantResults[index] != PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(mContext,"不获取 $permission 程序无法正常运行，请授权",Toast.LENGTH_LONG).show()
               }
           }
       }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
