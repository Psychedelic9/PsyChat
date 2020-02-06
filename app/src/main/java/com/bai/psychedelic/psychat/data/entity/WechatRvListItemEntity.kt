package com.bai.psychedelic.psychat.data.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
//Realm 数据类需要定义初始值
open class WechatRvListItemEntity(
    @PrimaryKey var id: Long = 1000,
    var icon: String = "",
    var name: String = "",
    var content: String = "",
    var msgCount: String = "",
    var lastTime: String = ""
):RealmObject(){

}