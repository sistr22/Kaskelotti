package com.sistr.kaskelotti

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.verb_fragment.*

class VerbFragment : Fragment() {
    private val _tag = "VerbFragment"
    private lateinit var _verb: Verb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the type of the word (verb, noums, adverbe, etc)
        val cursor = activity!!.contentResolver.query(
            Uri.parse("content://com.sistr.kaskelotti.provider/verbs/${arguments?.getInt("_id")}"),
            null,
            null,
            null,
            null
        )
        cursor!!
        _verb = Verb(cursor)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.verb_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        title_verb.text = _verb.aInfinitiivi
    }
}