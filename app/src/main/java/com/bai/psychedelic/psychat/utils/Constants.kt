package com.bai.psychedelic.psychat.utils

/** OkHttp 网络请求超时时间 */
const val NET_CONNECT_TIME_OUT = 10L
const val NET_WRITE_TIME_OUT = 30L
const val NET_READ_TIME_OUT = 30L

/** Chat item type 聊天item类型*/
const val CHAT_TYPE_SEND_TXT = 1
const val CHAT_TYPE_GET_TXT = 2
const val CHAT_TYPE_GET_IMAGE = 3
const val CHAT_TYPE_SEND_IMAGE = 4

/** 动态运行权限*/

const val REQUEST_PERMISSIONS = 777

/** Activity result code*/
const val START_ACTIVITY_IMAGE = 999 //打开图库


/** 网络请求缓存大小 100M */
const val NET_CACHE_SIZE = 1024L * 1024L * 100L
/** 网络请求结果码 */
const val NET_RESPONSE_CODE_SUCCESS = 0

/** SP key - 是否登录 */
const val SP_IS_LOGIN = "sp_is_login"
/** SP key - 用户信息 */
const val SP_USER = "sp_user"
const val NOT_LOGIN = "未登录"

const val SP_SPLASH_TEXT = "sp_splash_text"
const val SP_KEYBOARD_HEIGHT = "sp_keyboard_height"

/** 会话ID */
const val CONVERSATION_USER_ID = "conversation_user_id"