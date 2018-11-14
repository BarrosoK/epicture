package com.example.root.epicture.activities

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.root.epicture.models.Image
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.graphics.Bitmap
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.*
import com.example.root.epicture.R
import com.example.root.epicture.objects.UserObject
import java.io.File
import java.io.FileOutputStream
import java.util.*


class DetailActivity : AppCompatActivity() {

    var deleteHash: String? = null
    var image: Image? = null
    var _btnSave: Button? = null
    var imageView: ImageView? = null
    var isImageFitToScreen: Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var intent: Intent = getIntent()

        image = intent.extras.get("image") as Image


        imageView = findViewById(R.id.imageViewDetail)
        var textViewName: TextView = findViewById(R.id.textViewDetailName)
        var textViewAuthor: TextView = findViewById(R.id.textViewDetailAuthor)
        var textViewDesc: TextView = findViewById(R.id.textViewDetailDesc)

        _btnSave = findViewById(R.id.detail_btnSave)

        if (image!!.name == "null" ) image!!.name = "No title"
        if (image!!.author == "null") image!!.author = "Anon"
        if (image!!.description == "null") image!!.description = "No description"

        Picasso.get().load(image!!.imageUrl).into(imageView)
        textViewName.text = image!!.name
        textViewAuthor.text = image!!.author
        textViewDesc.text = image!!.description
        deleteHash = image!!.deleteHash

        imageView!!.setOnClickListener {


        }
    }

    fun copyUrl(view: View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("url", image!!.imageUrl);
        clipboard.primaryClip = clip
        Toast.makeText(
            getApplicationContext(), "Link Copied",
            Toast.LENGTH_SHORT
        ).show();
    }

    fun save(view: View) {
        val draw = imageView!!.drawable as BitmapDrawable
        var bitmap: Bitmap = draw.getBitmap();



        if (ContextCompat.checkSelfPermission(
                this@DetailActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {


            var root: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            var myDir: File = File(root + "/epicture");
            myDir.mkdirs();
            var generator: Random = Random();
            var n = 10000;
            n = generator.nextInt(n);
            var fname: String = "i-" + n + "-" + image!!.name + ".jpg";
            var file: File = File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                var out: FileOutputStream = FileOutputStream(file);
                draw.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                Toast.makeText(
                    getApplicationContext(), "Image saved !",
                    Toast.LENGTH_SHORT
                ).show();
                out.flush();
                out.close();
            } catch (e: Exception) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(
                this, arrayOf(file.toString()), null
            ) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }


        } else {
            ActivityCompat.requestPermissions(
                this@DetailActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    fun deleteImage(view: View) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Delete image")
            .setMessage("Are you sure ?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                val url = "https://api.imgur.com/3/account/" + UserObject.username + "/image/" + deleteHash
                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .addHeader("Authorization", "Bearer " + UserObject.token)
                    .build()

                UserObject.client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}
                    override fun onResponse(call: Call, response: Response) {
                        setResult(RESULT_OK)
                        finish()
                    }
                })
            }
            .setNegativeButton("No", null)
            .show();


    }


}
