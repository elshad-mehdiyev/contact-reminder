package com.iremeber.rememberfriends.utils.util.date_and_animation

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager

interface DateAndAnimUtil {
    fun getDateAndAnim(): DateAndAnimUtilImpl
    fun getHourAndMinute(): String
    fun getDate(): String
    fun convertToTimeInMillis(minute: Int, hour: Int, day: Int, month: Int, year: Int): Long
    fun showDatePickerDialog(context: Context, textView: TextView, manager: FragmentManager)
    fun showTimePickerDialog(context: Context, textView: TextView, manager: FragmentManager)
    fun formatMonth(context: Context, month: String?): String
    fun flipCard(context: Context, visibleView: View, inVisibleView: View)
}