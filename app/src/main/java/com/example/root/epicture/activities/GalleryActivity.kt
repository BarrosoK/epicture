package com.example.root.epicture.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import com.example.root.epicture.adapters.ImageAdapter
import com.example.root.epicture.R
import com.example.root.epicture.models.Gallery
import java.io.Serializable


class GalleryActivity : AppCompatActivity(), ImageAdapter.onItemClickListener {




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
    var _Image_adapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var intent: Intent = getIntent()

        _gallery = intent.extras.get("gallery") as Gallery

        _recyclerView = findViewById(R.id.gallery_reciclerView)
        _recyclerView!!.setHasFixedSize(true)
        _recyclerView!!.layoutManager = LinearLayoutManager(this)
        _Image_adapter = ImageAdapter(this, _gallery!!.images)
        _recyclerView!!.adapter = _Image_adapter
        _Image_adapter!!.setOnItemClickListener(this)
    }



}
