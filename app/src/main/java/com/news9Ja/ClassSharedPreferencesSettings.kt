package com.news9Ja

import android.content.Context

class  ClassSharedPreferencesSettings(context: Context?){

    private val PREFERENCE_NAME = "settings_preference"
    private  val PREFERENCE_OPENING_FOR_THE_FIRST_TIME = "opening_for_the_first_time"
    private val PREFERENCE_AUTO_DOWNLOAD_NEWS = "auto_download_news"
    private val PREFERENCE_AUTO_DOWNLOAD_NEWS_IMGS= "auto_download_news_imgs"
    private val PREFERENCE_NEWS_SYNCHRONIZATION_INTERVAL = "news_synchronization_interval"
//    private val PREFERENCE_REFRESH_ON_DOUBLE_CLICK = "refresh_on_db_click"
    private val PREFERENCE_GET_NOTIFICATION = "get_notification"
    private val PREFERENCE_NOTIFICATION_MODE = "notification_mode"
    private val PREFERENCE_DELETE_OLD_NEWS_INTERVAL = "delete_old_news_interval"
    private val PREFERENCE_NEXT_DELETE_OLD_NEWS_INTERVAL = "next_delete_old_news_interval"


    private val preference = context?.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)!!


    //set first time app opening
    fun setOpeningForTheFirstTime(id:Boolean){
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_OPENING_FOR_THE_FIRST_TIME,id)
        editor.apply()
    }
    //get first time app opening
    fun getOpeningForTheFirstTime():Boolean{
        return  preference.getBoolean(PREFERENCE_OPENING_FOR_THE_FIRST_TIME,true)
    }
    //set setIsNewsLoadedOffline
    fun setIsNewsLoadedOffline(choice:Boolean){
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_AUTO_DOWNLOAD_NEWS,choice)
        editor.apply()
    }
    //get AutoDownloadNewsStatus
    fun getIsNewsLoadedOffline():Boolean{
        return  preference.getBoolean(PREFERENCE_AUTO_DOWNLOAD_NEWS,true)
    }

    //set setAutoDownloadNewsImgStatus
    fun setAutoDownloadNewsImgStatus(choice:Boolean){
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_AUTO_DOWNLOAD_NEWS_IMGS,choice)
        editor.apply()
    }
    //get AutoDownloadNewsStatus
    fun getAutoDownloadNewsImgStatus():Boolean{
        return  preference.getBoolean(PREFERENCE_AUTO_DOWNLOAD_NEWS_IMGS,false)
    }

    //set setNewsSyncInterval
    fun setNewsSyncInterval(id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_NEWS_SYNCHRONIZATION_INTERVAL,id)
        editor.apply()
    }
    //get getNewsSyncInterval
    fun getSyncInterval():Int{
        return  preference.getInt(PREFERENCE_NEWS_SYNCHRONIZATION_INTERVAL,30)//10 mins
    }

    //set RefreshOnDbTapStatus
//    fun setRefreshOnDbTapStatus(choice:Boolean){
//        val editor = preference.edit()
//        editor.putBoolean(PREFERENCE_REFRESH_ON_DOUBLE_CLICK,choice)
//        editor.apply()
//    }
//    //get RefreshOnDbTapStatus
//    fun getRefreshOnDbTapStatus():Boolean{
//        return  preference.getBoolean(PREFERENCE_REFRESH_ON_DOUBLE_CLICK,true)
//    }

    //set NotificationStatus
    fun setNotificationStatus(choice:Boolean){
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_GET_NOTIFICATION,choice)
        editor.apply()
    }
    //get NotificationStatus
    fun getNotificationStatus():Boolean{
        return  preference.getBoolean(PREFERENCE_GET_NOTIFICATION,true)
    }

    //set NotificationMode
    fun setNotificationMode(choice:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_NOTIFICATION_MODE,choice)
        editor.apply()
    }
    //get NotificationStatus
    fun getNotificationMode():Int{
        return  preference.getInt(PREFERENCE_NOTIFICATION_MODE,0)//0=single or 1=batch
    }

    //set NewsDeleteInterval
    fun setNewsDeleteInterval(choice:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_DELETE_OLD_NEWS_INTERVAL,choice)
        editor.apply()
    }
    //get NotificationStatus
    fun getNewsDeleteInterval():Int{
        return  preference.getInt(PREFERENCE_DELETE_OLD_NEWS_INTERVAL,1)//0=single or 1=batch
    }

    //set Next News DeleteInterval
    fun setNextNewsDeleteInterval(choice:Long){
        val editor = preference.edit()
        editor.putLong(PREFERENCE_NEXT_DELETE_OLD_NEWS_INTERVAL,choice)
        editor.apply()
    }
    //get Next News DeleteInterval
    fun getNextNewsDeleteInterval():Long{
        return  preference.getLong(PREFERENCE_NEXT_DELETE_OLD_NEWS_INTERVAL,1879876540987)
    }


    //reset preference
    fun resetSettingsPreference(){
        preference.edit().clear().apply();
    }



}