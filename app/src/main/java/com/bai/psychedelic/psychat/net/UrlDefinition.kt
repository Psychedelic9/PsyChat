package com.arcb.laborpos.net

/**
 * 网络接口地址
 */
object UrlDefinition {

    /** 正式环境  */
    private const val API_STABLE = "https://www.wanandroid.com"
    /** 测试环境  */
    private const val API_DEV = "http://36.7.109.116:9527/"
    //注意：BaseUrl必须以com/cn/端口号或者“/”结尾，否则会报错

//    private const val API_DEV = "http://192.168.1.127:8080"
//    private const val API_DEV = "http://localhost:8080"

    /** 服务器url  */
    @Suppress("ConstantConditionIf")
    val BASE_URL = (if (true) API_DEV else API_STABLE)
    /**提交交易信息*/
    const val TRANS_RESULT = "tradeunion/app/addDeal.action"
    /** 获取活动信息 */
    const val GET_ACTIVITY_INFO = "tradeunion/app/findActivity.action"
    /** 获取所属行信息 */
    const val GET_BANK_BIN = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json"
    /** 获取工会卡BIN */
    const val GET_CARD_BIN = "tradeunion/app/findCardBin.action"
    /** 退款返回记录接口 */
    const val REFUND_TRANS = "tradeunion/app/refundDeal.action"

    const val GET_VERSION = "tradeunion/app/findVersion.action"

    /** 新版APK下载接口*/
    const val DOWNLOAD_URL = "http://36.7.109.116:9527/tradeunion/app/downloadNewVersion.action?versionType=2"
}
