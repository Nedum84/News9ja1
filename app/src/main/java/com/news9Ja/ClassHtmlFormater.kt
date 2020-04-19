package com.news9Ja

import android.text.Html
import android.text.Spanned



class ClassHtmlFormater {

    @Suppress("DEPRECATION")
    fun fromHtml(html: String?): Spanned {
        val result: Spanned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html!!.replace("&lt;", "<").replace("&gt;", ">"), Html.FROM_HTML_MODE_LEGACY)
        } else {
            result = Html.fromHtml(html!!.replace("&lt;", "<").replace("&gt;", ">"))
//            result = Html.fromHtml("<![CDATA[$html]]>");
        }
        return result
    }

    fun removeHtmlTags(html: String?) :String{
        return html!!.replace("&lt;p&gt;", "").replace("&lt;/p&gt;", "\n")
                .replace("&lt;i&gt;", "").replace("&lt;/i&gt;", "")
                .replace("&lt;a href='", "").replace("&lt;/a&gt;", "")
                .replace("&ldquo;", "\"").replace("&rsquo;", "'")
                .replace("&ldquo;", "\"").replace("&rsquo;", "'")
                .replace("&acirc;", "â").replace("&pound;", "£")
                .replace("&nbsp;", " ").replace("&gt;", "")
                .replace("&not;", "¬")
    }
}