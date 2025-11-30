package com.example.dailychecktodo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailychecktodo.databinding.ItemDateBinding

data class DateItem(
    val dayOfWeek: String,
    val dayOfMonth: String,
    var isSelected: Boolean = false
)

class DateAdapter(private val dates: List<DateItem>, private val onDateSelected: (DateItem) -> Unit) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1

    init {
        // find today and set it as selected initially
        selectedPosition = dates.indexOfFirst { it.isSelected }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            if (selectedPosition != holder.adapterPosition) {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onDateSelected(dates[selectedPosition])
            }
        }
    }

    override fun getItemCount() = dates.size

    class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dateItem: DateItem, isSelected: Boolean) {
            binding.dayOfWeekText.text = dateItem.dayOfWeek
            binding.dayOfMonthText.text = dateItem.dayOfMonth
            // The background and text color changes are handled by the selectors
            binding.dayOfMonthText.isSelected = isSelected
        }
    }
}