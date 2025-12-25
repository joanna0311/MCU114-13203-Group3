package com.example.threadhandlerandprogressbaraddmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
// 1. 匯入自動生成的 View Binding 類別
import com.example.threadhandlerandprogressbaraddmusic.databinding.FragStartBinding

class StartFragment : Fragment() {

    private val viewModel: WorkViewModel by activityViewModels()

    // 2. 宣告 View Binding 的變數
    private var _binding: FragStartBinding? = null
    // 這個屬性只在 onCreateView 和 onDestroyView 之間有效，可以避免空指針
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 3. 使用 View Binding 來加載佈局
        _binding = FragStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 4. 使用 binding 安全地存取按鈕
        // 5. 呼叫 ViewModel 中正確的方法名稱：start() 和 cancel()
        binding.btnStart.setOnClickListener {
            viewModel.start() // 不是 startWork()
        }

        binding.btnCancel.setOnClickListener {
            viewModel.cancel() // 不是 cancelWork()
        }
    }

    // 6. 在 Fragment 的 View 被銷毀時，清理 binding 以避免記憶體洩漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
