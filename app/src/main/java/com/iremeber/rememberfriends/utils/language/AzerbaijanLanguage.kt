package com.iremeber.rememberfriends.utils.language

import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.utils.util.UtilsWithContext

class AzerbaijanLanguage : Language {
    override fun displayReminderForContactText(
        allContactModel: AllContactModel
    ): String {
        return "${allContactModel.name} üçün xatırladıcı"
    }

    override fun displayNotificationText(allContactModel: AllContactModel): String {
        return "${allContactModel.name} ilə  əlaqə  saxlamaq  vaxtıdır."
    }

    override fun displayReminderCardDateText(
        list: List<String>,
        utilsWithContext: UtilsWithContext
    ): String {
        return "${list[0]} ${utilsWithContext.formatMonth(list[1])} " +
                "${list[2]} tarixi üçün xatırladıcı planlaşdırılıb."
    }

    override fun displayReminderCardInterval(interval: String): String {
        return if (interval == "0") {
            "Yalniz Bir dəfə"
        } else {
            "Təkrarlanma intervalı $interval gün."
        }
    }
}