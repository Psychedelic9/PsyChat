package com.bai.psychedelic.psychat.database

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


/**
 *@Author: yiqing
 *@CreateDate: 2020/2/9 16:37
 *@UpdateDate: 2020/2/9 16:37
 *@Description:
 *@ClassName: MyMigration
 */
class MyMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        if (oldVersion == 0L){
            val wechatRvListItemSchema = schema["WechatRvListItemEntity"]
            wechatRvListItemSchema?.addField("nickName",String::class.java, FieldAttribute.REQUIRED)
            wechatRvListItemSchema?.addField("localNickName",String::class.java, FieldAttribute.REQUIRED)
            oldVersion.inc()
        }

    }
}