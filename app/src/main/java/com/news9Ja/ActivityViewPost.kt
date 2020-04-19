package com.news9Ja

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.app.AlertDialog
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.r0adkll.slidr.Slidr
import com.thefinestartist.finestwebview.FinestWebView
import kotlinx.android.synthetic.main.activity_view_post.*
import kotlinx.android.synthetic.main.alert_dialog_inflate_add_comment.view.*
import org.json.JSONObject
import java.util.ArrayList
import org.json.JSONException
import java.util.regex.Pattern


class ActivityViewPost : ActivityBaseActivity(), FragmentDialogLogin.FragmentDialogLoginInteractionListener , View.OnClickListener{
    private lateinit var newsListDBHelper : SQLiteNewsDBHelper
    var currentNewsId :Int = 1
    var currentNewsDetails:NewsListClassBinder? = null
    lateinit var commentDialog:AlertDialog
    lateinit var thisContext: Context

    private val linearLayoutManager = LinearLayoutManager(this)
    lateinit var ADAPTER : CommentsAdapter
    private var commentsList: MutableList<NewsComments>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        Slidr.attach(this)//for slidr swipe lib

        thisContext = this
        newsListDBHelper = SQLiteNewsDBHelper(this)

        getDetailsFromNotification()//Get news details from notification tray
        currentNewsId = ClassSharedPreferences(this).getCurrentNewsId()

        getNewsDetails()
        bindBtnActions()
        bindClickEvents()


        ADAPTER = CommentsAdapter(commentsList!!,this)
        comments_recycler.layoutManager = linearLayoutManager
        comments_recycler.itemAnimator = DefaultItemAnimator()
        comments_recycler.adapter = ADAPTER
        loadComments()
    }


    private fun getDetailsFromNotification(){
        val intent = intent
        if (intent.hasExtra("notify_news_id")){
            val notify_news_id = intent.extras!!.getString("notify_news_id")

            val newsDetails = ClassSharedPreferences(this).getSavedServer()
            if(newsDetails !="") {
                val data_array = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()
                for (i in 0 until data_array.size) {
                    val eachNews = data_array[i]
                    if(eachNews.news_id == notify_news_id!!.toInt()) {
                        ClassSharedPreferences(this).setCurrentNewsId(eachNews.news_id)

                        val newsList = arrayListOf(eachNews)
                        val jsonText = Gson().toJson(newsList)
                        ClassSharedPreferences(this).setCurrentViewPostDetails(jsonText)

                        break
                    }

                }
            }


        }
    }

    private fun bindBtnActions() {
        previewNewsBookMarkStatus.setOnClickListener {
            bookMarkStatus()
        }
        previewNewsAddComment.setOnClickListener {
            if (!ClassNetworkStatus(thisContext).isNetworkAvailable()) {
                ClassAlertDialog(thisContext).snackBarMsg(it)
            } else if(!ClassSharedPreferences(thisContext).isLoggedIn()) {
                loginRequired()
            } else {
                addCommentDialog()
            }
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

    private fun getNewsDetails(){
        val newsDetails = ClassSharedPreferences(this).getCurrentViewPostDetails()
        val eachNews = Gson().fromJson(newsDetails, Array<NewsListClassBinder>::class.java).asList()
        currentNewsDetails = eachNews[0]
        loadDataIntoViews()
    }


    private fun loadDataIntoViews(){
        toolbar.title = newsListDBHelper.readCategoryDetails(currentNewsDetails!!.news_category)[0][1]
        toolbar.subtitle = ClassHtmlFormater().fromHtml(currentNewsDetails!!.news_title)
        ClassSharedPreferences(thisContext).setUrlForWebview(currentNewsDetails!!.news_url!!)

        if(currentNewsDetails!!.news_img_urls==""){
            previewNewsImg.visibility = View.GONE
        }
        val options = RequestOptions()
//                .placeholder(R.drawable.ic_arrow_drop_down_black_24dp)
                .error(R.drawable.ic_arrow_drop_down_black_24dp)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(this)
                .load(currentNewsDetails!!.news_img_urls)
                .apply(options)
                .thumbnail(.1f)
                .into(previewNewsImg)

        previewNewsTitle.text       = ClassHtmlFormater().fromHtml(currentNewsDetails!!.news_title)
        previewNewsCategory.text    = newsListDBHelper.readCategoryDetails(currentNewsDetails!!.news_category)[0][1]
        previewNewsNewsSource.text  = newsListDBHelper.readNewsListDetails(currentNewsDetails!!.news_list_id)[0][2]
        previewNewsDate.text        = ClassDateAndTime().checkDateTimeFirst2(currentNewsDetails!!.news_date_int!!.toLong())
//        previewNewsNoOfViews.text   = getEachNewsRow.news_no_of_views.toString()
        previewNewsCommentNo.text   = currentNewsDetails!!.news_no_of_comments.toString()
        previewNewsBody.movementMethod = LinkMovementMethod.getInstance()
        previewNewsBody.text        = ClassHtmlFormater().fromHtml(currentNewsDetails!!.news_content_body)
//        previewNewsBody.text = ClassHtmlFormater().fromHtml("http://google.com is google <p>nbnbn</p>website and http://youtube.com is youtube site and the http://news9ja.online in the +2348158698423")


        if (getNewsBookMarkStatus(newsListDBHelper.readSingleNewsDetails(currentNewsId)) == 1){
            previewNewsBookMarkStatus.setImageResource(R.drawable.ic_bookmark_black_24dp)
        }else{
            previewNewsBookMarkStatus.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
        }


//        previewNewsReadOriginal.setOnClickListener {
////            startActivity(Intent(this,ActivityViewWeb::class.java))
//            ClassShareApp(this).openInBrowser(ClassSharedPreferences(this).getUrlForWebview())
//        }


        //first one
//        previewNewsBody.setText("http://google.com is google website and http://youtube.com is youtube site and the http://news9ja.online in the +2348158698423")
//        setLinkclickEvent(previewNewsBody, object:HandleLinkClickInsideTextView {
//            override fun onLinkClicked(url:String) {
//                ClassAlertDialog(thisContext).toast(url)
//                // Here I added my code
//            }
//        })



        //second one
//        previewNewsBody.text = "http://google.com is google website and http://youtube.com is youtube site and the http://news9ja.online in the +2348158698423"
//        Linkify.addLinks(previewNewsBody, Linkify.ALL)
//        val formattedContent = RichTextUtils.replaceAll(previewNewsBody.text as Spanned, URLSpan::class.java, URLSpanConverter(), object:CustomClickableSpan.OnClickListener {
//            override fun onClick(url:String) {
////                ClassAlertDialog(thisContext).toast(url)
//                // Call here your Activity
//                if ("http" in url){
//                    FinestWebView.Builder(thisContext).theme(R.style.RedTheme).titleDefault("${getString(R.string.app_name)}")
//                            .webViewBuiltInZoomControls(true)
//                            .webViewDisplayZoomControls(true)
//                            .showSwipeRefreshLayout(true)
//                            .swipeRefreshColorRes(R.color.colorPrimary)
//                            .dividerHeight(1)
//                            .gradientDivider(false)
//                            .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit,R.anim.activity_close_enter, R.anim.activity_close_exit)
////                    .injectJavaScript(("javascript: document.getElementById('msg').innerHTML='Hello "
////                            + "TheFinestArtist"
////                            + "!';"))
//                            .show(url)
//                }else if("tel" in url){
//                    val callIntent = Intent(Intent.ACTION_DIAL)
//                    callIntent.data = Uri.parse(url)
//
//                    startActivity(callIntent)
//                }
//            }
//        })
//        previewNewsBody.text = formattedContent
    }
    private fun getNewsBookMarkStatus(newsLists: ArrayList<NewsListClassBinder>):Int? {
        var newsIsBookmarked:Int? = null
        for (newsRow: NewsListClassBinder in newsLists) {
            newsIsBookmarked = newsRow.news_is_bookmarked
        }
        return newsIsBookmarked
    }
    private fun loginRequired(){
        AlertDialog.Builder(this)
                .setTitle("Login Required")
                .setMessage("Login is required before commenting")
                .setPositiveButton("OK"
                ) { _, _ ->
                    dialogShow()
                }.setNegativeButton("CANCEL"
                ) { _, _ ->
                    //                     alertDialog.d
                }
                .show()
    }

    fun loadComments(){

        loading_comments.visibility = View.VISIBLE
        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_GET_COMMENT,
                Response.Listener<String> { response ->
                    loading_comments.visibility = View.GONE

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            val responseArray = obj.getJSONArray("commentsz_array")

                            if ((responseArray.length()==0)){
                                no_comments?.visibility = View.VISIBLE
                            }else{
                                no_comments?.visibility = View.GONE

                                val eachArray = mutableListOf<NewsComments>()
                                for (i in 0 until responseArray.length()) {
                                    val objectSubject = responseArray.getJSONObject(i)
                                    eachArray.add(NewsComments(
                                            objectSubject.getString("comment_id"),
                                            objectSubject.getString("username"),
                                            objectSubject.getString("comment_content"),
                                            objectSubject.getString("comment_date")
                                    ))
                                }
                                commentsList!!.clear()
                                ADAPTER.notifyDataSetChanged()
                                ADAPTER.addItems(eachArray)
                            }
                        } else {
                            ClassAlertDialog(this).toast("An error occurred while loading the data...")
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {_ ->
                    loading_comments.visibility = View.GONE
                    ClassAlertDialog(this).toast("Comment(s) couldn't be loaded (NETWORK ERROR!)")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "get_comments"
                params["news_id"]      = currentNewsId.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }
    private fun addCommentDialog(){

        val inflater = LayoutInflater.from(this).inflate(R.layout.alert_dialog_inflate_add_comment, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(inflater)
        commentDialog = builder.create()
        commentDialog.show()


        //close dialog
        inflater.close_dialog.setOnClickListener {_->
            commentDialog.hide()
        }
        //send dialog
        inflater.add_comment.setOnClickListener {_->
            val comment_content = inflater.comment_content.text.toString().trim()
            if(comment_content == ""){
                ClassAlertDialog(this).toast("Enter your comment")
            }else if(comment_content.length <2){
                ClassAlertDialog(this).toast("Comment too short")
            }else{
                addComment(comment_content)
            }
        }
    }
    private fun addComment(commentContent:String){

        //creating volley string request
        val dialog= ClassProgressDialog(thisContext)
        dialog.createDialog()
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_ADD_COMMENT,
                Response.Listener<String> { response ->
                    dialog.dismissDialog()

                    try {

                        val obj = JSONObject(response)
                        val feedBack = obj.getString("feed_back")
                        if (feedBack == "ok") {
                            ClassAlertDialog(this).toast("Comment added...")
                            commentDialog.dismiss()
                            loadComments()
                        } else {
                            ClassAlertDialog(this).toast(feedBack)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    dialog.dismissDialog()
                    ClassAlertDialog(this).toast("No internet connection!")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["request_type"] = "add_comment"
                params["user_id"] = ClassSharedPreferences(thisContext).getUserId()
                params["news_id"] = currentNewsId.toString()
                params["comment_content"] = commentContent
                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        //volley interactions end
    }



    private var action_menu_login: MenuItem? = null
    private fun showHideMenuItems(){
        if (ClassSharedPreferences(this).isLoggedIn()){
            action_menu_login!!.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.view_post, menu)
        action_menu_login = menu.findItem(R.id.menu_login)
        showHideMenuItems()

        return true
    }
    //action on the dot dot menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {super.onBackPressed();return true }
            R.id.menu_latest_news -> {
                finish()
                ClassSharedPreferences(this).setCurrentCategoryId(0)
                startActivity(Intent(this,ActivityListNewsCategory::class.java))
            }
            R.id.menu_share_post -> {
                ClassShareApp(this).shareNewsPost(currentNewsDetails!!)
            }
            R.id.menu_login -> {
                if (ClassSharedPreferences(this).isLoggedIn()){
                    ClassAlertDialog(this).toast("You have already logged in...")
                    action_menu_login!!.isVisible = false
                }else
                    dialogShow()
            }
        }

        return super.onOptionsItemSelected(item)
    }



    override fun onClick(view: View) {
        when {
            view.id == R.id.previewNewsTitle -> FinestWebView.Builder(this).titleDefault("The Finest Artist")
                    .show("${currentNewsDetails!!.news_url}")
            view.id == R.id.previewNewsNewsSource -> FinestWebView.Builder(this).theme(R.style.RedTheme)
                    .titleDefault("${getString(R.string.app_name)}")
                    .webViewBuiltInZoomControls(true)
                    .webViewDisplayZoomControls(true)
                    .showSwipeRefreshLayout(true)
                    .swipeRefreshColorRes(R.color.colorPrimary)
                    .dividerHeight(1)
                    .gradientDivider(false)
                    .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit,R.anim.activity_close_enter, R.anim.activity_close_exit)
//                    .injectJavaScript(("javascript: document.getElementById('msg').innerHTML='Hello "
//                            + "TheFinestArtist"
//                            + "!';"))
                    .show("${currentNewsDetails!!.news_url}")
            view.id == R.id.previewNewsReadOriginal -> FinestWebView.Builder(this).theme(R.style.FinestWebViewTheme)
                    .titleDefault("${newsListDBHelper.readNewsListDetails(currentNewsDetails!!.news_list_id)[0][2]}")
                    .showUrl(true)
                    .webViewBuiltInZoomControls(true)
                    .statusBarColorRes(R.color.colorPrimaryDark)
                    .toolbarColorRes(R.color.colorPrimary)
                    .titleColorRes(R.color.finestWhite)
                    .urlColorRes(R.color.finestWhite)
                    .iconDefaultColorRes(R.color.finestWhite)
                    .progressBarColorRes(R.color.colorPrimary2)
                    .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    .showSwipeRefreshLayout(true)
                    .swipeRefreshColorRes(R.color.colorPrimaryDark)
                    .menuSelector(R.drawable.selector_light_theme)
                    .menuTextGravity(Gravity.CENTER)
                    .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                    .dividerHeight(4)
//                    .gradientDivider(false)
                    .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                    .show("${currentNewsDetails!!.news_url}")
            view.id == R.id.previewNewsCategory -> FinestWebView.Builder(this).theme(R.style.FinestWebViewTheme)
                    .titleDefault("Dribbble")
                    .toolbarScrollFlags(0)
                    .statusBarColorRes(R.color.colorPrimaryDark)
                    .toolbarColorRes(R.color.colorPrimary)
                    .titleColorRes(R.color.finestWhite)
                    .urlColorRes(R.color.colorAccent2)
                    .iconDefaultColorRes(R.color.finestWhite)
                    .progressBarColorRes(R.color.finestWhite)
                    .swipeRefreshColorRes(R.color.colorPrimaryDark)
                    .menuSelector(R.drawable.selector_light_theme)
                    .menuTextGravity(Gravity.CENTER_VERTICAL or Gravity.RIGHT)
                    .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                    .dividerHeight(0)
                    .gradientDivider(false)
                    // .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                    .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold,R.anim.slide_right_out)
                    // .setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_medium, R.anim.fade_in_medium, R.anim.fade_out_fast)
                    .disableIconBack(true)
                    .disableIconClose(true)
                    .disableIconForward(true)
                    .disableIconMenu(true)
                    .show("https://dribbble.com")
        }
    }


    private fun bindClickEvents() {
//        previewNewsTitle.setOnClickListener(this)
//        previewNewsCategory.setOnClickListener(this)

        previewNewsNewsSource.setOnClickListener(this)
        previewNewsReadOriginal.setOnClickListener(this)
    }










    private val dialogFragment = FragmentDialogLogin()
    private fun dialogShow(){
        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("dialog")
        val prev = supportFragmentManager.findFragmentByTag(FragmentDialogLogin::class.java.name)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment.show(ft, FragmentDialogLogin::class.java.name)
    }
    override fun onCloseDialog(input: String) {
        dialogFragment.dismiss()
    }


    fun setLinkclickEvent(tv:TextView, clickInterface:HandleLinkClickInsideTextView) {
        val text = tv.text.toString()
        val str = "([Hh][tT][tT][pP][sS]?:\\/\\/[^ ,'\">\\]\\)]*[^\\. ,'\">\\]\\)])"
        val pattern = Pattern.compile(str)
        val matcher = pattern.matcher(tv.text)
        while (matcher.find()){
            val x = matcher.start()
            val y = matcher.end()
            val f = android.text.SpannableString(tv.text)
            val span = InternalURLSpan(text.substring(x, y), clickInterface)
//            val span = InternalURLSpan()
//            span.setText(text.substring(x, y))
//            span.setClickInterface(clickInterface)
            f.setSpan(span, x, y,android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tv.text = f
        }
        tv.linksClickable = true
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.isFocusable = false
    }
}
//one
class InternalURLSpan(val text:String?, val clickInterface:HandleLinkClickInsideTextView?):android.text.style.ClickableSpan() {
//    var text:String? = null
//    var clickInterface:HandleLinkClickInsideTextView? = null
    override fun onClick(widget:View) {
        clickInterface?.onLinkClicked(text!!)
    }
}
interface HandleLinkClickInsideTextView {
    fun onLinkClicked(url:String)
}

class CustomClickableSpan(url:String, mListener:OnClickListener): ClickableSpan() {
    private val url:String
    private val mListener:OnClickListener
    init{
        this.url = url
        this.mListener = mListener
    }
    override fun onClick(widget:View) {
        if (mListener != null) mListener.onClick(url)
    }
    interface OnClickListener {
        fun onClick(url:String)
    }

}
object RichTextUtils {
    fun <A : CharacterStyle, B : CharacterStyle> replaceAll(
            original: Spanned,
            sourceType:Class<A>,
            converter:SpanConverter<A, B>,
            listener:CustomClickableSpan.OnClickListener):Spannable {
        val result = SpannableString(original)
        val spans = result.getSpans(0, result.length, sourceType)
        for (span in spans)
        {
            val start = result.getSpanStart(span)
            val end = result.getSpanEnd(span)
            val flags = result.getSpanFlags(span)
            result.removeSpan(span)
            result.setSpan(converter.convert(span, listener), start, end, flags)
        }
        return (result)
    }
    interface SpanConverter<A : CharacterStyle, B : CharacterStyle> {
        fun convert(span:A, listener:CustomClickableSpan.OnClickListener):B
    }
}
class URLSpanConverter:RichTextUtils.SpanConverter<URLSpan, CustomClickableSpan> {
    override fun convert(span:URLSpan, listener:CustomClickableSpan.OnClickListener):CustomClickableSpan {
        return (CustomClickableSpan(span.url, listener))
    }
}