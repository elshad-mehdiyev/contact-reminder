package com.iremeber.rememberfriends.utils.util.date_and_animation

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iremeber.rememberfriends.R
import java.text.SimpleDateFormat
import java.util.*


class DateAndAnimUtilImpl : DateAndAnimUtil {
    override fun getDateAndAnim() = this
    override fun getHourAndMinute(): String {
        val utc = Calendar.getInstance()
        val hour = utc.get(Calendar.HOUR_OF_DAY)
        val minute = utc.get(Calendar.MINUTE)
        val formatHour = if (hour < 10) "0$hour" else hour.toString()
        val formatMinute = if (minute < 10) "0$minute" else minute.toString()
        return "$formatHour:$formatMinute"
    }

    override fun getDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    override fun convertToTimeInMillis(minute: Int, hour: Int, day: Int, month: Int, year: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return calendar.timeInMillis
    }

    override fun showDatePickerDialog(context: Context, textView: TextView, manager: FragmentManager) {
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText(context.getString(R.string.select_date_text))
        val materialDatePicker = materialDateBuilder.build()
        materialDatePicker.show(manager, context.getString(R.string.select_date_text))
        materialDatePicker.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it as Long
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatted = format.format(utc.time)
            textView.text = formatted
        }
    }

    override fun showTimePickerDialog(context: Context, textView: TextView, manager: FragmentManager) {

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(context.getString(R.string.select_time_text))
                .build()
        picker.show(manager, context.getString(R.string.select_time_text))
        picker.addOnPositiveButtonClickListener {
            textView.text = buildString {
                append(if (picker.hour < 10) "0" + picker.hour else picker.hour)
                append(":")
                append(if (picker.minute < 10) "0" + picker.minute else picker.minute)
            }
        }
    }

    override fun formatMonth(context: Context, month: String?): String {
        return when (month) {
            "01" -> context.getString(R.string.january)
            "02" -> context.getString(R.string.february)
            "03" -> context.getString(R.string.mart)
            "04" -> context.getString(R.string.april)
            "05" -> context.getString(R.string.may)
            "06" -> context.getString(R.string.june)
            "07" -> context.getString(R.string.july)
            "08" -> context.getString(R.string.august)
            "09" -> context.getString(R.string.september)
            "10" -> context.getString(R.string.october)
            "11" -> context.getString(R.string.november)
            "12" -> context.getString(R.string.december)
            else -> context.getString(R.string.unknown_error)
        }
    }

    override fun flipCard(context: Context, visibleView: View, inVisibleView: View) {
        try {
            visibleView.visibility = View.VISIBLE
            val scale = context.resources.displayMetrics.density
            val cameraDist = 8000 * scale
            visibleView.cameraDistance = cameraDist
            inVisibleView.cameraDistance = cameraDist

            val flipOutAnimatorSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_out
                ) as AnimatorSet
            flipOutAnimatorSet.setTarget(inVisibleView)

            val flipInAnimationSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_in
                ) as AnimatorSet
            flipInAnimationSet.setTarget(visibleView)
            flipOutAnimatorSet.start()
            flipInAnimationSet.start()
            flipInAnimationSet.doOnEnd {
                inVisibleView.visibility = View.GONE
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}