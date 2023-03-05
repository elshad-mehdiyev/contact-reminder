package com.iremeber.rememberfriends.utils.language

import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtilImpl

interface Language {
    fun displayReminderForContactText(allContactModel: AllContactModel): String
    fun displayNotificationText(allContactModel: AllContactModel): String
    fun displayReminderCardDateText(list: List<String>, dateAndAnimUtilImpl: DateAndAnimUtilImpl): String
    fun displayReminderCardInterval(interval: String): String
}