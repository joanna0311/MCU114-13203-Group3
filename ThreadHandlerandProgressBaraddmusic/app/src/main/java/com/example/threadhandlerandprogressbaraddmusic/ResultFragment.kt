package com.example.threadhandlerandprogressbaraddmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ResultFragment : Fragment() {

    private val viewModel: WorkViewModel by activityViewModels()
    private lateinit var tvResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvResult = view.findViewById(R.id.tv_result)

        viewModel.status.observe(viewLifecycleOwner) { result ->
            tvResult.text = result
        }
    }
}