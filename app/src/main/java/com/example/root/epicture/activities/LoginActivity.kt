package com.example.root.epicture.activities

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.root.epicture.R
import com.example.root.epicture.objects.UserObject


class Login : AppCompatActivity() {

    var imgurWebView: WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imgurWebView = findViewById(R.id.LoginWebView);

        if (intent.hasExtra("disconnect")) {
            Log.d("Disconnect", "LOGGING OUT")
            imgurWebView!!.clearCache(true)
            imgurWebView!!.clearHistory()
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }

        imgurWebView!!.setBackgroundColor(Color.TRANSPARENT);
        val settings = imgurWebView!!.getSettings()
        settings.setSupportMultipleWindows(true);
        imgurWebView!!.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=" + UserObject.clientId + "&response_type=token&state=APPLICATION_STATE");
        imgurWebView!!.getSettings().setJavaScriptEnabled(true);
        imgurWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.contains("https://org.barroso.epicture/")) {
                    splitUrl(url, view!!)
                    val mainActivity = Intent(this@Login, MainActivity::class.java)
                    startActivity(mainActivity)
                    finish()
                } else {
                    imgurWebView!!.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=" + UserObject.clientId + "&response_type=token&state=APPLICATION_STATE")
                }
                return true
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun login(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent);
    }


    fun splitUrl(url: String, view: WebView) {
        var outerSplit = url.split("#")[1].split("&");

        var index = 0;
        for (s: String in outerSplit) {
            var innerSplit = s.split("=");
            when (index) {
                0 -> {
                    UserObject.token = innerSplit[1];
                }
                3 -> {
                    UserObject.refreshToken = innerSplit[1];
                }
                4 -> {
                    UserObject.username = innerSplit[1]

                }
            }
            index++;
        }
    }
}
