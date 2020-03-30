package com.bai.psychedelic.psychat.service

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.huawei.hms.push.SendException
import com.hyphenate.chat.EMClient
import java.util.*

class HWPushService : HmsMessageService() {
    private val TAG = "PushDemoLog"
    private val CODELABS_ACTION = "com.huawei.codelabpush.action"

    /**
     * When an app calls the getToken method to apply for a token from the server,
     * if the server does not return the token during current method calling, the server can return the token through this method later.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     * @param token token
     */
    override fun onNewToken(token: String?) {
        Log.i(TAG, "received refresh token:" + token!!)
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
            refreshedTokenToServer(token)
        }

        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onNewToken")
        intent.putExtra("msg", "onNewToken called, token: $token")

        sendBroadcast(intent)
    }

    private fun refreshedTokenToServer(token: String) {
        Log.i(TAG, "sending token to server. token:$token")
        EMClient.getInstance().sendHMSPushTokenToServer(token)
    }

    /**
     * This method is used to receive downstream data messages.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     *
     * @param message RemoteMessage
     */
    override fun onMessageReceived(message: RemoteMessage?) {
        Log.i(TAG, "onMessageReceived is called")
        if (message == null) {
            Log.e(TAG, "Received message entity is null!")
            return
        }
        // getCollapseKey() Obtains the classification identifier (collapse key) of a message.
        // getData() Obtains valid content data of a message.
        // getMessageId() Obtains the ID of a message.
        // getMessageType() Obtains the type of a message.
        // getNotification() Obtains the notification data instance from a message.
        // getOriginalUrgency() Obtains the original priority of a message.
        // getSentTime() Obtains the time when a message is sent from the server.
        // getTo() Obtains the recipient of a message.

        Log.i(
            TAG, "getCollapseKey: " + message.collapseKey
                    + "\n getData: " + message.data
                    + "\n getFrom: " + message.from
                    + "\n getTo: " + message.to
                    + "\n getMessageId: " + message.messageId
                    + "\n getOriginalUrgency: " + message.originalUrgency
                    + "\n getUrgency: " + message.urgency
                    + "\n getSendTime: " + message.sentTime
                    + "\n getMessageType: " + message.messageType
                    + "\n getTtl: " + message.ttl
        )

        // getBody() Obtains the displayed content of a message
        // getTitle() Obtains the title of a message
        // getTitleLocalizationKey() Obtains the key of the displayed title of a notification message
        // getTitleLocalizationArgs() Obtains variable parameters of the displayed title of a message
        // getBodyLocalizationKey() Obtains the key of the displayed content of a message
        // getBodyLocalizationArgs() Obtains variable parameters of the displayed content of a message
        // getIcon() Obtains icons from a message
        // getSound() Obtains the sound from a message
        // getTag() Obtains the tag from a message for message overwriting
        // getColor() Obtains the colors of icons in a message
        // getClickAction() Obtains actions triggered by message tapping
        // getChannelId() Obtains IDs of channels that support the display of messages
        // getImageUrl() Obtains the image URL from a message
        // getLink() Obtains the URL to be accessed from a message
        // getNotifyId() Obtains the unique ID of a message

        val notification = message.notification
        if (notification != null) {
            Log.i(
                TAG, "\n getImageUrl: " + notification.imageUrl
                        + "\n getTitle: " + notification.title
                        + "\n getTitleLocalizationKey: " + notification.titleLocalizationKey
                        + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.titleLocalizationArgs)
                        + "\n getBody: " + notification.body
                        + "\n getBodyLocalizationKey: " + notification.bodyLocalizationKey
                        + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.bodyLocalizationArgs)
                        + "\n getIcon: " + notification.icon
                        + "\n getSound: " + notification.sound
                        + "\n getTag: " + notification.tag
                        + "\n getColor: " + notification.color
                        + "\n getClickAction: " + notification.clickAction
                        + "\n getChannelId: " + notification.channelId
                        + "\n getLink: " + notification.link
                        + "\n getNotifyId: " + notification.notifyId
            )
        }

        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onMessageReceived")
        intent.putExtra(
            "msg", "onMessageReceived called, message id:" + message.messageId + ", payload data:"
                    + message.data
        )

        sendBroadcast(intent)

        val judgeWhetherIn10s = false

        // If the messages are not processed in 10 seconds, the app needs to use WorkManager for processing.
        if (judgeWhetherIn10s) {
            startWorkManagerJob(message)
        } else {
            // Process message within 10s
            processWithin10s(message)
        }
    }

    private fun startWorkManagerJob(message: RemoteMessage?) {
        Log.d(TAG, "Start new Job processing.")
    }

    private fun processWithin10s(message: RemoteMessage) {
        Log.d(TAG, "Processing now.")
    }

    override fun onMessageSent(msgId: String?) {
        Log.i(TAG, "onMessageSent called, Message id:" + msgId!!)
        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onMessageSent")
        intent.putExtra("msg", "onMessageSent called, Message id:$msgId")

        sendBroadcast(intent)
    }

    override fun onSendError(msgId: String?, exception: Exception) {
        Log.i(
            TAG, "onSendError called, message id:" + msgId + ", ErrCode:"
                    + (exception as SendException).errorCode + ", description:" + exception.message
        )

        val intent = Intent()
        intent.action = CODELABS_ACTION
        intent.putExtra("method", "onSendError")
        intent.putExtra(
            "msg", "onSendError called, message id:" + msgId + ", ErrCode:"
                    + exception.errorCode + ", description:" + exception.message
        )

        sendBroadcast(intent)
    }
}