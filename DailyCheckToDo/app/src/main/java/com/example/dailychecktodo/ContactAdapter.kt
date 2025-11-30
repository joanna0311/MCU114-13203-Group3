package com.example.dailychecktodo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailychecktodo.databinding.ItemContactBinding

data class Contact(val name: String)

class ContactAdapter(private val contacts: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size

    class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.contactName.text = contact.name
            // 在這裡可以設定頭像等其他資訊
        }
    }
}