package com.news9Ja


data class NewsListClassBinder(val table_id: Int?,val news_id: Int, val news_list_id: Int, val news_category: Int,
                               val news_url: String?, val news_title: String?, val news_img_urls: String?,
                               val news_content_body: String?, val news_date_int: Int?,
                               val news_no_of_views: Int?, val news_no_of_comments: Int, val news_is_bookmarked: Int?,val type:Int = 0) {}
data class NewsComments(val comment_id: String?,val username: String?,val comment_content: String, val comment_date: String?) {}
