package com.news9Ja

import android.content.Intent
import android.app.IntentService


class IntentServiceWithReceiver : IntentService("IntentServiceWithReceiver") {

    override fun onHandleIntent(intent: Intent?) {
        // Fetch data passed into the intent on start
        val valFromAct = intent?.getStringExtra("foo_from_activity")
        // Construct an Intent tying it to the ACTION (arbitrary event namespace)
        val intentReceiver = Intent(ACTION)
//        ClassDownloadDataFromServer(this).numberOfUnread(intentReceiver)//Get the number of Unread
        ClassDownloadDataFromServer(this).downLoadNews()//Get the number of Unread
    }

    companion object {
//        val ACTION = "com.codepath.example.servicesdemo.MyTestService"
        val ACTION = IntentServiceWithReceiver::class.java.name
    }
}