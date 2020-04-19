@file:Suppress("DEPRECATION")

package com.news9Ja

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver

class ReceiverWakefulBroadcast : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED -> {
                //what you want to do
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Launch the specified service when this message is received
//                val startServiceIntent = Intent(context, IntentServiceLoadDevotionFromServer::class.java)
//                WakefulBroadcastReceiver.startWakefulService(context, startServiceIntent)
                if (ClassSharedPreferencesSettings(context).getIsNewsLoadedOffline()){
                    ClassAlarmSettings(context).scheduleAlarm()//alarm sscheduling for background intent
                }
            }
        }
        //what you want to do
    }


}
