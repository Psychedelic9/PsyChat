package com.bai.psychedelic.psychat.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bai.psychedelic.psychat.ui.fragment.ContactListFragment
import com.bai.psychedelic.psychat.ui.fragment.FindFragment
import com.bai.psychedelic.psychat.ui.fragment.MeFragment
import com.bai.psychedelic.psychat.ui.fragment.WeChatFragment
import kotlin.collections.ArrayList

class MainPagerViewAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fragments = ArrayList<Fragment>()
    private val mTitles = ArrayList<String>(listOf("微信","通讯录","发现","我"))
    init {
        fragments.clear()
        fragments.add(WeChatFragment())
        fragments.add(ContactListFragment())
        fragments.add(FindFragment())
        fragments.add(MeFragment())
    }
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return mTitles.size
    }
}