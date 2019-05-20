package com.nigernewsheadlines

import android.content.Context

class  ClassSharedPreferences(context: Context?){

    private val PREFERENCE_NAME = "news_details_preference"
    private val PREFERENCE_CURRENT_NEWS_ID = "current_news_id"
    private val PREFERENCE_CURRENT_NEWS_LIST_ID = "current_news_list_id"
    private val PREFERENCE_CURRENT_CATEGORY_ID = "current_category_id"
    private val PREFERENCE_CURRENT_LAST_NEWS_ID = "current_last_news_id"

    private val PREFERENCE_CURRENT_ADMIN_TABLE_ID = "current_admin_table_id"
    private val PREFERENCE_LOGGED_IN_USER_ID = "logged_in_user_id"
    private val PREFERENCE_LOGGED_IN_USER_NAME = "logged_in_user_name"


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
    //set current cat id
    fun setLastNewsId(id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_CURRENT_LAST_NEWS_ID,id)
        editor.apply()
    }
    //get current cat id
    fun getLastNewsId():Int{
        return  preference.getInt(PREFERENCE_CURRENT_LAST_NEWS_ID,1)
    }








    //set current admin table id
    fun setCurrentAdminTableId(table_id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_CURRENT_ADMIN_TABLE_ID,table_id)
        editor.apply()
    }
    //get current admin table  id
    fun getCurrentAdminTableId():Int{
        return  preference.getInt(PREFERENCE_CURRENT_ADMIN_TABLE_ID,1)
    }


    //set logged in users ID
    fun setLoggedInUserId(user_id:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_LOGGED_IN_USER_ID,user_id)
        editor.apply()
    }
    //get logged in users ID
    fun getLoggedInUserId():Int{
        return  preference.getInt(PREFERENCE_LOGGED_IN_USER_ID,0)
    }
    //set logged in users Name
    fun setLoggedInUserName(user_id:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_LOGGED_IN_USER_NAME,user_id)
        editor.apply()
    }
    //get logged in users Name
    fun getLoggedInUserName():String?{
        return  preference.getString(PREFERENCE_LOGGED_IN_USER_NAME,"Pst Innocent Eleke")
    }

}