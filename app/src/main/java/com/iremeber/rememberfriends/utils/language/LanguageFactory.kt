package com.iremeber.rememberfriends.utils.language

import android.content.Context

object LanguageFactory {
    fun languageForKey(context: Context, key: String): Language {
        return when (key) {
            "az" -> AzerbaijanLanguage(context)
            "tr" -> TurkishLanguage(context)
            else -> EnglishLanguage(context)
        }
    }
}
