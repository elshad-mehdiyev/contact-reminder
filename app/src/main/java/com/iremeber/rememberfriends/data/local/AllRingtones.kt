package com.iremeber.rememberfriends.data.local

import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import com.iremeber.rememberfriends.data.models.AllRingtonesModel
import javax.inject.Inject


class AllRingtones @Inject constructor(private val context: Context){
    fun getRingtonesList(): ArrayList<AllRingtonesModel> {
        val manager = RingtoneManager(context)
        manager.setType(RingtoneManager.TYPE_NOTIFICATION)
        val cursor: Cursor = manager.cursor
        val list: ArrayList<AllRingtonesModel> = arrayListOf()
        while (cursor.moveToNext()) {
            val title: String = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri: String = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
            val model = AllRingtonesModel(title = title, uri = uri)
            list.add(model)
        }
        cursor.close()
        return list
    }
}