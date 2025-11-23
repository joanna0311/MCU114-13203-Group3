package com.example.threadhandlerandprogressbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.threadhandlerandprogressbar.WorkViewModel.WorkStatus

class ProgressFragment : Fragment() {

    private val viewModel: WorkViewModel by activityViewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        tvProgress = view.findViewById(R.id.tv_progress)

        viewModel.progressStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is WorkStatus.Idle -> {
                    progressBar.visibility = View.INVISIBLE
                    tvProgress.text = "Idle"
                }
                is WorkStatus.Preparing -> {
                    progressBar.visibility = View.INVISIBLE
                    tvProgress.text = "Preparing..."
                }
                is WorkStatus.InProgress -> {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = status.progress
                    tvProgress.text = "Working ${status.progress} %"
                }
                is WorkStatus.WorkFinished -> {
                    progressBar.visibility = View.INVISIBLE
                    tvProgress.text = "背景工作結束!"
                }
                is WorkStatus.Cancelled -> {
                    progressBar.visibility = View.INVISIBLE
                    tvProgress.text = "Canceled"
                }
            }
        }
    }
}