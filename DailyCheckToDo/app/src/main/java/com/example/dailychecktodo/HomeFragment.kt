package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailychecktodo.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // 透過 activityViewModels() 取得共用的 ViewModel
    private val habitViewModel: HabitViewModel by activityViewModels()

    private lateinit var habitAdapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatesRecyclerView()
        setupHabitsRecyclerView()

        // 設定新增習慣按鈕的點擊事件
        binding.btnAddHabit.setOnClickListener {
            habitViewModel.startNewHabit() // 開始一個新的習慣
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, AddHabitFragment())
                .addToBackStack(null)
                .commit()
        }

        // 監聽 ViewModel 中 habits 列表的變化
        habitViewModel.habits.observe(viewLifecycleOwner) { habits ->
            habitAdapter.updateHabits(habits)
        }
    }

    private fun setupDatesRecyclerView() {
        // 1. 精準地取得「今天」
        val today = Calendar.getInstance()

        // 2. 建立一個新的 Calendar 物件來回推到星期一，避免動到「今天」
        val monday = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        val dates = mutableListOf<DateItem>()
        val dayOfWeekFormat = SimpleDateFormat("(E)", Locale.TAIWAN)
        val dayOfMonthFormat = SimpleDateFormat("d", Locale.TAIWAN)

        // 3. 從星期一開始，產生 7 天的日期
        for (i in 0..6) {
            // 4. 精準地比對「年」和「一年中的第幾天」來判斷是否為今天
            val isToday = monday.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        monday.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

            dates.add(
                DateItem(
                    dayOfWeek = dayOfWeekFormat.format(monday.time),
                    dayOfMonth = dayOfMonthFormat.format(monday.time),
                    isSelected = isToday
                )
            )
            monday.add(Calendar.DATE, 1)
        }

        // 5. 改用 GridLayoutManager 來讓日期置中均分
        binding.recyclerViewDates.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.recyclerViewDates.adapter = DateAdapter(dates) { dateItem ->
            // Handle date selection, e.g., filter the habits list
        }
    }

    private fun setupHabitsRecyclerView() {
        habitAdapter = HabitAdapter(mutableListOf()) // 初始化一個空的 adapter
        binding.recyclerViewHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHabits.adapter = habitAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}