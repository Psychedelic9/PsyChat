package com.bai.psychedelic.psychat.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.bai.psychedelic.psychat.R
import com.bai.psychedelic.psychat.data.viewmodel.AddFriendsViewModel
import com.bai.psychedelic.psychat.databinding.ActivityAddFriendsBinding
import org.koin.android.viewmodel.ext.android.viewModel

class AddFriendsActivity : AppCompatActivity() {
    private val mViewModel:AddFriendsViewModel by viewModel()
    private lateinit var mBinding:ActivityAddFriendsBinding
    private lateinit var mContext:Context
    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, AddFriendsActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_friends)
        mBinding.model = mViewModel
        setListener()
    }

    private fun setListener(){
        mBinding.activityAddFriendEtSearch.addTextChangedListener {
            if (it.toString().trim() == ""){
                mBinding.activityAddFriendClConfirmSearch.visibility = View.INVISIBLE
                mBinding.activityAddFriendTvSearchContent.text = ""

            }else{
                mBinding.activityAddFriendClConfirmSearch.visibility = View.VISIBLE
                mBinding.activityAddFriendTvSearchContent.text = it.toString()
            }
        }

    }

    fun onSearchCancelClick(view: View) {
        onBackPressed()
    }

    fun searchConfirmClick(view: View) {}

}
