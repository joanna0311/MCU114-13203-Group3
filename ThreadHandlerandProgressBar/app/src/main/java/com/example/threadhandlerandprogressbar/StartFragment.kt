package com.example.threadhandlerandprogressbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class StartFragment : Fragment() {

    private val viewModel: WorkViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 您的版面檔名是 frag_start.xml，請確認
        return inflater.inflate(R.layout.frag_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnStart = view.findViewById<Button>(R.id.btnStart)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnStart.setOnClickListener {
            viewModel.startWork()
        }

        btnCancel.setOnClickListener {
            viewModel.cancelWork()
        }
    }
}
