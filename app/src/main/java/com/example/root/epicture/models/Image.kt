package com.example.root.epicture.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.json.JSONObject
import java.io.Serializable


class Image(val imageUrl: String, var author: String, var name: String, val deleteHash: String, var description: String): Serializable
{
}

class Gallery(val id: String,
              val title: String,
              val description: String,
              datetime: String,
              cover: String,
              accountUrl: String,
              views: Number,
              val link: String,
              val up: Number,
              val down: Number,
              val points: Number,
              val score: Number,
              val images: ArrayList<Image>): Serializable

public fun CreateGalleryFromJson(json: JSONObject): Gallery? {

    Log.e("image", json.toString())
    if (!json.getBoolean("is_album"))
        return null
    val id = json.getString("id")
    val title = json.getString("title")
    val description = json.getString("description")
    val datetime = json.getString("datetime")
    val cover = json.getString("cover")
    val accountUrl = json.getString("account_url")
    val views = json.getInt("views")
    val link = json.getString("link")
    val up = json.getInt("ups")
    val down = json.getInt("downs")
    val points = json.getInt("points")
    val score = json.getInt("score")
    var images= ArrayList<Image>()
    val imageArray = json.getJSONArray("images");
    for (i in 0..imageArray.length() - 1) {
        var img = imageArray.getJSONObject(i);
        if (img!!.getString("type") == "image/jpeg") {
            images!!.add(CreateImageFromJson(img, true))
        }
    }
    return Gallery(id, title, description, datetime, cover, accountUrl, views, link, up, down, points, score, images!!);
}

public fun CreateImageFromJson(json: JSONObject, isGallery: Boolean = false): Image {
    var name = json.getString("title")
    var author = json.getString("account_url")
    var imageUrl = json.getString("link")
    var description = json.getString("description")
    var deleteHash = "null"
    if (!isGallery) deleteHash = json.getString("deletehash")
    return Image(imageUrl, author, name, deleteHash, description)
}
