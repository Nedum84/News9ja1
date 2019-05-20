package com.nigernewsheadlines


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.database.DatabaseUtils
import android.graphics.BitmapFactory




class SQLiteNewsDBHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_ENTRIES_FOR_NEWS_LIST)
        db.execSQL(SQL_CREATE_ENTRIES_FOR_NEWS_CATEGORY)
        db.execSQL(SQL_CREATE_ENTRIES_FOR_DOWNLOADED_IMGS)
        db.execSQL(SQL_CREATE_ENTRIES_FOR_DELETED_NEWS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_ENTRIES_FOR_NEWS_LIST)
        db.execSQL(SQL_DELETE_ENTRIES_FOR_NEWS_CATEGORY)
        db.execSQL(SQL_DELETE_ENTRIES_FOR_NEWS_CATEGORY)
        db.execSQL(SQL_DELETE_ENTRIES_FOR_DELETED_NEWS)
        db.execSQL(SQL_DELETE_ENTRIES_FOR_DOWNLOADED_IMGS)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertNews(news: NewsListClassBinder): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
//        values.put(TABLE_ID, news.table_id)
        values.put(NEWS_ID, news.news_id)
        values.put(NEWS_LIST_ID, news.news_list_id)
        values.put(NEWS_CATEGORY, news.news_category)
        values.put(NEWS_URL, news.news_url)
        values.put(NEWS_TITLE, news.news_title)
        values.put(NEWS_IMG_URLS, news.news_img_urls)
//        values.put(NEWS_IMG_URL_DOWNLOAD_NAME, news.news_img_url_download_name)
        values.put(NEWS_CONTENT_BODY, news.news_content_body)
        values.put(NEWS_DATE_INT, news.news_date_int)
        values.put(NEWS_NO_OF_VIEWS, news.news_no_of_views)
        values.put(NEWS_NO_OF_COMMENTS, news.news_no_of_comments)
        values.put(NEWS_HIDE, 0)
        values.put(NEWS_IS_BOOKMARKED, 0)
        // Insert the new row, returning the primary key value of the new row
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteNews(news_id: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query. & // Specify arguments in placeholder order. &
        // Specify arguments in placeholder order.
        // Issue SQL statement.
        val _success = db.delete(TABLE_NAME, "$NEWS_ID = ?", arrayOf(news_id)).toLong()

        insertDeletedNewsId(news_id.toInt())//adding to news deleted table
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    //update bookmark status
    fun updateNewsBookmarkStatus(news_id: Int?, action:String = "star"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        if(action == "star"){
            values.put(NEWS_IS_BOOKMARKED, 1)
        }else{
            values.put(NEWS_IS_BOOKMARKED, 0)
        }
        val _success = db.update(TABLE_NAME, values, "$NEWS_ID=?", arrayOf(news_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    //update hide/show status
    fun updateHideAndShowStatus(news_id: Int?, action:String = "hide"): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        if(action == "star"){
            values.put(NEWS_HIDE, 1)
        }else{
            values.put(NEWS_HIDE, 0)
        }
        val _success = db.update(TABLE_NAME, values, "$NEWS_ID=?", arrayOf(news_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    //update all the news row
    fun updateNewsRow(news_id: Int?,news_list_id:Int, news_category:Int, news_url:String,news_title:String,news_img_urls:String, news_img_url_download_name:String,
                      news_content_body:String, news_date_int:Int, news_no_of_views:Int,news_no_of_comments:Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put(NEWS_LIST_ID, news_list_id)
        values.put(NEWS_CATEGORY, news_category)
        values.put(NEWS_URL, news_url)
        values.put(NEWS_TITLE, news_title)
        values.put(NEWS_IMG_URLS, news_img_urls)
//        values.put(NEWS_IMG_URL_DOWNLOAD_NAME, news_img_url_download_name)
        values.put(NEWS_CONTENT_BODY, news_content_body)
        values.put(NEWS_DATE_INT, news_date_int)
        values.put(NEWS_NO_OF_VIEWS, news_no_of_views)
        values.put(NEWS_NO_OF_COMMENTS, news_no_of_comments)

        val _success = db.update(TABLE_NAME, values, "$NEWS_ID=?", arrayOf(news_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun readSingleNewsDetails(news_id: Int): ArrayList<NewsListClassBinder> {

        val news_details = ArrayList<NewsListClassBinder>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $NEWS_ID = $news_id"
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {

//                val imgByte = cursor.getBlob(cursor.getColumnIndex(NEWS_IMG_URL_DOWNLOAD_NAME))
//                val imgFileBimap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)

                news_details.add(NewsListClassBinder(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(TABLE_ID))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_ID))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_LIST_ID))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_CATEGORY))),
                        cursor.getString(cursor.getColumnIndex(NEWS_URL)),
                        cursor.getString(cursor.getColumnIndex(NEWS_TITLE)),
                        cursor.getString(cursor.getColumnIndex(NEWS_IMG_URLS)),
//                        imgFileBimap,
                        cursor.getString(cursor.getColumnIndex(NEWS_CONTENT_BODY)),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_DATE_INT))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_NO_OF_VIEWS))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_NO_OF_COMMENTS))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_HIDE))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_IS_BOOKMARKED)))
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return news_details
    }

    fun readAllNewsList(filter_id:Int?=1, filter_by_cat_or_news_list:String? = "category"): ArrayList<NewsListClassBinder> {
        val newsList = ArrayList<NewsListClassBinder>()
        val db = writableDatabase
        var cursor: Cursor? = null

        try {
            val selectQuery = when (filter_by_cat_or_news_list) {
                "latest_news" -> "SELECT  * FROM $TABLE_NAME ORDER BY $NEWS_ID DESC"
                "category" -> "SELECT  * FROM $TABLE_NAME  WHERE $NEWS_CATEGORY = $filter_id & $NEWS_HIDE !=1 ORDER BY $NEWS_ID DESC"
                "news_list" -> "SELECT  * FROM $TABLE_NAME  WHERE $NEWS_LIST_ID = $filter_id & $NEWS_HIDE !=1 ORDER BY $NEWS_ID DESC"
                "news_bookmark" -> "SELECT  * FROM $TABLE_NAME WHERE $NEWS_IS_BOOKMARKED = 1 ORDER BY $NEWS_ID DESC"
                "single_news" -> "SELECT  * FROM $TABLE_NAME WHERE $NEWS_ID = $filter_id"
                else -> "SELECT  * FROM $TABLE_NAME  WHERE $NEWS_HIDE !=1 ORDER BY $NEWS_ID DESC LIMIT 20"
            }

            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
//                val imgByte = cursor.getBlob(cursor.getColumnIndex(NEWS_IMG_URL_DOWNLOAD_NAME))
//                val imgFileBimap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)

                newsList.add(NewsListClassBinder(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(TABLE_ID))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_ID))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_LIST_ID))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_CATEGORY))),
                        cursor.getString(cursor.getColumnIndex(NEWS_URL)),
                        cursor.getString(cursor.getColumnIndex(NEWS_TITLE)),
                        cursor.getString(cursor.getColumnIndex(NEWS_IMG_URLS)),
//                        imgFileBimap,
                        cursor.getString(cursor.getColumnIndex(NEWS_CONTENT_BODY)),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_DATE_INT))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_NO_OF_VIEWS))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_NO_OF_COMMENTS))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_HIDE))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_IS_BOOKMARKED)))
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return newsList
    }
    fun returnLastNewsId(): Int{
        var lastNewsId = 1
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $NEWS_ID DESC LIMIT 1"
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return 1
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                lastNewsId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_ID)))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return lastNewsId
    }

    fun checkIfNewsExist(news_id: Int?):Boolean{
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            val checkQuery = "SELECT  * FROM $TABLE_NAME WHERE $NEWS_ID = $news_id"
            cursor = db.rawQuery(checkQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
        }
        val cursorCount = cursor!!.count
        cursor.close()
        return cursorCount ==1
    }



    ///for news list and news category

    fun insertNewsIntoTheList(news_list_news_list_id:Int,news_list_news_website:String,news_list_news_name:String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        val values = ContentValues()
//        values.put(TABLE_ID, news.table_id)
        values.put(NEWS_LIST_NEWS_LIST_ID, news_list_news_list_id)
        values.put(NEWS_LIST_NEWS_WEBSITE, news_list_news_website)
        values.put(NEWS_LIST_NEWS_NAME, news_list_news_name)

        val _success = db.insert(TABLE_NAME_NEWS_LIST, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }
    fun updateNewsInTheNewsList(news_list_news_list_id:Int,news_list_news_website:String,news_list_news_name:String): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put(NEWS_LIST_NEWS_WEBSITE, news_list_news_website)
        values.put(NEWS_LIST_NEWS_NAME, news_list_news_name)

        val _success = db.update(TABLE_NAME_NEWS_LIST, values, "$NEWS_LIST_NEWS_LIST_ID=?", arrayOf(news_list_news_list_id.toString())).toLong()
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }
    fun readNewsListDetails(filter_id: Int = 0): MutableList<List<String>> {

        val news_list_details = mutableListOf<List<String>>()
        val db = writableDatabase
        val cursor: Cursor?
        val selectQuery: String?
        try {
            selectQuery = if (filter_id != 0){
                "SELECT  * FROM $TABLE_NAME_NEWS_LIST WHERE $NEWS_LIST_NEWS_LIST_ID = $filter_id"
            }else{
                "SELECT  * FROM $TABLE_NAME_NEWS_LIST"
            }

            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES_FOR_NEWS_LIST)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                news_list_details.add( listOf<String>(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_LIST_NEWS_LIST_ID))).toString(),
                        cursor.getString(cursor.getColumnIndex(NEWS_LIST_NEWS_WEBSITE)),
                        cursor.getString(cursor.getColumnIndex(NEWS_LIST_NEWS_NAME))
                    )

                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return news_list_details
    }


    ///for news category

    fun insertCategory(news_category_cat_id:Int,news_category_cat_name:String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        val values = ContentValues()

        values.put(NEWS_CATEGORY_CAT_ID, news_category_cat_id)
        values.put(NEWS_CATEGORY_CAT_NAME, news_category_cat_name)

        val _success = db.insert(TABLE_NAME_NEWS_CATEGORY, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }
    fun updateCategory(news_category_cat_id:Int,news_category_cat_name:String): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put(NEWS_CATEGORY_CAT_NAME, news_category_cat_name)


        val _success = db.update(TABLE_NAME_NEWS_CATEGORY, values, "$NEWS_CATEGORY_CAT_ID=?", arrayOf(news_category_cat_id.toString())).toLong()
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }
    //read category info
    fun readCategoryDetails(filter_id: Int = 0): MutableList<List<String>> {

        val category_list_details = mutableListOf<List<String>>()
        val db = writableDatabase
        val cursor: Cursor?
        val selectQuery: String?
        try {
            selectQuery = if (filter_id != 0){
                "SELECT  * FROM $TABLE_NAME_NEWS_CATEGORY WHERE $NEWS_CATEGORY_CAT_ID = $filter_id"
            }else{
                "SELECT  * FROM $TABLE_NAME_NEWS_CATEGORY"
            }

            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES_FOR_NEWS_CATEGORY)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                category_list_details.add(listOf<String>(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(NEWS_CATEGORY_CAT_ID))).toString(),
                        cursor.getString(cursor.getColumnIndex(NEWS_CATEGORY_CAT_NAME))
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return category_list_details
    }

    //FOR DELETED NEWS

    fun insertDeletedNewsId(news_id:Int): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        val values = ContentValues()

        values.put(DELETED_NEWS_NEWS_ID, news_id)

        val _success = db.insert(TABLE_NAME_DELETED_NEWS, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }
    fun readDeletedNewsIds(): MutableList<String> {

        val deleted_news = mutableListOf<String>()
        val db = writableDatabase
        val cursor: Cursor?
        val selectQuery: String?
        try {
            selectQuery = "SELECT  * FROM $TABLE_NAME_DELETED_NEWS"
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES_FOR_DELETED_NEWS)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                deleted_news.add(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(DELETED_NEWS_NEWS_ID))).toString()
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return deleted_news
    }


    //DOWNLOADED PURPOSES
    fun checkIfNewsImgExist(news_id: Int?):Boolean{
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            val checkQuery = "SELECT  * FROM $TABLE_NEWS_DOWNLOADED_IMAGES WHERE $NEWS_ID_IMG_DOWNLOAD = $news_id"
            cursor = db.rawQuery(checkQuery, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES_FOR_DOWNLOADED_IMGS)
        }
        val cursorCount = cursor!!.count
        cursor.close()
        return cursorCount ==1
    }
    fun numberOfRowsTestTable(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, TABLE_NEWS_DOWNLOADED_IMAGES).toInt()
    }

    //insert image bitmap to database
    fun insertNewsImg(img: Bitmap?):Boolean {
        val data = getBitmapAsByteArray(img) // this is a function


        val db = writableDatabase
        val values = ContentValues()

        values.put(NEWS_IMG_DOWNLOADED_FILE, data)
        val _success = db.insert(TABLE_NEWS_DOWNLOADED_IMAGES, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)//RETURN TRUE IF SUCCESSFUL OTHERWISE FALSE
    }
    fun getNewsImage(news_id: Int): Bitmap? {
        val db = writableDatabase

        val qu = "select $NEWS_IMG_DOWNLOADED_FILE  from $TABLE_NEWS_DOWNLOADED_IMAGES where $NEWS_ID_IMG_DOWNLOAD=$news_id"
        val cur = db.rawQuery(qu, null)

        if (cur!!.moveToFirst()) {
            val imgByte = cur.getBlob(cur.getColumnIndex(NEWS_IMG_DOWNLOADED_FILE))
            cur.close()
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
        }

        cur.close()

        return null
    }
    fun getBitmapAsByteArray(bitmap: Bitmap?): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(CompressFormat.PNG, 0, outputStream)
        outputStream.close()
        return outputStream.toByteArray()
    }
    //img inserts ends...
    //DOWNLOADED  ENDS





    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION        = 1
        val DATABASE_NAME           = "all_niger_news.db"
        //for news contents
        val TABLE_NAME              = "news_contents"
        val TABLE_ID                = "table_id"
        val NEWS_ID                 = "news_id"
        val NEWS_LIST_ID            = "news_list_id"
        val NEWS_CATEGORY           = "news_category"
        val NEWS_URL                = "news_url"
        val NEWS_TITLE              = "news_title"
        val NEWS_IMG_URLS           = "news_img_urls"
        val NEWS_IMG_URL_DOWNLOAD_NAME = "news_img_url_download_name"
        val NEWS_CONTENT_BODY = "news_content_body"
        val NEWS_DATE_INT           = "news_date_int"
        val NEWS_NO_OF_VIEWS        = "news_no_of_views"
        val NEWS_NO_OF_COMMENTS        = "news_no_of_comments"
        val NEWS_HIDE               = "news_hide"
        val NEWS_IS_BOOKMARKED      = "news_is_bookmarked"

        private val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME " +
                "($TABLE_ID Integer PRIMARY KEY AUTOINCREMENT, $NEWS_ID Integer, $NEWS_LIST_ID Integer, $NEWS_CATEGORY TEXT, $NEWS_URL TEXT, " +
                "$NEWS_TITLE TEXT, $NEWS_IMG_URLS TEXT, $NEWS_IMG_URL_DOWNLOAD_NAME TEXT, $NEWS_CONTENT_BODY TEXT, $NEWS_DATE_INT Integer, " +
                "$NEWS_NO_OF_VIEWS Integer, $NEWS_NO_OF_COMMENTS Integer, $NEWS_HIDE Integer, $NEWS_IS_BOOKMARKED Integer)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"


        //        for news list
        val TABLE_NAME_NEWS_LIST           = "table_name_news_list"
        val NEWS_LIST_NEWS_LIST_ID         = "news_list_news_list_id"
        val NEWS_LIST_NEWS_WEBSITE         = "news_list_news_website"
        val NEWS_LIST_NEWS_NAME            = "news_list_news_name"
        private val SQL_CREATE_ENTRIES_FOR_NEWS_LIST = "CREATE TABLE $TABLE_NAME_NEWS_LIST " +
                "($NEWS_LIST_NEWS_LIST_ID Integer PRIMARY KEY, $NEWS_LIST_NEWS_WEBSITE TEXT, $NEWS_LIST_NEWS_NAME TEXT)"

        private val SQL_DELETE_ENTRIES_FOR_NEWS_LIST = "DROP TABLE IF EXISTS $TABLE_NAME_NEWS_LIST"


        //        for news category
        val TABLE_NAME_NEWS_CATEGORY           = "table_name_news_category"
        val NEWS_CATEGORY_CAT_ID         = "news_category_cat_id"
        val NEWS_CATEGORY_CAT_NAME         = "news_category_cat_name"
        private val SQL_CREATE_ENTRIES_FOR_NEWS_CATEGORY = "CREATE TABLE $TABLE_NAME_NEWS_CATEGORY " +
                "($NEWS_CATEGORY_CAT_ID Integer PRIMARY KEY, $NEWS_CATEGORY_CAT_NAME TEXT)"

        private val SQL_DELETE_ENTRIES_FOR_NEWS_CATEGORY = "DROP TABLE IF EXISTS $TABLE_NAME_NEWS_CATEGORY"


        val TABLE_NAME_DELETED_NEWS = "tableNameDeletedNews"
        val  DELETED_NEWS_TABLE_ID = "deletedNewsTableId"
        val  DELETED_NEWS_NEWS_ID= "deletedNewsId"
        val SQL_CREATE_ENTRIES_FOR_DELETED_NEWS ="CREATE TABLE $TABLE_NAME_DELETED_NEWS " +
                "($DELETED_NEWS_TABLE_ID Integer PRIMARY KEY AUTOINCREMENT, $DELETED_NEWS_NEWS_ID Integer)"

        private val SQL_DELETE_ENTRIES_FOR_DELETED_NEWS = "DROP TABLE IF EXISTS $TABLE_NAME_DELETED_NEWS"




        val TABLE_NEWS_DOWNLOADED_IMAGES = "news_dowloaded_images"
        val TABLE_ID_IMG_DOWNLOAD = "table_id_img_download"
        val NEWS_ID_IMG_DOWNLOAD = "news_id_img_download"
        val NEWS_IMG_DOWNLOADED_FILE = "news_img_downloaded_file"
        val SQL_CREATE_ENTRIES_FOR_DOWNLOADED_IMGS ="CREATE TABLE $TABLE_NEWS_DOWNLOADED_IMAGES " +
                "($TABLE_ID_IMG_DOWNLOAD Integer PRIMARY KEY AUTOINCREMENT, $NEWS_ID_IMG_DOWNLOAD Integer, $NEWS_IMG_DOWNLOADED_FILE BLOB)"


        private val SQL_DELETE_ENTRIES_FOR_DOWNLOADED_IMGS = "DROP TABLE IF EXISTS $TABLE_NEWS_DOWNLOADED_IMAGES"
    }

}