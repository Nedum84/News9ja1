package com.news9Ja

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat


object  UrlHolder{
    const val APP_FOLDER_NAME = "News9Ja"
//    private const val URL_ROOT = "http://192.168.44.236/news/server_request/"
//    private const val URL_ROOT = "http://192.168.43.168/news/server_request/"
    private val URL_ROOT = "http://news9ja.online/server_request/"
    val URL_ADD_COMMENT: String?   = URL_ROOT + "add_comment.php"
    val URL_GET_COMMENT: String?   = URL_ROOT + "get_comment.php"
    val URL_LOGIN: String?     = URL_ROOT + "login.php"
    val URL_REGISTER: String?  = URL_ROOT + "register.php"
    val URL_DOWNLOAD_NEWS: String?  = URL_ROOT + "download_news.php"
    val URL_BACKUP_BOOKMARKS: String?  = URL_ROOT + "backup_bookmarks.php"
    val URL_RESTORE_BOOKMARKS: String?  = URL_ROOT + "restore_bookmarks.php"
    val URL_DOWNLOAD_NEWS_LIST_AND_LIST_CATEGORY: String?  = URL_ROOT + "download_news_list_and_news_category.php"
    val URL_APP_CRASH_REPORT_UPLOAD: String? = URL_ROOT + "app_crash_report_upload.php"




    val LATEST_CAT_ID = 0
    val SPORTS_CAT_ID = 1
    val ENTERTAINMENTS_CAT_ID = 2
    val POLITICS_ID = 3
    val NEWS_CAT_ID = 13
    val TECH_CAT_ID = 4
    val HEALTH_CAT_ID = 10
    val WORLD_CAT_ID = 11
    val ECONOMY_CAT_ID = 5
    val SCHOLARSHIP_CAT_ID = 6

    var TAB_CATS = mutableListOf(LATEST_CAT_ID, SPORTS_CAT_ID,
                                    ENTERTAINMENTS_CAT_ID,      POLITICS_ID,
                                    NEWS_CAT_ID,                TECH_CAT_ID,
                                    SCHOLARSHIP_CAT_ID,              WORLD_CAT_ID
                                )



    fun getNewsSourceImage(c: Context, newsSourceId: Int): Int {
        val sourceIcon = "menu_icon$newsSourceId"
        val checkExistence = c.resources.getIdentifier(sourceIcon, "drawable", c.packageName)

        if (checkExistence != 0) {  // the resource exists...
            return checkExistence
        } else { // checkExistence == 0  // the resource does NOT exist!!
            return R.drawable.menu_icon18
        }
//        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()))
    }
    fun getImageDrawable(c: Context, newsSourceId: String): Drawable? {
        return ContextCompat.getDrawable(c, c.resources.getIdentifier("", "drawable", c.packageName))
//        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()))
    }
}