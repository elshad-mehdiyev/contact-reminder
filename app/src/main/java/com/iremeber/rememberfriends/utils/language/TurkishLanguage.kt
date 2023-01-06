package com.iremeber.rememberfriends.utils.language

import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel
import com.iremeber.rememberfriends.utils.util.UtilsWithContext

class TurkishLanguage : Language {
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
        utilsWithContext: UtilsWithContext
    ): String {
        return "${list[0]} ${utilsWithContext.formatMonth(list[1])} " +
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