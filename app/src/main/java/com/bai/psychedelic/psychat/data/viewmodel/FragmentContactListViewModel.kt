package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ContactListItemEntity
import com.hyphenate.chat.EMClient
import org.koin.core.KoinComponent
import org.koin.core.inject

class FragmentContactListViewModel : ViewModel(),KoinComponent {
    private val mEMClient:EMClient by inject()
    fun getContactList():ArrayList<ContactListItemEntity>{
        var list = ArrayList<ContactListItemEntity>()
        
        return list
    }
}
