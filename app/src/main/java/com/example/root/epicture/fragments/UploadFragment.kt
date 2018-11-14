package com.example.root.epicture.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.root.epicture.R
import com.example.root.epicture.objects.UserObject
import com.example.root.epicture.activities.DetailActivity
import com.example.root.epicture.models.CreateImageFromJson
import okhttp3.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.*


class UploadFragment : Fragment() {


    var _progressBar: ProgressBar? = null
    var _btnBrowse: Button? = null
    var _btnSend: Button? = null
    var _imageView: ImageView? = null
    var _editTitle: TextInputLayout? = null
    var _editDesc: TextInputLayout? = null
    var _text: TextView? = null
    var _selectedImage: Bitmap? = null
    var _anonymeCheckbox: CheckBox? = null
    val _client = OkHttpClient()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            try {
                var imageUri: Uri = data!!.getData();
                var imageStream: InputStream = context!!.getContentResolver().openInputStream(imageUri);
                _selectedImage = BitmapFactory.decodeStream(imageStream);
                _imageView!!.setImageBitmap(_selectedImage);
                _btnSend!!.visibility = View.VISIBLE
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
                Toast.makeText(this@UploadFragment.context, "Something went wrong", Toast.LENGTH_LONG).show();
                _btnSend!!.visibility = View.GONE
            }

        } else {
            Toast.makeText(this@UploadFragment.context, "You haven't picked Image", Toast.LENGTH_LONG).show();
            _btnSend!!.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _btnBrowse = view.findViewById(R.id.upload_browse)
        _btnSend = view.findViewById(R.id.upload_btn_upload)
        _imageView = view.findViewById(R.id.upload_imageView)
        _editTitle = view.findViewById(R.id.upload_title_edit)
        _editDesc = view.findViewById(R.id.upload_desc_edit)
        _progressBar = view.findViewById(R.id.upload_pb)
        _anonymeCheckbox = view.findViewById(R.id.upload_cb_anon)

        _imageView!!.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1001)
        }
        _btnBrowse!!.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1001)
        }
        _btnSend!!.setOnClickListener {
            _btnSend!!.visibility = View.GONE
            _progressBar!!.visibility = View.VISIBLE;
            upload()
        }

    }

    fun encodeTobase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    fun upload() {

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", _editTitle!!.editText!!.text.toString())
            .addFormDataPart("description", _editDesc!!.editText!!.text.toString())
            .addFormDataPart("image", encodeTobase64(_selectedImage!!))
            .build()


        val url = "https://api.imgur.com/3/image"
        var request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Client-ID " + UserObject.clientId)
            .build();

        if (!_anonymeCheckbox!!.isChecked) {
            request = request.newBuilder()
                .header("Authorization", "Bearer " + UserObject.token)
                .build()
        }
        _btnSend!!.visibility = View.GONE

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity!!.runOnUiThread {
                    _progressBar!!.visibility = View.GONE;
                    _btnSend!!.visibility = View.VISIBLE
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONObject("data");

                activity!!.runOnUiThread {
                    _progressBar!!.visibility = View.GONE;
                    _btnSend!!.visibility = View.VISIBLE
                }


                val detailIntent: Intent = Intent(this@UploadFragment.context, DetailActivity::class.java)
                var image = CreateImageFromJson(Jdata)
                if (!_anonymeCheckbox!!.isChecked) image.author = UserObject.profile!!.accountUrl else image.author = "Anon"
                detailIntent.putExtra("image", image as Serializable)
                startActivity(detailIntent)
            }
        })
    }
}
