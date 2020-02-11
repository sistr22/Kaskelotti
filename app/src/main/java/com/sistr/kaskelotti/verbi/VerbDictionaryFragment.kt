package com.sistr.kaskelotti.verbi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sistr.kaskelotti.R

class VerbDictionaryFragment : Fragment() {
    private val viewModel: VerbDictionaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.verb_dictionary, container, false)
    }
}