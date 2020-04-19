package com.news9Ja

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.ActivityManager


@Suppress("DEPRECATION")
class ReceiverLoadDataFromServer : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val i = Intent(context, IntentServiceLoadDataFromServer::class.java)
        i.putExtra("foo", "bar")

        if (!isServiceRunning(IntentServiceLoadDataFromServer::class.java,context))
            context.startService(i)
//            ClassAlertDialog(context).toast("Not Running...")
//        else
//            ClassAlertDialog(context).toast("Running...")
//        context.stopService(i)
    }

    private fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
//                Log.i("Service already", "running")
                return true
            }
        }
//        Log.i("Service not", "running")
        return false
    }

    companion object {
        val REQUEST_CODE = 12345
        val ACTION = "com.codepath.example.servicesdemo.alarm"
    }
}
