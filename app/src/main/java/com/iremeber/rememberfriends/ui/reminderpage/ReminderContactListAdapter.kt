package com.iremeber.rememberfriends.ui.reminderpage

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.databinding.ReminderContactListRecycleItemBinding
import com.iremeber.rememberfriends.utils.util.CommonUtil

class ReminderContactListAdapter: RecyclerView.Adapter<ReminderContactListAdapter.ReminderHolder>() {
    inner class ReminderHolder(val binding: ReminderContactListRecycleItemBinding): RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object: DiffUtil.ItemCallback<FavoriteContactModel>() {
        override fun areItemsTheSame(
            oldItem: FavoriteContactModel,
            newItem: FavoriteContactModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FavoriteContactModel,
            newItem: FavoriteContactModel
        ): Boolean {
            return oldItem.dbId == newItem.dbId
        }
    }
    private val list = AsyncListDiffer(this, diffUtil)
    var reminderAdapterList: List<FavoriteContactModel>
    get() = list.currentList
    set(value) = list.submitList(value)

    // editorIconClickListener
    private var onEditorIconClickListener: ((FavoriteContactModel, viewFront: View, viewBack: View) -> Unit)? = null

    fun setEditorIconClickListener(listener: (FavoriteContactModel, viewFront: View, viewBack: View) -> Unit) {
        onEditorIconClickListener = listener
    }
    private var onTextClickListener: ((view: TextView) -> Unit)? = null

    fun setTextClickListener(listener: (view: TextView) -> Unit) {
        onTextClickListener = listener
    }
    private var onEditTextClickListener: ((view: EditText) -> Unit)? = null

    fun setEditTextClickListener(listener : (view: EditText) -> Unit) {
        onEditTextClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderHolder {
        val binding = ReminderContactListRecycleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent,false)
        return ReminderHolder((binding))
    }

    override fun onBindViewHolder(holder: ReminderHolder, position: Int) {
        val contactList = reminderAdapterList[position]
        holder.binding.reminderList = reminderAdapterList[position]
        holder.binding.letterBackDecor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(CommonUtil.getColors()))
        buttonClicker(holder, contactList)
    }

    override fun getItemCount(): Int {
        return reminderAdapterList.size
    }

    private fun buttonClicker(holder: ReminderHolder, model: FavoriteContactModel) {
        holder.binding.editorIcon.setOnClickListener {
            onEditorIconClickListener?.let {
                it(model, holder.binding.backOfCard, holder.binding.frontOfCard)
            }
        }
        holder.binding.saveIconBack.setOnClickListener {
            onEditorIconClickListener?.let {
                it(model, holder.binding.frontOfCard, holder.binding.backOfCard)
            }
        }
        holder.binding.editorDateCardBack.setOnClickListener {
            onTextClickListener?.let {
                it(holder.binding.editorDateCardBack)
            }
        }
        holder.binding.editorIntervalCardBack.setOnClickListener {
            onEditTextClickListener?.let {
                it(holder.binding.editorIntervalCardBack)
            }
        }
        holder.binding.editorHourStartCardBack.setOnClickListener {
            onTextClickListener?.let {
                it(holder.binding.editorHourStartCardBack)
            }
        }
        holder.binding.editorHourEndCardBack.setOnClickListener {
            onTextClickListener?.let {
                it(holder.binding.editorHourEndCardBack)
            }
        }
    }
}