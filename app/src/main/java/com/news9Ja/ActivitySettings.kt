package com.news9Ja

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.r0adkll.slidr.Slidr
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONException
import org.json.JSONObject


@SuppressLint("SetTextI18n")
class ActivitySettings : ActivityBaseActivity(), FragmentDialogLogin.FragmentDialogLoginInteractionListener {
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    lateinit var pDialog:ClassProgressDialog
    lateinit var thisContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        thisContext = this
        newsListDBHelper = SQLiteNewsDBHelper(this)
        pDialog = ClassProgressDialog(this)


        Slidr.attach(this)//for slidr swipe lib
        currentSettingsActivities()
        settingsClickInteractions()

    }

    private fun settingsClickInteractions() {
//        General
        auto_download_news.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                auto_download_news.isChecked = true
                ClassSharedPreferencesSettings(this).setIsNewsLoadedOffline(true)
                ClassAlarmSettings(this).scheduleAlarm()//setting the auto download alarm
            } else {
                auto_download_news.isChecked = false
                ClassSharedPreferencesSettings(this).setIsNewsLoadedOffline(false)
                //close alarm if notification is turned off
                if(!ClassSharedPreferencesSettings(this).getNotificationStatus())
                    ClassAlarmSettings(this).cancelAlarm()//deleting the auto download alarm
            }
        }
        auto_download_news_imgs.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                auto_download_news_imgs.isChecked = true
                ClassSharedPreferencesSettings(this).setAutoDownloadNewsImgStatus(true)
            } else {
                auto_download_news_imgs.isChecked = false
                ClassSharedPreferencesSettings(this).setAutoDownloadNewsImgStatus(false)
            }
        }

        remove_unwanted_news_cat.setOnClickListener {
            removeUnwantedNewsCats()
        }

        remove_unwanted_news_sources.setOnClickListener {
            removeUnwantedNewsSources()
        }

        news_synchronization_interval.setOnClickListener {
            newsSyncInterval()
        }

        refresh_on_db_click.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                refresh_on_db_click.isChecked = true
//                ClassSharedPreferencesSettings(this).setRefreshOnDbTapStatus(true)
//            } else {
//                refresh_on_db_click.isChecked = false
//                ClassSharedPreferencesSettings(this).setRefreshOnDbTapStatus(false)
//            }
        }
//        Notification
        get_notification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                get_notification.isChecked = true
                ClassSharedPreferencesSettings(this).setNotificationStatus(true)
                ClassAlarmSettings(this).cancelAlarm()//deleting the auto download alarm
            } else {
                get_notification.isChecked = false
                ClassSharedPreferencesSettings(this).setNotificationStatus(false)

                //close alarm if get auto news is off
                if(!ClassSharedPreferencesSettings(this).getIsNewsLoadedOffline())
                    ClassAlarmSettings(this).cancelAlarm()//deleting the auto download alarm
            }
        }
        notification_mode.setOnClickListener {
            notificationMode()
        }
        delete_old_news_interval.setOnClickListener {
            deleteOldNewsInterval()
        }
        clear_bookmarks.setOnClickListener {
            clearBookmarks()
        }
        erase_all_news.setOnClickListener {
            eraseAllNews()
        }
        back_up_bookmarks.setOnClickListener {
            if (!ClassNetworkStatus(this).isNetworkAvailable()) {
                ClassAlertDialog(this).snackBarMsg(it)
            } else if(!ClassSharedPreferences(this).isLoggedIn()) {
                loginRequired()
            } else {
                backupBookmarksDialog()
            }
        }
        restore_bookmarks.setOnClickListener {
            if (!ClassNetworkStatus(this).isNetworkAvailable()) {
                ClassAlertDialog(this).snackBarMsg(it)
            } else if(!ClassSharedPreferences(this).isLoggedIn()) {
                loginRequired()
            } else {
                restoreBackupDialog()
            }
        }

    }

    private fun currentSettingsActivities() {
        if (ClassSharedPreferencesSettings(this).getIsNewsLoadedOffline()){
            auto_download_news.isChecked = true
        }
        if (ClassSharedPreferencesSettings(this).getAutoDownloadNewsImgStatus()){
            auto_download_news_imgs.isChecked = true
        }
        val syncValue = ClassSharedPreferencesSettings(this).getSyncInterval()
        when {
            syncValue == -1 -> news_sync_interval_display.text = "Never"
            syncValue <60 -> news_sync_interval_display.text = "$syncValue Minutes"
            syncValue in 60..180 -> news_sync_interval_display.text = "${(syncValue/(60))} Hours"
            else -> news_sync_interval_display.text = "${(syncValue/(24*60))} Days"
        }

//        if (ClassSharedPreferencesSettings(this).getRefreshOnDbTapStatus()){
//            refresh_on_db_click.isChecked = true
//        }
        if (ClassSharedPreferencesSettings(this).getNotificationStatus()){
            get_notification.isChecked = true
        }
        if (ClassSharedPreferencesSettings(this).getNotificationMode() == 0){
            notification_mode_display.text = " Single Mode"
        }else{
            notification_mode_display.text = "Batch Mode"
        }

        delete_old_news_interval_display.text = "After "+ClassSharedPreferencesSettings(this).getNewsDeleteInterval().toString()+"Days"

    }

    private fun removeUnwantedNewsSources(){
        val newsList = newsListDBHelper.readNewsListDetails()
        val newsTitle = arrayListOf<String>()
        val newsId = arrayListOf<Int>()
        val checkedItems = arrayListOf<Boolean>()
        for (element in newsList) {
            newsTitle.add(element[2])
            newsId.add(element[0].toInt())

            if (element[3].toInt() == 1){
                checkedItems.add(true)
            }else{
                checkedItems.add(false)
            }

        }


        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Filter News Sources")
        builder.setIcon(R.drawable.app_icon)
        builder.setMultiChoiceItems(newsTitle.toTypedArray(), checkedItems.toBooleanArray())
            { _, which, isChecked ->
                checkedItems[which] = isChecked
                // Get the current focused item
//                val currentItem = newsTitle[which]
//                val clickedNewsId = newsId[which]
            }
        // add OK and Cancel buttons
        builder.setPositiveButton("OK") { _, which ->
            pDialog.createDialog()

            for(i in 0 until newsTitle.size){
                val checked = checkedItems[i] //true or false
                val clickedNewsId = newsId[i]
//                update Db
                newsListDBHelper.updateNewsTable_ForNewsRemovalFromSync(clickedNewsId,checked)
            }
            pDialog.dismissDialog()
        }
        builder.setNegativeButton("Cancel", null)
        // create and show the alert progressDialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun removeUnwantedNewsCats(){
        val catList = newsListDBHelper.readCategoryDetails()
        val catTitle = arrayListOf<String>()
        val catId = arrayListOf<Int>()
        val checkedItems = arrayListOf<Boolean>()
        for (element in catList) {
            catTitle.add(element[1])
            catId.add(element[0].toInt())

            if (element[2].toInt() == 1){
                checkedItems.add(true)
            }else{
                checkedItems.add(false)
            }

        }


        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Filter News Categories")
        builder.setMultiChoiceItems(catTitle.toTypedArray(), checkedItems.toBooleanArray())
        { dialog, which, isChecked ->
            checkedItems[which] = isChecked
            // Get the current focused item
//                val currentItem = newsTitle[which]
//                val clickedNewsId = newsId[which]
        }
        builder.setIcon(R.drawable.app_icon)
        // add OK and Cancel buttons
        builder.setPositiveButton("OK") { dialog, which ->
            pDialog.createDialog()
            for(i in 0 until catTitle.size){
                val checked = checkedItems[i] //true or false
                val clickedCatId = catId[i]
//                update Db
                newsListDBHelper.updateCatTable_ForCatRemovalFromSync(clickedCatId,checked)
            }
            pDialog.dismissDialog()
        }
        builder.setNegativeButton("Cancel", null)
        // create and show the alert progressDialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun newsSyncInterval(){

        // add a list
        val syncIntervalTitles = arrayOf("2 minutes", "5 minutes",
                "10 minutes","15 minutes","30 minutes","1 hour","3 hours","1 day","5 days", "Never")
        val syncIntervalValues = arrayOf(2, 5,10,15,30,60,180,1440,7200,-1)
        var selectedValue = syncIntervalValues.indexOf(ClassSharedPreferencesSettings(this).getSyncInterval())//select the current day
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.app_icon)
        builder.setTitle("News Update Sync")


        builder.setSingleChoiceItems(syncIntervalTitles,selectedValue) { _, which ->
            selectedValue = which
        }

        builder.setNegativeButton("CANCEL"){ _, _ ->}
        builder.setPositiveButton("OK") { dialog, which ->
            val clickedIntValue = syncIntervalValues[selectedValue]

            when {
                clickedIntValue ==-1 -> news_sync_interval_display.text = "Never"
                clickedIntValue <60 -> news_sync_interval_display.text = "$clickedIntValue Minutes"
                clickedIntValue in 60..180 -> news_sync_interval_display.text = "${(clickedIntValue/(60))} Hours"
                else -> news_sync_interval_display.text = "${(clickedIntValue/(24*60))} Days"
            }
            ClassSharedPreferencesSettings(this).setNewsSyncInterval(clickedIntValue)
        }

// create and show the alert progressDialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun notificationMode(){
        var selectedPosition:Int = ClassSharedPreferencesSettings(this).getNotificationMode()
        // add a list
        val notificationMode = arrayOf("Single", "Batch")
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.app_icon)
        builder.setTitle("Notification Mode")


        builder.setSingleChoiceItems(notificationMode, ClassSharedPreferencesSettings(this).getNotificationMode()) { dialog, which ->
            selectedPosition = which
        }
        builder.setNegativeButton("CANCEL"){ _, _ ->}
        builder.setPositiveButton("OK") { dialog, which ->
            ClassSharedPreferencesSettings(this).setNotificationMode(selectedPosition)
            notification_mode_display.text = notificationMode[selectedPosition]
        }

// create and show the alert progressDialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun deleteOldNewsInterval(){
        // add a list
        val deleteIntervalTitles = arrayOf("1 day", "2 days", "3 days","4 days","5 days","10 days")
        val deleteIntervalValues = arrayOf(1, 2, 3, 4, 5, 10)
        var selectedValue = deleteIntervalValues.indexOf(ClassSharedPreferencesSettings(this).getNewsDeleteInterval())//selete the current day

        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.app_icon)
        builder.setTitle("News Delete Period")


        builder.setSingleChoiceItems(deleteIntervalTitles, selectedValue) { dialog, which ->
            selectedValue = which
        }
        builder.setNegativeButton("CANCEL"){ _, _ ->}
        builder.setPositiveButton("OK") { dialog, which ->
            val clickedIntValue = deleteIntervalValues[selectedValue]
            val clickedIntTitle = deleteIntervalTitles[selectedValue]

            delete_old_news_interval_display.text = "After ${clickedIntTitle}Days"
            ClassSharedPreferencesSettings(this).setNewsDeleteInterval(clickedIntValue)
            ClassSharedPreferencesSettings(this).setNextNewsDeleteInterval((System.currentTimeMillis()/1000)+(clickedIntValue*3600))//current system seconds + no of days selected seconds(multiply by 3600)

        }

// create and show the alert progressDialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun clearBookmarks(){
        AlertDialog.Builder(this)
                .setMessage("Clear Bookmarks")
                .setPositiveButton("Clear"
                ) { dialog, id ->
                    pDialog.createDialog()

                    val allNews = newsListDBHelper.readAllNewsList(0,"all_the_news")
                    for(i in allNews){
                        newsListDBHelper.updateNewsBookmarkStatus(i.news_id,"unstar")
                    }
                    ClassAlertDialog(this).alertMessage("Bookmarks successfully cleared")
                    pDialog.dismissDialog()
                }.setNegativeButton("Cancel"
                ) { dialog, id ->

                }
                .show()
    }
    private fun eraseAllNews(){
        AlertDialog.Builder(this)
                .setMessage("Erase all the saved news?")
                .setPositiveButton("Erase"
                ) { dialog, id ->
                    pDialog.createDialog()

                    val allNews = newsListDBHelper.readAllNewsList(0,"all_the_news")
                    for(i in allNews){
                        if (i.news_is_bookmarked == 1)continue
                        newsListDBHelper.deleteNews(i.news_id.toString())
                    }
                    ClassAlertDialog(this).alertMessage("Saved News successfully erased")
                    pDialog.dismissDialog()
                }.setNegativeButton("Cancel"
                ) {
                    _, _ ->

                }
                .show()
    }
    private fun backupBookmarksDialog(){
        AlertDialog.Builder(this)
                .setMessage("Back up all the Bookmarks to Online Server?")
                .setPositiveButton("OK"
                ) { dialog, id ->
                    val bookArrays = mutableListOf<Int>()
                    val allNews = newsListDBHelper.readAllNewsList(0,"all_the_news")
                    for(i in allNews){
                        if (i.news_is_bookmarked == 1){
                            bookArrays.add(i.news_id)
                        }
                    }
                    if(bookArrays.size !=0){
                        backUpBookmark(bookArrays)
                    }else{
                        ClassAlertDialog(this).alertMessage("There's no bookmark at the moment")
                    }
                }.setNegativeButton("Cancel"
                ) { dialog, id ->

                }
                .show()
    }

    private fun backUpBookmark(data:MutableList<Int>){

        pDialog.createDialog()
        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_BACKUP_BOOKMARKS,
                Response.Listener<String> { response ->
                    pDialog.dismissDialog()

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            ClassAlertDialog(this).alertMessage("Bookmarks successfully backed up")
                        } else {
                            Toast.makeText(this, "An error occurred while backing up data... ", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    pDialog.dismissDialog()
                    ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "back_up"
                params["user_id"] = ClassSharedPreferences(thisContext).getUserId()
                params["data"] = data.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }

    private fun restoreBackupDialog(){
        AlertDialog.Builder(this)
                .setMessage("Restore your Bookmarks from online server?")
                .setPositiveButton("Restore"
                ) { dialog, id ->
                    restoreBackup()
                }.setNegativeButton("Cancel"
                ) { dialog, id ->

                }
                .show()
    }
    private fun restoreBackup() {

        pDialog.createDialog()
        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_RESTORE_BOOKMARKS,
                Response.Listener<String> { response ->
                    pDialog.dismissDialog()

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            ClassAlertDialog(this).alertMessage("Bookmarks Restored...")

                            val noOfDev = obj.getJSONArray("news_backup_arraysz")
                            for (i in 0 until noOfDev.length()) {
                                val eachDevotion = noOfDev.getJSONObject(i)
                                if (!newsListDBHelper.checkIfNewsExist(eachDevotion.getInt("news_id"))){
                                    //download img
                                    val addEachNewsRow = NewsListClassBinder(
                                            1,
                                            eachDevotion.getInt("news_id"),
                                            eachDevotion.getInt("news_list_id"),
                                            eachDevotion.getInt("news_category"),
                                            eachDevotion.getString("news_url"),
                                            eachDevotion.getString("news_title"),
                                            eachDevotion.getString("news_img_urls"),
                                            eachDevotion.getString("news_content_body"),
                                            eachDevotion.getInt("news_date_int"),
                                            eachDevotion.getInt("news_no_of_views"),
                                            eachDevotion.getInt("news_no_of_comments"),
                                            1
                                    )
                                    newsListDBHelper.insertNews(addEachNewsRow)
                                }else{
                                    //update already added news
                                    newsListDBHelper.updateNewsBookmarkStatus(eachDevotion.getInt("news_id"),"star")
                                }

                            }
                        } else {
                            Toast.makeText(this, obj.getString("msg"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    pDialog.dismissDialog()
                    ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "restore_backup"
                params["user_id"] = ClassSharedPreferences(thisContext).getUserId()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end
    }


    fun newsUpdateInterval(){
        val newsList = newsListDBHelper.readNewsListDetails()
        val newsTitle = arrayListOf<String>()
        val newsId = arrayListOf<Int>()
        for (element in newsList) {
            newsTitle.add(element[2])
            newsId.add(element[0].toInt())
        }
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an animal")

        // add a list
        builder.setItems(newsTitle.toTypedArray()) { dialog, which ->
            val clickedNewsId = newsId[which]
            Toast.makeText(this, clickedNewsId.toString(), Toast.LENGTH_LONG).show()
        }

// create and show the alert progressDialog
        val dialog = builder.create()
        dialog.show()
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {super.onBackPressed();return true }
            R.id.menu_settings -> {
                ClassSharedPreferencesSettings(this).resetSettingsPreference()
                finish()
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }








    private fun loginRequired(){
        AlertDialog.Builder(this)
                .setTitle("Login Required!")
                .setMessage("You need to login to your account to perfome this operation")
                .setPositiveButton("Ok"
                ) { _, _ ->
                    dialogShow()
                }.setNegativeButton("Cancel"
                ) { _, _ ->
                    //                     alertDialog.d
                }
                .show()
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
}
