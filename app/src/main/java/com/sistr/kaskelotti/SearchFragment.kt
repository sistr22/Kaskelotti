package com.sistr.kaskelotti

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.search_fragment.*

class SearchFragment : Fragment() {
    private val _tag = "SearchFragment"
    private lateinit var adapter: SimpleCursorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = SimpleCursorAdapter(
            context, R.layout.search_result_item, null,
            arrayOf("suomi", "english"),
            intArrayOf(R.id.word_suomi, R.id.word_translated), 0
        )
        list_view.adapter = adapter
        list_view.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.d(_tag, "onListItemClick[$id]")
        }

        val intent = activity!!.intent
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            search(query)
        }
    }



    private fun search(query: String)
    {
        val projection: Array<String> = arrayOf(
            "suomi",
            "english",
            "_id"
        )

        val cursor = context!!.contentResolver.query(
            Uri.parse("content://com.sistr.kaskelotti.provider/search"),  // The content URI of the words table
            projection,                       // The columns to return for each row
            query,                  // Either null, or the word the user entered
            null,                    // Either empty, or the string the user entered
            null                         // The sort order for the returned rows
        )

        val oldCursor = adapter.swapCursor(cursor)
        oldCursor?.close()

    }
}