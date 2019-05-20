package com.nigernewsheadlines

object  UrlHolder{
    const val YOUTUBE_DEVELOPER_KEY = "AIzaSyCuuIkzweg_5prNp8uKGXtO48JavZuUPUo"
    private const val URL_ROOT = "http://192.168.44.236/news/server_request/"
//    private const val URL_ROOT = "http://192.168.43.168/news/server_request/"
//    private val URL_ROOT = "http://provinceofgrace.org/app_request/"
    val URL_NEWS_COMMENT: String?   = URL_ROOT + "submit_feedback_msg.php"
    val URL_USER_LOGIN: String?     = URL_ROOT + "user_login.php"
    val URL_USER_REGISTER: String?  = URL_ROOT + "user_register.php"
    val URL_DOWNLOAD_NEWS: String?  = URL_ROOT + "download_news.php"
    val URL_DOWNLOAD_NEWS_LIST_AND_LIST_CATEGORY: String?  = URL_ROOT + "download_news_list_and_news_category.php"
    val URL_NEWS_WEBSITE: String?   = "http://allnigernews.com"
}