package com.example.root.epicture.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.root.epicture.adapters.GalleryAdapter
import com.example.root.epicture.R
import com.example.root.epicture.objects.UserObject
import com.example.root.epicture.activities.GalleryActivity
import com.example.root.epicture.models.CreateGalleryFromJson
import com.example.root.epicture.models.Gallery
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable


class SearchFragment : Fragment(), GalleryAdapter.onItemClickListener, AdapterView.OnItemSelectedListener {
    var swipeContainer: SwipeRefreshLayout? = null

    var _galleries = ArrayList<Gallery>()
    var _images = ArrayList<com.example.root.epicture.models.Image>()

    var _linearLayoutFitler: LinearLayout? = null

    var _inputView: TextInputLayout? = null
    var _recyclerView: RecyclerView? = null
    var _pbLoad: ProgressBar? = null
    var _adapter: GalleryAdapter? = null
    val _client = OkHttpClient()

    var _filter: String = ""
    var _window: String = ""

    var _dropdownFilter: Spinner? = null
    var _dropdownWindow: Spinner? = null
    var _items: Array<String>? = null
    var _itemsWindow: Array<String>? = null

    var _spinner_adapter: ArrayAdapter<String>? = null
    var _spinner_adapterWindow: ArrayAdapter<String>? = null


    override fun onItemClick(position: Number) {
        val detailIntent: Intent = Intent(this@SearchFragment.context, GalleryActivity::class.java)
        val gallery = _galleries[position.toInt()]
        detailIntent.putExtra("gallery", gallery as Serializable)
        startActivity(detailIntent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, null)
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.spinner_window -> {
                _window = parent.getItemAtPosition(position).toString()
            }
            R.id.spinner_sort -> {
                _filter = parent.getItemAtPosition(position).toString()
            }
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search button
        view.findViewById<ImageButton>(R.id.search_button).setOnClickListener(View.OnClickListener {
            searchGallery(_inputView!!.editText!!.text.toString())
        })
        // Filter button
        view.findViewById<ImageButton>(R.id.search_button_filter)!!.setOnClickListener {
            when (_linearLayoutFitler!!.visibility) {
                View.GONE -> _linearLayoutFitler!!.visibility = View.VISIBLE
                View.VISIBLE -> _linearLayoutFitler!!.visibility = View.GONE
            }
        }

        _linearLayoutFitler = view.findViewById(R.id.search_filter)

        _dropdownFilter = view.findViewById(R.id.spinner_sort)
        _items = arrayOf("viral", "top", "time")
        _spinner_adapter =
                ArrayAdapter(this@SearchFragment.context, android.R.layout.simple_spinner_dropdown_item, _items)
        _dropdownFilter!!.setAdapter(_spinner_adapter)
        _dropdownFilter!!.setOnItemSelectedListener(this@SearchFragment)

        _dropdownWindow = view.findViewById(R.id.spinner_window)
        _itemsWindow = arrayOf("day", "week", "month", "year", "all")
        _spinner_adapterWindow =
                ArrayAdapter(this@SearchFragment.context, android.R.layout.simple_spinner_dropdown_item, _itemsWindow)
        _dropdownWindow!!.adapter = _spinner_adapterWindow
        _dropdownWindow!!.setOnItemSelectedListener(this@SearchFragment)

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
        val url = "https://api.imgur.com/3/gallery/search/" + _filter + "/" + _window + "?q=" + search
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
