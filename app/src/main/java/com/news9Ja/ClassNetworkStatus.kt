package com.news9Ja

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class ClassNetworkStatus(context: Context?) {

    fun hasInternetAccess(): Boolean {
        if (isNetworkAvailable()) {
            try {
                val urlc = URL("http://clients3.google.com/generate_204")
                        .openConnection() as HttpURLConnection
                urlc.setRequestProperty("User-Agent", "Android")
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1500
                urlc.connect()
                return urlc.responseCode == 204 && urlc.contentLength == 0
            } catch (e: IOException) {
                Log.e(TAG, "Error checking internet connection", e)
            }

        } else {
            Log.d(TAG, "No network available!")
        }
        return false
    }

    private val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
    fun isNetworkAvailable(): Boolean {
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
//    fun isNetworkAvailable(): Boolean {
//        return true
//    }

}