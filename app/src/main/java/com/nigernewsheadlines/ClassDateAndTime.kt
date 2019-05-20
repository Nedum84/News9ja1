package com.nigernewsheadlines

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class ClassDateAndTime {

    fun dateTimePast(timeStamp:Long):String{
// it comes out like this 2013-08-31 15:55:22 so adjust the date format
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = df.parse(getDateTimeWithMinsHrs(timeStamp))
        val epoch = date.time
        return DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString()//3 mins ago
    }


    private fun getDateTimeWithMinsHrs(timeStamp: Long): String? {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())//Locale.US for usa
            val netDate = java.util.Date(timeStamp*1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun getDateTime(s: Long): String? {
        try {
            val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())//Mar 21, 2019
            val netDate = java.util.Date(s*1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    fun getDateTime2(s: Long): String? {
        try {
            val sdf = java.text.SimpleDateFormat("EEE. MMM dd, yyyy", Locale.getDefault())//Wed, Mar 21, 2019
            val netDate = java.util.Date(s*1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}