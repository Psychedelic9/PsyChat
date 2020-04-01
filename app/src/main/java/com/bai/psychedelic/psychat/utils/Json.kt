@file:Suppress("unused")

package com.bai.psychedelic.psychat.utils

import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/* ----------------------------------------------------------------------------------------- */
/* |                                        Json相关                                        | */
/* ----------------------------------------------------------------------------------------- */

/**
 * 对 json 字符串进行格式化
 *
 * @return json 格式化完成字符串
 */
fun String.jsonFormat(): String {
    val json = this.trim()
    return try {
        when {
            json.startsWith("{") -> JSONObject(json).toString(2)
            json.startsWith("[") -> JSONArray(json).toString(2)
            else -> "Invalid Json"
        }
    } catch (e: JSONException) {
        Log.e("NET_INTERCEPTOR", "Json Format Error", e)
        "Invalid Json"
    }
}

/**
 * 从数组、集合 Json 字符串获取数据
 */
fun <T> Gson.fromJsonArray(json: String, clazz: Class<T>): ArrayList<T> {
    val ls = arrayListOf<T>()
    val array = JsonParser().parse(json).asJsonArray
    array.forEach { ls.add(this.fromJson(it, clazz)) }
    return ls
}

/**
 * 将数据对象转换成 Json 字符串
 *
 * @param error 转换异常返回
 */
fun Any?.toJson(error: String = ""): String {
    if (this == null) {
        return error
    }
    return try {
        val json = Gson().toJson(this)
        if (json.isBlank()) {
            error
        } else {
            json
        }
    } catch (e: JsonIOException) {
        error
    }
}

/**
 * 将数据对象转换成 Json 字符串
 * - 禁用 Html 转义
 *
 * @param error 转换异常返回
 */
fun Any?.toJsonWithoutHtmlEscaping(error: String = ""): String {
    if (this == null) {
        return error
    }
    return try {
        val json = GsonBuilder().disableHtmlEscaping().create().toJson(this)
        if (json.isBlank()) {
            error
        } else {
            json
        }
    } catch (e: JsonIOException) {
        error
    }
}

/**
 * 将字符串转换成 数据对象
 * - 转换失败返回 null
 */
fun <T> String?.toEntity(clazz: Class<T>): T? {
    return try {
        Gson().fromJson<T>(this, clazz)
    } catch (e: JsonSyntaxException) {
        null
    }
}

/**
 * 将字符串转换成 数据对象
 * - 转换失败返回 null
 */
fun <T> String?.toEntity(): T? {
    return try {
        Gson().fromJson<T>(this, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
        null
    }
}