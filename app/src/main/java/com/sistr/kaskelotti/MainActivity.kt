package com.sistr.kaskelotti

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        findViewById<SearchView>(R.id.search_view).apply {
            setSearchableInfo(searchManager.getSearchableInfo(
                ComponentName(this@MainActivity, SearchActivity::class.java)))
            setIconifiedByDefault(false)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent == null)
            return
        if (Intent.ACTION_VIEW == intent.action) {
            Log.d(TAG, "ACTION_VIEW intent")
        }
    }
}
