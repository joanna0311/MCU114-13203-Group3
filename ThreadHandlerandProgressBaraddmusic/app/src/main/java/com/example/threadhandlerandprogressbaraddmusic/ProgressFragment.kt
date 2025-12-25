package com.example.threadhandlerandprogressbaraddmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
// 1. 匯入自動生成的 View Binding 類別
import com.example.threadhandlerandprogressbaraddmusic.databinding.FragProgressBinding

class ProgressFragment : Fragment() {

    private val viewModel: WorkViewModel by activityViewModels()

    // 2. 宣告 View Binding 的變數
    private var _binding: FragProgressBinding? = null
    // 這個屬性只在 onCreateView 和 onDestroyView 之間有效
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 3. 使用 View Binding 來加載佈局
        _binding = FragProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 4. 分別觀察 'status' 和 'progress' 這兩個 LiveData
        viewModel.status.observe(viewLifecycleOwner) { statusText ->
            // 更新文字狀態
            binding.tvProgress.text = statusText

            // 根據狀態文字來決定進度條的顯示或隱藏
            if (statusText == "Working…") {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        viewModel.progress.observe(viewLifecycleOwner) { progressValue ->
            // 更新進度條的進度
            binding.progressBar.progress = progressValue

            // 如果正在工作中，也更新文字以包含百分比
            if (binding.progressBar.visibility == View.VISIBLE) {
                binding.tvProgress.text = "Working ${progressValue} %"
            }
        }
    }

    // 5. 在 Fragment 銷毀其 View 時，清理 binding 以避免記憶體洩漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
