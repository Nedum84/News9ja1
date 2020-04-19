package com.news9Ja

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_about_app.*


class ActivityAboutApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        handleAboutOperations()
        app_version_span.text = "Version "+ BuildConfig.VERSION_NAME

        Slidr.attach(this)//for slidr swipe lib
    }
    private fun handleAboutOperations(){

        update_to_latest_version.setOnClickListener {
            ClassAlertDialog(this).redirectToPlayStore()
        }
        share_app.setOnClickListener {
            ClassShareApp(this).shareApp()
        }
        rate_this_app.setOnClickListener {
            ClassAlertDialog(this).rateApp()
        }
        email_us.setOnClickListener {
            val intent  =  Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.app_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Message from "+getString(R.string.app_name))
//            intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")

            startActivity(Intent.createChooser(intent, "Send Email"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.about_app, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {super.onBackPressed();return true }
            R.id.menu_share_app -> {
                ClassShareApp(this).shareApp()
            }
            R.id.menu_settings -> {
                startActivity(Intent(this,ActivitySettings::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
