package com.bai.psychedelic.psychat.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bai.psychedelic.psychat.data.entity.WechatRvListItemEntity
import java.util.*
import kotlin.collections.ArrayList

class FragmentWeChatViewModel : ViewModel() {
    fun getChatListFromDB(): ArrayList<WechatRvListItemEntity> {
        var list = ArrayList<WechatRvListItemEntity>()
        for (index in 1..10) {
            var entity = WechatRvListItemEntity(
                id = Random().nextLong(),
                name = "13955555555",
                content = "中午什么时候吃饭",
                msgCount = "3",
                lastTime = "上午：9:23",
                icon = "123",
                nickName = "鸣鸣"
            )
            list.add(entity)
        }
        return list

    }
}
