package com.iremeber.rememberfriends.ui.contactlist

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.databinding.ContactListRecycleItemBinding
import com.iremeber.rememberfriends.utils.util.CommonUtil

class ContactListAdapter: RecyclerView.Adapter<ContactListAdapter.ContactHolder>() {
    inner class ContactHolder(val binding: ContactListRecycleItemBinding): RecyclerView.ViewHolder(binding.root)


    private val diffUtil = object: DiffUtil.ItemCallback<AllContactModel>() {
        override fun areItemsTheSame(oldItem: AllContactModel, newItem: AllContactModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AllContactModel, newItem: AllContactModel): Boolean {
            return oldItem.id == newItem.id
        }
    }
    private val list = AsyncListDiffer(this, diffUtil)
    var contactList: List<AllContactModel>
    get() = list.currentList
    set(value) = list.submitList(value)

    private var onItemClickListener: ((AllContactModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (AllContactModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val binding = ContactListRecycleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ContactHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contactList = contactList[position]
        holder.binding.contactList = contactList
        holder.binding.letterBackContactListAdapter.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(
                CommonUtil.getColors()))
        holder.binding.addAlarmIcon.setOnClickListener {
            onItemClickListener?.let {
                it(contactList)
            }
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}