package com.bai.psychedelic.psychat.data.entity

import com.hyphenate.chat.EMMessage

open class ChatItemEntity(
    var id:Long = 0,
    var icon:String = "",
    var name:String = "",
    var content:String = "",
    var type:Int = 0,
    var sendTime:String = "",
    var height:Int = 0,
    var width:Int = 0,
    var voiceLength:Int = 0,
    var voiceMessage:EMMessage?= null
)
{

}