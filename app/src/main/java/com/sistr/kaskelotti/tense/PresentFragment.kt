package com.sistr.kaskelotti.tense

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sistr.kaskelotti.*

import kotlinx.android.synthetic.main.fragment_present.*

class PresentFragment(verb: Verb) : Fragment() {
    private val _verb: Verb = verb

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_present, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_view_vartalo.text = _verb.vartalo_heikko

        val pronounTextFields = arrayOf(
            text_view_mina,
            text_view_sina,
            text_view_han,
            text_view_me,
            text_view_te,
            text_view_he
        )
        for(pronoun in Pronoun.values())
        {
            pronounTextFields[pronoun.ordinal].text = _verb.tenses[Tense.PRESENT]!![pronoun.ordinal].first
        }
    }
}
