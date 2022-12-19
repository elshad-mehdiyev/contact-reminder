package com.iremeber.rememberfriends.utils.language

import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.utils.util.UtilsWithContext

interface Language {
    fun displayReminderForContactText(allContactModel: AllContactModel): String
    fun displayNotificationText(allContactModel: AllContactModel): String
    fun displayReminderCardDateText(list: List<String>, utilsWithContext: UtilsWithContext): String
    fun displayReminderCardInterval(interval: String): String
}