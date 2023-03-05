package com.iremeber.rememberfriends.utils.language

import android.content.Context
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtilImpl

class TurkishLanguage(val context: Context) : Language {
    override fun displayReminderForContactText(
        allContactModel: AllContactModel
    ): String {
        return "${allContactModel.name} için hatırlatıcı"
    }

    override fun displayNotificationText(
        allContactModel: AllContactModel
    ): String {
        return "${allContactModel.name} ile konuşma zamanı geldi."
    }

    override fun displayReminderCardDateText(
        list: List<String>,
        dateAndAnimUtilImpl: DateAndAnimUtilImpl
    ): String {
        return "${list[0]} ${dateAndAnimUtilImpl.formatMonth(context, list[1])} " +
                "${list[2]} tarihi için bir hatırlatıcı programlandı."
    }

    override fun displayReminderCardInterval(interval: String): String {
        return if (interval == "0") {
            "Sadece Bir defa"
        } else {
            "tekrarlanma aralığı $interval gün."
        }
    }
}