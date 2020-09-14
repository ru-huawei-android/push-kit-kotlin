package com.sample.huawei.pushkit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val TAG: String = this@MainActivity.javaClass.simpleName

    // topic should match the format:[\u4e00-\u9fa5\w-_.~%]{1,900}
    private val TOPIC_NAME: String = "topic_name"
    private lateinit var hmsMessaging: HmsMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hmsMessaging = HmsMessaging.getInstance(applicationContext)

        // first way to obtain token
        obtainToken()
        // second way to obtain token
        // autoInitObtainingToken()

        subscribeToTopic.setOnClickListener {
            subscribeToTopic(TOPIC_NAME)
        }

        unsubscribeToTopic.setOnClickListener {
            unsubscribeToTopic(TOPIC_NAME)
        }

        deregisteringToken.setOnClickListener { deregisteringToken() }

        enablingPushSwitch.setOnCheckedChangeListener {
                _, isEnabled -> enablePushMessage(isEnabled)
        }
    }

    private fun obtainToken() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appId =
                    AGConnectServicesConfig.fromContext(applicationContext)
                        .getString("client/app_id")
                val token = HmsInstanceId.getInstance(applicationContext).getToken(appId, "HCM")
                if (!TextUtils.isEmpty(token)) {
                    Log.i(TAG, "obtainToken() token: $token")
                }
            } catch (e: Exception) {
                Log.i(TAG, "obtainToken() failed, $e")
            }
        }
    }

    private fun subscribeToTopic(topicName: String) {
        hmsMessaging.subscribe(topicName)
            .addOnCompleteListener {
                onCompleteListener(
                    it.isComplete,
                    it.isSuccessful,
                    it.exception
                )
            }
            .addOnSuccessListener {
                onSuccessListener()
                showMessage(getString(R.string.successful_subscribe, topicName))
            }
            .addOnFailureListener { onFailureListener(it.message) }
    }

    private fun unsubscribeToTopic(topicName: String) {
        hmsMessaging.unsubscribe(topicName)
            .addOnCompleteListener {
                onCompleteListener(
                    it.isComplete,
                    it.isSuccessful,
                    it.exception
                )
            }
            .addOnSuccessListener {
                onSuccessListener()
                showMessage(getString(R.string.successful_unsubscribe, topicName))
            }
            .addOnFailureListener { onFailureListener(it.message) }
    }

    private fun deregisteringToken() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appId =
                    AGConnectServicesConfig.fromContext(applicationContext)
                        .getString("client/app_id")
                HmsInstanceId.getInstance(applicationContext).deleteToken(appId, "HCM")
                Log.i(TAG, "deregisteringToken success")
                showMessage("Deregistering token was successful")
            } catch (e: ApiException) {
                Log.e(TAG, "deregisteringToken failed.$e")
                showMessage("Deregistering token failed")
            }
        }
    }

    // This function is supported only on Huawei devices whose EMUI version is 5.1 or later.
    private fun enablePushMessage(isEnable: Boolean) {
        if (isEnable) {
            hmsMessaging.turnOnPush().addOnSuccessListener { showMessage("push was turn on") }
        } else {
            hmsMessaging.turnOffPush().addOnSuccessListener { showMessage("push was turn off") }
        }
    }

    private fun onCompleteListener(
        isComplete: Boolean,
        isSuccessful: Boolean,
        e: java.lang.Exception?
    ) {
        Log.i(
            TAG, "onCompleteListener() called \n" +
                    "isComplete - $isComplete; isSuccessful - $isSuccessful; exception - $e"
        )
    }

    private fun onFailureListener(message: String?) {
        Log.e(
            TAG, "onFailureListener() called \n" +
                    "message - $message"
        )
    }

    private fun onSuccessListener() {
        Log.i(TAG, "onSuccessListener() called")
    }

    private fun showMessage(message: String) {
        Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun autoInitObtainingToken() {
        val receiver = Receiver()
        val filter = IntentFilter()
        filter.addAction("GET_HMS_TOKEN_ACTION")
        this@MainActivity.registerReceiver(receiver, filter)
    }
}

class Receiver : BroadcastReceiver() {

    private val TAG: String = this@Receiver.javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        intent.let {
            if ("GET_HMS_TOKEN_ACTION" == intent.action) {
                Log.i(TAG, "obtainTokenViaReceiver() token: ${intent.getStringExtra("token")}")
            }
        }
    }

}