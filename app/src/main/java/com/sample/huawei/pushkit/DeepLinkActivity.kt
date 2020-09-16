package com.sample.huawei.pushkit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_deeplink.*

class DeepLinkActivity : AppCompatActivity(R.layout.activity_deeplink) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        returnToHomeBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}