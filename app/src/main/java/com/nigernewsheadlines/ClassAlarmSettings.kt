package com.nigernewsheadlines

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent





class ClassAlarmSettings(var context:Context) {


    // Setup a recurring alarm every half hour
    fun scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        val intent = Intent(context, ReceiverLoadDataFromServer::class.java)
        // Create a PendingIntent to be triggered when the alarm goes off
        val pIntent = PendingIntent.getBroadcast(context, ReceiverLoadDataFromServer.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Setup periodic alarm every every half hour from this point onwards
        val firstMillis = System.currentTimeMillis() // alarm is set right away
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, (AlarmManager.INTERVAL_FIFTEEN_MINUTES)/180, pIntent)
    }

    fun cancelAlarm() {
        val intent = Intent(context, ReceiverLoadDataFromServer::class.java)
        val pIntent = PendingIntent.getBroadcast(context, ReceiverLoadDataFromServer.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pIntent)
    }
}