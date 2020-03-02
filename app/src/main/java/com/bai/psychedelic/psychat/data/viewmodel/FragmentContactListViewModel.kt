package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.ContactListItemEntity
import com.bai.psychedelic.psychat.utils.MyLog
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.Exception

class FragmentContactListViewModel : ViewModel(), KoinComponent {
    private val mEMClient: EMClient by inject()
    fun getContactList(callback:EMValueCallBack<List<String>>){
        mEMClient.contactManager().aysncGetAllContactsFromServer(callback)
    }
    fun getContactList(): ArrayList<ContactListItemEntity>{
        var list =  ArrayList<ContactListItemEntity>()
        try {
            val contactList = mEMClient.contactManager().allContactsFromServer
            MyLog.d("getContactList","contactList ${contactList.size}")
            for (user in contactList){
                list.add(ContactListItemEntity().apply {
                    id = user
                    icon = ""//TODO
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        return list
    }
}
