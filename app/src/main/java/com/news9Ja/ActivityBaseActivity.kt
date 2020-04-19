package com.news9Ja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper


abstract class ActivityBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Thread.setDefaultUncaughtExceptionHandler(MyThreadUncaughtExceptionHandler())//for network thread error

        if((System.currentTimeMillis()/1000)-ClassSharedPreferences(this).getTimeForCatAndNewsCatDownload() >= 43200) {// is it more than 12HRS?
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    ClassDownloadDataFromServer(this).downLoadNewsListAndNewsCategory()
                } catch (e: Exception) {

                }
            }, 20000) //Delay 20 second}
        }
    }

    override fun onResume() {
        super.onResume()
        setRefreshTime()
    }

    override fun onDestroy() {
        super.onDestroy()
        setRefreshTime()
    }

    override fun onRestart() {
        super.onRestart()
        setRefreshTime()
    }

    override fun onPause() {
        super.onPause()
        setRefreshTime()
    }

    override fun onStop() {
        super.onStop()
        setRefreshTime()
    }



    private fun setRefreshTime(){
        ClassSharedPreferences(this).setLastRefreshTimeForNotification()
    }










    //    FOR CRASH REPORTS
    private fun appCrashReport(){
        if (!(Thread.getDefaultUncaughtExceptionHandler() is ClassCustomExceptionHandler)){
            Thread.setDefaultUncaughtExceptionHandler(ClassCustomExceptionHandler(
                    Environment.getExternalStorageDirectory().path + "/"+UrlHolder.APP_FOLDER_NAME+"/reports", "http://<desired_url>/upload.php"))
        }
    }

}
