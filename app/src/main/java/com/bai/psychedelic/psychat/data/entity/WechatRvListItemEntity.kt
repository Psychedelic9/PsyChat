package com.bai.psychedelic.psychat.data.entity

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


//Realm 数据类需要定义初始值
open class WechatRvListItemEntity(
    @PrimaryKey var id: Long = 1000,
    var icon: String = "",
    var name: String = "",
    var content: String = "",
    var msgCount: String = "",
    var lastTime: String = "",
    var nickName: String = "",
    var localNickName: String = ""
) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id)
        dest?.writeString(icon)
        dest?.writeString(name)
        dest?.writeString(content)
        dest?.writeString(msgCount)
        dest?.writeString(lastTime)
        dest?.writeString(nickName)
        dest?.writeString(localNickName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WechatRvListItemEntity> {
        override fun createFromParcel(parcel: Parcel): WechatRvListItemEntity {
            return WechatRvListItemEntity(parcel)
        }

        override fun newArray(size: Int): Array<WechatRvListItemEntity?> {
            return arrayOfNulls(size)
        }
    }

}