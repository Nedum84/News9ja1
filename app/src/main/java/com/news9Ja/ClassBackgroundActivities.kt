package com.news9Ja

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.lang.ref.WeakReference


class ImageDownload (var context: Context, var imageView: ImageView) : AsyncTask<String, Int, Bitmap>() {
    var bitmap: Bitmap? = null
    var inputStream: InputStream? = null
    var responseCode = -1
    override fun onPreExecute() {


    }

    override fun doInBackground(vararg params: String): Bitmap? {

        var url: URL? = null
        try {
            url = URL(params[0])

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.doOutput = true
            httpURLConnection.connect()
            responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream!!.close()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onPostExecute(data: Bitmap?) {
        imageView.setImageBitmap(data)
//        SQLiteNewsDBHelper(context).insertImg(data)
        SQLiteNewsDBHelper(context).insertNewsImg(data,53)
//        Toast.makeText(context, SQLiteNewsDBHelper(context).numberOfRowsTestTable().toString(), Toast.LENGTH_SHORT).show();
    }

    fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    fun saveBitmapToJPEGFile(theTempBitmap: Bitmap?, theTargetFile: File): Boolean? {
        var result: Boolean? = true
        if (theTempBitmap != null) {
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(theTargetFile)
                theTempBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)    //kdfsJpegCompressionRatio
            } catch (e: FileNotFoundException) {
                result = false
                e.printStackTrace()
            }

            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else {
            result = false
        }
        return result
    }
}

class ImageBitmapDownload (context: Context, val news_id: Int) : AsyncTask<String, Int, Bitmap>() {
    private val activityReference: WeakReference<Context> = WeakReference(context)
//    private val activityReferenceForImg: WeakReference<ImageView> = WeakReference(imageView)
    lateinit var newsListDBHelper : SQLiteNewsDBHelper
    var bitmap: Bitmap? = null
    private var inputStream: InputStream? = null
    private var responseCode = -1


    override fun onPreExecute() { }
    override fun doInBackground(vararg params: String): Bitmap? {

        val url: URL?
        try {
            url = URL(params[0])

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.doOutput = true
            try {//try connecting...
                httpURLConnection.connect()
                responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.inputStream
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onPostExecute(bitmapData: Bitmap?) {
        if (bitmapData == null)return//return if bitmap data is null
        val thisContext = activityReference.get() ?: return//returns if thisContext is null

//        activityReferenceForImg.get()?.setImageBitmap(bitmapData)
        SQLiteNewsDBHelper(thisContext).insertNewsImg(bitmapData,news_id)
//        Toast.makeText(context, SQLiteNewsDBHelper(context).numberOfRowsTestTable().toString(), Toast.LENGTH_SHORT).show();
    }

}

class ImgDownloadForNotification internal constructor(context: Context, val news_title: String, val notify_news_id: String, news_source_id:Int = 1) : AsyncTask<String, Int, Bitmap>() {
    private val activityReference: WeakReference<Context> = WeakReference(context)

    private val newsSrcIcon = UrlHolder.getNewsSourceImage(context,news_source_id)
    private var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.n9_banner)!!
    var inputStream: InputStream? = null
    var responseCode = -1


    override fun doInBackground(vararg params: String): Bitmap? {

        val url: URL?
        try {
            url = URL(params[0])

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.doOutput = true
            try {//try connecting...
                httpURLConnection.connect()
                responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.inputStream
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onPostExecute(bitmapData: Bitmap?) {
        if (bitmapData == null)return//return if bitmap data is null

        val thisContext = activityReference.get() ?: return//returns if thisContext is null
        if(ClassSharedPreferencesSettings(thisContext).getNotificationMode() == 1) {//0=single or 1=batch
//            ClassNotification(thisContext).sendNewsUpdates()
        }else{
            ClassNotification(thisContext).sendNewsUpdatesSingle(news_title, notify_news_id, bitmapData)
        }
    }
}

class LoadImg (var context: Context, var imageView: ImageView, val news_id:Int): AsyncTask<String, Void, String>() {
    var newsListDBHelper = SQLiteNewsDBHelper(context)
    var imgUrl:String = ""

    override fun doInBackground(vararg params: String?): String? {
        imgUrl = params[0]!!

        return imgUrl
    }


    override fun onPostExecute(filePath: String?) {

        if (newsListDBHelper.checkIfNewsImgExist(news_id)){
            imageView.setImageBitmap(newsListDBHelper.getNewsImage(news_id))
        }else{
            Glide.with(context)
                    .load(imgUrl)
                    .apply(RequestOptions()
//                            .placeholder(R.drawable.ic_tune_black_24dp)//default image on loading
                            .error(R.drawable.n9_logo1)//without n/w, this img shows
                            .dontAnimate()
                            .fitCenter()
                    )
                    .thumbnail(.1f)
                    .into(imageView)
        }
    }

}

class MyThreadUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        Log.e("uncaughtExceptionError1", "Received exception '" + ex.message + "' from thread " + thread.name, ex)
        Log.e("uncaughtExceptionError2" + Thread.currentThread().stackTrace[2], ex.localizedMessage)
    }
}
