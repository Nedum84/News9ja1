package com.nigernewsheadlines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReceiverLoadDataFromServer : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val i = Intent(context, IntentServiceLoadDataFromServer::class.java)
        i.putExtra("foo", "bar")
        context.startService(i)
    }

    companion object {
        public val REQUEST_CODE = 12345
        val ACTION = "com.codepath.example.servicesdemo.alarm"
    }
}
