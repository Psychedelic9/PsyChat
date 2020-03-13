package com.bai.psychedelic.psychat.utils

import java.util.regex.Pattern

object PhoneUtils {

    /**
     * 中国手机号码
     */
    private val CHINESE_PHONE_PATTERN = Pattern.compile("((13|15|17|18|19)\\d{9})|(14[57]\\d{8})")


    /**
     * 是否是有效的中国手机号码
     * @param phone
     * @return
     */
    fun isValidChinesePhone(phone: String?): Boolean {
        if (phone == null || phone.length != 11) {
            return false
        }

        val matcher = CHINESE_PHONE_PATTERN.matcher(phone)
        return matcher.matches()
    }


    /**
     * 检查手机是否无效
     * @param phone
     * @return
     */
    fun isNotValidChinesePhone(phone: String): Boolean {
        return !isValidChinesePhone(phone)
    }


    /**
     * 手机中间添加星号
     * @param phone
     * @param beginIndex
     * @param endIndex
     * @return empty string if phone length is illegal
     */
    @JvmOverloads
    fun setAsterisk(phone: String, beginIndex: Int = 3, endIndex: Int = 7): String {

        if ("" == phone) {
            return "手机号不可为空！"
        }

        if (beginIndex < 0 || endIndex < 0 || beginIndex > phone.length || endIndex > phone.length) {
            throw IllegalArgumentException("illegal index $beginIndex,$endIndex")
        }

        val phoneWithAsterisk = StringBuilder(phone.substring(0, beginIndex))

        for (i in beginIndex until endIndex) {
            phoneWithAsterisk.append("*")
        }

        phoneWithAsterisk.append(phone.substring(endIndex, phone.length))
        return phoneWithAsterisk.toString()
    }

    /**
     * 手机中间添加星号,中间六位
     * @param phone
     * @return
     */
    fun setAsterisk2(phone: String): String {
        return setAsterisk(phone, 3, 9)
    }
}
