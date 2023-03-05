package com.iremeber.rememberfriends.utils.language

import android.content.Context
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtilImpl

class EnglishLanguage(val context: Context) : Language {
    override fun displayReminderForContactText(
        allContactModel: AllContactModel
    ): String {
        return "Reminder for ${allContactModel.name}"
    }

    override fun displayNotificationText(
        allContactModel: AllContactModel
    ): String {
        return "It's time to talk with ${allContactModel.name}"
    }

    override fun displayReminderCardDateText(
        list: List<String>,
        dateAndAnimUtilImpl: DateAndAnimUtilImpl
    ): String {
        return "A reminder is scheduled for the ${list[0]} " +
                "${dateAndAnimUtilImpl.formatMonth(context, list[1])} " + list[2]
    }

    override fun displayReminderCardInterval(interval: String): String {
        return if (interval == "0") {
            "Only once"
        } else {
            "Repetition interval $interval days."
        }
    }
}