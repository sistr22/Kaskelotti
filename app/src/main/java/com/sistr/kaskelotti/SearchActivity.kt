package com.sistr.kaskelotti

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {
    val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
            ComponentName(this, SearchActivity::class.java)
        ))
        searchView.setIconifiedByDefault(false)

        // Get the intent, verify the action and get the query
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent == null)
            return
        if (Intent.ACTION_VIEW == intent.action) {
            Log.d(TAG, "ACTION_VIEW intent: ${intent.dataString}")

            // Get the type of the word (verb, noums, adverbe, etc)
            val cursor = contentResolver.query(
                Uri.parse(intent.dataString),
                null,
                null,
                null,
                null
            )

            for(i in 0 until cursor!!.columnCount)
            {
                Log.d(TAG, "column [$i]: ${cursor.getColumnName(i)}")
            }
            cursor.moveToFirst()
            if(cursor.getInt(2) == 0 ) // 0 is for verbs
            {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = VerbFragment()
                val fragmentArgs = Bundle()
                fragmentArgs.putInt("_id", cursor.getInt(0))
                fragment.arguments = fragmentArgs
                fragmentTransaction.replace(R.id.fragment_container, fragment)
                fragmentTransaction.commit()
            }
            cursor.close()
        } else if (Intent.ACTION_SEARCH == intent.action) {
            Log.d(TAG, "ACTION_SEARCH intent")
            setIntent(intent)
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = SearchFragment()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commit()
        }
    }
}
