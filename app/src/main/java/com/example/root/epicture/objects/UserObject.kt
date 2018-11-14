package com.example.root.epicture.objects

import android.support.v7.app.AppCompatActivity
import okhttp3.*


class ImgurProfile(val accountUrl: String, val email: String, val avatar: String, val cover: String) {

}

object UserObject: AppCompatActivity() {
    val clientId: String = "8a3f7a5c623a3b9"

    var username: String? = null
    var token: String? = null
    var refreshToken: String? = null
    var profile: ImgurProfile? = null
    val CONNECTON_TIMEOUT_MILLISECONDS = 60000

    val client = OkHttpClient()


    fun test() {
    }


}

