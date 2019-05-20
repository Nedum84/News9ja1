package com.nigernewsheadlines

import android.graphics.Bitmap


data class NewsListClassBinder(val table_id: Int?,val news_id: Int, val news_list_id: Int, val news_category: Int,
                                       val news_url: String?, val news_title: String?, val news_img_urls: String?,
                                       val news_content_body: String?, val news_date_int: Int?,
                                       val news_no_of_views: Int?, val news_no_of_comments: Int,
                                       val news_hide: Int?, val news_is_bookmarked: Int?) {}
