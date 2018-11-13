package com.example.root.epicture

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.root.epicture.models.CreateGalleryFromJson
import com.example.root.epicture.models.Gallery
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable

class SearchFragment : Fragment(), GalleryAdapter.onItemClickListener {
    var swipeContainer: SwipeRefreshLayout? = null

    var _galleries= ArrayList<Gallery>()
    var _images = ArrayList<com.example.root.epicture.models.Image>()

    var _inputView: TextInputLayout? = null
    var _recyclerView: RecyclerView? = null
    var _pbLoad: ProgressBar? = null
    var _adapter: GalleryAdapter? = null
    val _client = OkHttpClient()

    override fun onItemClick(position: Number) {
        val detailIntent: Intent = Intent(this@SearchFragment.context, GalleryActivity::class.java)
        val gallery = _galleries[position.toInt()]
        detailIntent.putExtra("gallery", gallery as Serializable)
        startActivity(detailIntent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.search_button).setOnClickListener(View.OnClickListener {
            searchGallery(_inputView!!.editText!!.text.toString())
        })

        _pbLoad = view.findViewById(R.id.search_pb)
        _inputView = view.findViewById(R.id.search_input)
        _recyclerView = view.findViewById(R.id.search_reciclerView)
        _recyclerView!!.setHasFixedSize(true)
        _recyclerView!!.setLayoutManager(LinearLayoutManager(this.context))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }




    fun searchGallery(search: String) {


        _images.clear()
        _galleries.clear()

        _pbLoad!!.visibility = View.VISIBLE
        val url = "https://api.imgur.com/3/gallery/search/viral/?q=" + search
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + UserObject.token)
            .build()

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {

                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONArray("data");

                for (i in 0..Jdata.length() - 1) {
                    var gallery = CreateGalleryFromJson(Jdata.getJSONObject(i));
                    if (gallery != null && gallery!!.images.size > 0)
                        _galleries.add(gallery)
                }

                _adapter = GalleryAdapter(this@SearchFragment.context!!, _galleries)
                activity!!.runOnUiThread {
                    _pbLoad!!.visibility = View.GONE
                    _recyclerView!!.adapter = _adapter
                    _adapter!!.setOnItemClickListener(this@SearchFragment)
                }
            }
        })
    }
}
