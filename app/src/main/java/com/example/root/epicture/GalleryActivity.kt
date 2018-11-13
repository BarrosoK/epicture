package com.example.root.epicture

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.root.epicture.models.Image
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.R.attr.label
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.recycler_item.*
import android.graphics.Bitmap
import android.R.attr.bitmap
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.*
import com.example.root.epicture.models.Gallery
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class GalleryActivity : AppCompatActivity(), Adapter.onItemClickListener {




    override fun onItemClick(position: Number) {
        val detailIntent: Intent = Intent(this, DetailActivity::class.java)
        val image = _gallery!!.images[position.toInt()]
        detailIntent.putExtra("image", image as Serializable)
        startActivityForResult(detailIntent, 4242)
    }

    var deleteHash: String? = null
    var _gallery: Gallery? = null
    var _btnSave: Button? = null
    var imageView: ImageView? = null
    var isImageFitToScreen: Boolean = true

    var _recyclerView: RecyclerView? = null
    var _adapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var intent: Intent = getIntent()

        _gallery = intent.extras.get("gallery") as Gallery

        _recyclerView = findViewById(R.id.gallery_reciclerView)
        _recyclerView!!.setHasFixedSize(true)
        _recyclerView!!.layoutManager = LinearLayoutManager(this)
        _adapter = Adapter(this, _gallery!!.images)
        _recyclerView!!.adapter = _adapter
        _adapter!!.setOnItemClickListener(this)
    }



}
