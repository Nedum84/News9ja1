package com.news9Ja

import android.content.Context
import java.util.HashSet

class  ClassSharedPreferences(val context: Context?){

    private val PREFERENCE_NAME = "news_details_preference"
    private val PREFERENCE_CURRENT_NEWS_ID = "current_news_id"
    private val PREFERENCE_CURRENT_NEWS_LIST_ID = "current_news_list_id"
    private val PREFERENCE_CURRENT_CATEGORY_ID = "current_category_id"
    private val PREFERENCE_CURRENT_VIEW_URL = "current_url_view"
    private val PREFERENCE_GET_SERVER_RESPONSE = "get_server_response"
    private  val PREFERENCE_CURRENT_VIEW_POST_DETAILS = "current_view_post_details"
//    private  val PREFERENCE_NEWS_TITLE_ARRAYS = "news_title_arrays"
    private  val PREFERENCE_NEWS_LAST_ID = "news_last_id"
    private  val PREFERENCE_SAVED_SERVER_RESPONSE = "saved_server_response"
//    private  val PREFERENCE_NUM_OF_TABS = "no_of_tabs"
    private  val PREFERENCE_LAST_REFRESH_TIME_FOR_NOTIFICATION = "set_last_refresh_time_for_notification"
    private  val PREFERENCE_TIME_FOR_CAT_AND_NEWS_SOURCE_DOWNLOAD = "time_for_cat_and_news_source_download"



    private val PREFERENCE_USER_ID = "user_id"
    private val PREFERENCE_USER_EMAIL = "user_email"
    private val PREFERENCE_USERNAME = "username"

    private val preference = context?.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)!!

    //set current news id
    fun setCurrentNewsId(news_id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_CURRENT_NEWS_ID,news_id)
        editor.apply()
    }
    //get current news id
    fun getCurrentNewsId():Int{
        return  preference.getInt(PREFERENCE_CURRENT_NEWS_ID,1)
    }
    //set current news list id
    fun setCurrentNewsListId(id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_CURRENT_NEWS_LIST_ID,id)
        editor.apply()
    }
    //get current news list id
    fun getCurrentNewsListId():Int{
        return  preference.getInt(PREFERENCE_CURRENT_NEWS_LIST_ID,1)
    }
    //set current cat id
    fun setCurrentCategoryId(id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_CURRENT_CATEGORY_ID,id)
        editor.apply()
    }
    //get current cat id
    fun getCurrentCategoryId():Int{
        return  preference.getInt(PREFERENCE_CURRENT_CATEGORY_ID,1)
    }
    //set serverResponse
    fun setServerResponse(id:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_GET_SERVER_RESPONSE,id)
        editor.apply()
    }
    //get serverResponse
    fun getServerResponse():String{
        return  preference.getString(PREFERENCE_GET_SERVER_RESPONSE,"0")!!
    }

    //set current view post details
    fun setCurrentViewPostDetails(id:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_CURRENT_VIEW_POST_DETAILS,id)
        editor.apply()
    }
    //get Current View Post Details
    fun getCurrentViewPostDetails():String{
        return  preference.getString(PREFERENCE_CURRENT_VIEW_POST_DETAILS,null)!!
    }
    //set TitleForNotification
//    fun setNewsTitleForNotification(data:Set<String>){
//        val editor = preference.edit()
//        editor.putStringSet(PREFERENCE_NEWS_TITLE_ARRAYS,data)
//        editor.apply()
//    }
//    //get TitleForNotification
//    fun getNewsTitleForNotification():Set<String>{
//        return  preference.getStringSet(PREFERENCE_NEWS_TITLE_ARRAYS, HashSet(arrayListOf(context?.getString(R.string.app_name))))!!
//    }
    //set UrlForWebview
    fun setUrlForWebview(id:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_CURRENT_VIEW_URL,id)
        editor.apply()
    }
    //get UrlForWebview
    fun getUrlForWebview():String{
        return  preference.getString(PREFERENCE_CURRENT_VIEW_URL,null)!!
    }
    //set LastNewsId
    fun setLastNewsId(id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_NEWS_LAST_ID,id)
        editor.apply()
    }
    //get LastNewsId
    fun getLastNewsId():Int{
        return  preference.getInt(PREFERENCE_NEWS_LAST_ID,1)
    }
    //set SavedServer
    fun setSavedServer(id:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_SAVED_SERVER_RESPONSE,id)
        editor.apply()
    }
    //get SavedServer
    fun getSavedServer():String?{
        return  preference.getString(PREFERENCE_SAVED_SERVER_RESPONSE,"")!!
    }

    //set number of tabs
//    fun setNoOfTabs(id:Int){
//        val editor = preference.edit()
//        editor.putInt(PREFERENCE_NUM_OF_TABS,id)
//        editor.apply()
//    }
//    //get number of tabs
//    fun getNoOfTabs():Int{
//        return  preference.getInt(PREFERENCE_NUM_OF_TABS,4)
//    }

    //set last activity refresh in secs for notification
    fun setLastRefreshTimeForNotification(){
        val sysSecs = System.currentTimeMillis()/1000
        val editor = preference.edit()
        editor.putLong(PREFERENCE_LAST_REFRESH_TIME_FOR_NOTIFICATION,sysSecs)
        editor.apply()
    }
    //get last activity refresh in secs for notification
    fun getLastRefreshTimeForNotification():Long{
        return  preference.getLong(PREFERENCE_LAST_REFRESH_TIME_FOR_NOTIFICATION, 1567774190)
    }
    //set time for next category & news source download
    fun setTimeForCatAndNewsCatDownload(){
        val sysSecs = System.currentTimeMillis()/1000
        val editor = preference.edit()
        editor.putLong(PREFERENCE_TIME_FOR_CAT_AND_NEWS_SOURCE_DOWNLOAD,sysSecs)
        editor.apply()
    }
    //get time for next category & news source download
    fun getTimeForCatAndNewsCatDownload():Long{
        return  preference.getLong(PREFERENCE_TIME_FOR_CAT_AND_NEWS_SOURCE_DOWNLOAD, 1567774190)
    }






    //set UserId
    fun setUserId(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_USER_ID,data)
        editor.apply()
    }
    //get UserId
    fun getUserId():String{
        return  preference.getString(PREFERENCE_USER_ID,"")!!
    }


    //set UserEmail
    fun setUserEmail(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_USER_EMAIL,data)
        editor.apply()
    }
    //get UserEmail
    fun getUserEmail():String?{
        return  preference.getString(PREFERENCE_USER_EMAIL,null)
    }
    //set Username
    fun setUsername(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_USERNAME,data)
        editor.apply()
    }
    //get UserEmail
    fun getUsername():String?{
        return  preference.getString(PREFERENCE_USERNAME,null)
    }
    fun isLoggedIn():Boolean{
        return getUserId()!=""
    }

}