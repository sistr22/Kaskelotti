package com.sistr.kaskelotti

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sistr.kaskelotti.tense.PresentFragment
import kotlinx.android.synthetic.main.verb_fragment.*
import kotlin.math.abs
import kotlin.math.max


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
        Log.d(_tag, "Nb results: ${cursor.count}")
        for(i in 0 until cursor.columnCount)
        {
            Log.d(_tag, "column [$i]: ${cursor.getColumnName(i)}")
        }
        _verb = Verb(cursor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.verb_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title_verb.text = _verb.aInfinitiivi
        pager.adapter = ScreenSlidePagerAdapter(this)
        pager.offscreenPageLimit = 3

        val pageMargin = 20
        val pageOffset = 100

        pager.setPageTransformer{ page, position ->
            val myOffset = position * -(2 * pageOffset + pageMargin)
            when {
                position < -1 -> {
                    page.translationX = -myOffset
                }
                position <= 1 -> {
                    val scaleFactor = max(0.7f, 1 - abs(position - 0.15f))
                    page.translationX = myOffset
                    page.scaleY = scaleFactor
                    page.scaleX = scaleFactor
                    page.alpha = scaleFactor
                }
                else -> {
                    page.alpha = .0f
                    page.translationX = myOffset
                }
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return PresentFragment()
        }
    }
}