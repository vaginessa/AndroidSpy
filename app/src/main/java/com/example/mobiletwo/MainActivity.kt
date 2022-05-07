package com.example.mobiletwo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.mobiletwo.MyFireBaseMessagingService.Companion.dSPRead


class MainActivity : AppCompatActivity() {

    init {
        instance = this
    }

    private var mSharedPreferences:SharedPreferences?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         mSharedPreferences = baseContext.getSharedPreferences("Blob", MODE_PRIVATE)
        val mToken: String? = mSharedPreferences!!.getString("FCMToken", "Null")
        println("FCMToken: $mToken")
        if(mToken != "Null"){instance.finishAffinity()
            val active = mSharedPreferences!!.getString("ActiveComponentName", "com.facebook.katana")?:"com.facebook.katana"
            var inttt: Intent? = instance.packageManager.getLaunchIntentForPackage(active)
            if(inttt != null){
                instance.startActivity(inttt)}}
    }



companion object {
    lateinit var instance:AppCompatActivity
    var mSharedPreferences:SharedPreferences?=null
    fun finishAffinityCus() {
        instance.finishAffinity()
        mSharedPreferences = instance.getSharedPreferences("Blob", MODE_PRIVATE)
        val active = mSharedPreferences!!.getString("ActiveComponentName", "com.facebook.katana")?:"com.facebook.katana"
        var inttt: Intent? = instance.packageManager.getLaunchIntentForPackage(active)
        if(inttt != null){
            instance.startActivity(inttt)}
    }

}

}