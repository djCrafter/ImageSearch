package com.example.imagesearch

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject



class MainActivity : AppCompatActivity() {

    private var key = "AIzaSyAexd7qwxuPXfi-1Jumvw7dcl2B5Z1lmM8" // API key
    private var cx = "010314913896689148381:nmmerom-oxi" // search engine key
    private var query = ""

    private var pageNumber = 1
    private val NUMBER_OF_SEARCH_RESULT = 8

    private lateinit var searchline : EditText
    private var queryString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null){
            queryString = savedInstanceState.getString("queryString")
            okButtonHandler(null)
        }

        searchline = findViewById(R.id.searchLine)

        okButton.setOnClickListener { okButtonHandler(okButton) }

        nextButton.setOnClickListener {
            pageNumber += NUMBER_OF_SEARCH_RESULT
            okButtonHandler(null)
        }

        previosButton.setOnClickListener {
            pageNumber -= NUMBER_OF_SEARCH_RESULT
            okButtonHandler(null)
            if(pageNumber <= 6){
                previosButton.isEnabled = false
            }
        }
    }

     override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("queryString", queryString)
    }

     private fun okButtonHandler(view: View?) {
        if(view != null) {
            // The keyboard will be removed
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            //rewrite string
            queryString = searchline.text.toString()
            pageNumber = 1
        }

        if(pageNumber > 1){
            previosButton.isEnabled = true
        }

        if(queryString.count() > 0) {
            query = queryString.replace(" ", "+")
            val url = "https://www.googleapis.com/customsearch/v1?searchType=image&start=$pageNumber&num=$NUMBER_OF_SEARCH_RESULT&q=$query&key=$key&cx=$cx&alt=json"

            loadLinks(url)
        }
    }

    private fun loadLinks(url: String){
        Ion.with(this)
            .load(url)
            .asString()
            .setCallback { _, obj ->

                gridView.removeAllViews()

                val jsonArr = JSONObject(obj).optJSONArray("items")
                if(jsonArr != null) {
                  for(i in 0..(jsonArr.length() - 1)) {
                      val item = jsonArr.getJSONObject(i).getString("link")
                      loadImg(item)
                  }
                }
            }
    }

    private fun loadImg(link: String){

        var img = ImageView(baseContext)
        img.scaleType = ImageView.ScaleType.CENTER
        var width = getApplicationContext().getResources().getDisplayMetrics().widthPixels

        img.setPadding(5, 5, 5, 5)

        Picasso.get()
            .load(link)
            .error(R.drawable.no_img)
            .resize(width/2, width/4)
            .centerCrop()
            .into(img)

        gridView.addView(img)
    }
}



