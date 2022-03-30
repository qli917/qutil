package com.q.qutil

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hikvision.parameter.HikParameterInterface
import com.q.util.LogUtils
import com.q.util.Utils


//import com.hikvision.enterprisedesktop.EDaidl

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtils.json(HikParameterInterface.getInstance().apply {
            setContentResolver(Utils.getApp().contentResolver)
        }.getStrPara("appPassword"))
//        EDaidl.getInstance(this)

//        bindService(Intent().apply {
//            setPackage("com.hikvision.enterprisedesktop")
//        }, object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                ed = ED.Stub.asInterface(service)
//                LogUtils.i(ed.installApp)
//                ed.password = "7897987798"
//            }
//
//            override fun onServiceDisconnected(name: ComponentName?) {
//                ToastUtils.showLong("fuwu")
//            }
//        }, BIND_AUTO_CREATE)

    }
}