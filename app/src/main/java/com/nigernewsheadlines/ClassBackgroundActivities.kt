package com.nigernewsheadlines

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

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
        Toast.makeText(context, SQLiteNewsDBHelper(context).numberOfRowsTestTable().toString(), Toast.LENGTH_SHORT).show();
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


class ClassBackgroundActivities : AsyncTask<String, Void, String>() {
    private var downloadPath:String? = ""

    override fun doInBackground(vararg params: String?): String? {
        // TODO Auto-generated method stub
        try {
            val file: File
            val inputStream: InputStream
//            val path = Environment.getExternalStorageDirectory(Environment.DIRECTORY_MUSIC)
//            file = File(path, "DemoPicture.jpg")
           file  = File(Environment.getExternalStorageDirectory().toString() + "/FileEnc/"+(19990900..1009999900).shuffled().last()+".jpg")
            try {
//                Toast.makeText(get, "An error occurred while loading data... ", Toast.LENGTH_LONG).show()
                // Make sure the Pictures directory exists.
//                path.mkdirs()

//            val url = URL("http://www.pacuss.com/images/savvy.jpg")
                downloadPath = params[0]
                val url = URL("http://www.pacuss.com/images/savvy.jpg")
                /* Open a connection to that URL. */
                val ucon = url.openConnection()

                /*
                 * Define InputStreams to read from the URLConnection.
                 */
                inputStream = ucon.getInputStream()

                val os = FileOutputStream(file)
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                os.write(data)
                inputStream.close()
                os.close()

            } catch (e: IOException) {
                Log.d("ImageManager", "Error: $e")
            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }


        return null

    }


    override fun onPostExecute(filePath: String?) {
        if (filePath != null) {
            //save image path to database
//            Toast.makeText(this, "An error occurred while loading data... ", Toast.LENGTH_LONG).show()
        } else {

        }
    }

}