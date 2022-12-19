package com.iremeber.rememberfriends.utils.language

object LanguageFactory {
    fun languageForKey(key: String): Language {
        return when(key) {
            "az" -> AzerbaijanLanguage()
            "tr" -> TurkishLanguage()
            else -> EnglishLanguage()
        }
    }
}
