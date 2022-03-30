package com.q.qutil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.q.util.AppUtils
import com.q.util.SDCardUtils
import com.q.util.UriUtils
import java.io.File


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(Intent.ACTION_VIEW)

        intent.addCategory(Intent.CATEGORY_DEFAULT)

        intent.setDataAndType(
            UriUtils.file2Uri(File("${SDCardUtils.getSDCardPathByEnvironment()}/hikservice/qq.apk"))
            ,
            "application/vnd.android.package-archive"
        )

        startActivity(intent)
        AppUtils.installApp("")
//        AppUtils.install(this,"${SDCardUtils.getSDCardPathByEnvironment()}/hikservice/qq.apk")

//        edMessage = EDMessage.Builder().init(this).setServiceListener(object : ServiceListener {
//            override fun onServiceConnected(service: Boolean) {
//                edMessage.password = "dasdasdas"
//            }
//
//            override fun onServiceDisconnected() {
//
//            }
//        }).build().also {
//            it.bindService()
//        }

    }
}