package com.example.dailychecktodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dailychecktodo.databinding.FragmentAddHabitBinding

class AddHabitFragment : Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!

    private val habitViewModel: HabitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitViewModel.editingHabit.observe(viewLifecycleOwner) { habit ->
            habit?.let {
                // 當 ViewModel 中的資料變化時，更新 UI
                if (binding.tilHabitTitle.editText?.text.toString() != it.name) {
                    binding.tilHabitTitle.editText?.setText(it.name)
                }
                if (binding.tilHabitNotes.editText?.text.toString() != it.notes) {
                    binding.tilHabitNotes.editText?.setText(it.notes)
                }
            }
        }

        // 當使用者輸入時，即時更新 ViewModel
        binding.tilHabitTitle.editText?.doOnTextChanged { text, _, _, _ ->
            habitViewModel.editingHabit.value?.let {
                habitViewModel.updateEditingHabit(it.copy(name = text.toString()))
            }
        }

        binding.tilHabitNotes.editText?.doOnTextChanged { text, _, _, _ ->
            habitViewModel.editingHabit.value?.let {
                habitViewModel.updateEditingHabit(it.copy(notes = text.toString()))
            }
        }

        binding.buttonDetails.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HabitDetailsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.buttonSave.setOnClickListener {
            habitViewModel.saveEditingHabit()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}