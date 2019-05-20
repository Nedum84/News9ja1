package com.nigernewsheadlines

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import android.util.Log

@Suppress("DEPRECATION")

class IntentServiceLoadDataFromServer : IntentService("IntentServiceLoadDataFromServer") {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper

    override fun onCreate() {
        newsListDBHelper = SQLiteNewsDBHelper(this)
        super.onCreate()
    }
    override fun onHandleIntent(intent: Intent?) {
        Log.d("Background_Service",(10..99).shuffled().last().toString())
//        ClassDownloadDataFromServer(this).downLoadNews()
        ClassDownloadDataFromServer(this).downLoadNewsListAndNewsCategory()
//        ClassDownloadDataFromServer(this).downL


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    companion object {

    }
}
