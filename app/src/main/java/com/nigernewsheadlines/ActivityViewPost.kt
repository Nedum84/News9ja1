package com.nigernewsheadlines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_view_post.*
import java.util.ArrayList

class ActivityViewPost : AppCompatActivity() {
    private lateinit var newsListDBHelper : SQLiteNewsDBHelper
    var currentNewsId :Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)


        newsListDBHelper = SQLiteNewsDBHelper(this)
        currentNewsId = ClassSharedPreferences(this).getCurrentNewsId()

        getNewsData(newsListDBHelper.readSingleNewsDetails(currentNewsId))
        bindBtnActions()
        Slidr.attach(this)//for slidr swipe lib
    }

    private fun bindBtnActions() {
        previewNewsBookMarkStatus.setOnClickListener {
            bookMarkStatus()
        }
    }
    private fun bookMarkStatus(){
        if (getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(currentNewsId)) == 1){
            newsListDBHelper.updateNewsBookmarkStatus(currentNewsId,"unstar")
            previewNewsBookMarkStatus.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
        }else{
            newsListDBHelper.updateNewsBookmarkStatus(currentNewsId,"star")
            previewNewsBookMarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
        }
    }

    private fun getNewsData(newsLists: ArrayList<NewsListClassBinder>) {

        for (newsRow: NewsListClassBinder in newsLists) {
            val getEachNewsRow = NewsListClassBinder(
                    newsRow.table_id,
                    newsRow.news_id,
                    newsRow.news_list_id,
                    newsRow.news_category,
                    newsRow.news_url,
                    newsRow.news_title,
                    newsRow.news_img_urls,
//                    newsRow.news_img_url_download_name,
                    newsRow.news_content_body,
                    newsRow.news_date_int,
                    newsRow.news_no_of_views,
                    newsRow.news_no_of_comments,
                    newsRow.news_hide,
                    newsRow.news_is_bookmarked
            )
            loadDataIntoViews(getEachNewsRow)
        }
    }

    private fun loadDataIntoViews(getEachNewsRow : NewsListClassBinder){
        val imgNo        = TextUtils.split(getEachNewsRow.news_img_urls, ",")

        if(getEachNewsRow.news_img_urls==""){
            previewNewsImg.visibility = View.GONE
        }else{
        }
        val options = RequestOptions()
//                .placeholder(R.drawable.ic_tune_black_24dp)
                .error(R.drawable.ic_clear_white_24dp)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(this)
                .load(getEachNewsRow.news_img_urls)
                .apply(options)
                .thumbnail(.1f)
                .into(previewNewsImg)

        previewNewsTitle.text       = ClassHtmlFormater().fromHtml(getEachNewsRow.news_title)
        previewNewsCategory.text    = newsListDBHelper.readCategoryDetails(getEachNewsRow.news_category)[0][1]
        previewNewsNewsSource.text = newsListDBHelper.readNewsListDetails(getEachNewsRow.news_list_id)[0][2]
        previewNewsDate.text        = ClassDateAndTime().getDateTime2(getEachNewsRow.news_date_int!!.toLong())
        previewNewsNoOfViews.text   = getEachNewsRow.news_no_of_views.toString()
        previewNewsCommentNo.text   = getEachNewsRow.news_no_of_comments.toString()
//        previewNewsBody.text        = Html.fromHtml(getEachNewsRow.news_content_body)
        previewNewsBody.text        = ClassHtmlFormater().fromHtml(getEachNewsRow.news_content_body)


        if (getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(ClassSharedPreferences(this).getCurrentNewsId())) == 1){
            previewNewsBookMarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
        }else{
            previewNewsBookMarkStatus.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
        }
    }
    private fun getNewsBookMarkStatus(newsLists: ArrayList<NewsListClassBinder>):Int? {
        var newsIsBookmarked:Int? = null
        for (newsRow: NewsListClassBinder in newsLists) {
            newsIsBookmarked = newsRow.news_is_bookmarked
        }
        return newsIsBookmarked
    }

    //dot dot menu creation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }
    //action on the dot dot menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> {super.onBackPressed();return true }
            R.id.menu_settings -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }



}
