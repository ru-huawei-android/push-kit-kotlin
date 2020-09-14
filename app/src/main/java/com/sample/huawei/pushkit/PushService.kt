package com.sample.huawei.pushkit

import android.content.Intent
import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage


class PushService: HmsMessageService() {

    private val TAG: String = this@PushService.javaClass.simpleName

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken() called ")
        sendToken(token)
    }

    private fun sendToken(token: String?) {
        val intent = Intent("GET_HMS_TOKEN_ACTION")
        intent.putExtra("token", token)
        sendBroadcast(intent)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        Log.i(TAG, "onMessageReceived() data: ")
        message!!.dataOfMap.let {
            for (data in it) {
                Log.i(TAG, "key: ${data.key} - value: ${data.value};")
            }
        }
    }

}