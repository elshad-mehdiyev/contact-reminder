package com.iremeber.rememberfriends.ui.contactlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.databinding.ContactListBinding
import com.iremeber.rememberfriends.ui.viewmodel.ContactListViewModel
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.language.Language
import com.iremeber.rememberfriends.utils.language.LanguageFactory
import com.iremeber.rememberfriends.utils.util.Constants.REQUEST_CODE_PREFERENCE_KEY
import com.iremeber.rememberfriends.utils.util.UtilsWithContext
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ContactList : Fragment() {
    private var _binding: ContactListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactListViewModel by viewModels()
    private val contactListAdapter = ContactListAdapter()
    private var list = listOf<AllContactModel>()
    private lateinit var alarmManagerImpl: AlarmManagerImpl
    private lateinit var utils: UtilsWithContext
    private var requestCode = 0
    private var systemLanguage = "en"
    private lateinit var languageSelector: Language
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContactListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObjects()
        viewModel.getContactFromDevice()
        viewModel.getDataFromDataStore(REQUEST_CODE_PREFERENCE_KEY)
        buttonClicker()
        addToFavoriteContacts()
        observe()
    }

    private fun initObjects() {
        binding.recyclerViewContactList.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewContactList.adapter = contactListAdapter
        systemLanguage = Locale.getDefault().language
        alarmManagerImpl = AlarmManagerImpl(requireContext())
        utils = UtilsWithContext(requireContext())
        languageSelector = LanguageFactory.languageForKey(systemLanguage)
    }

    private fun addToFavoriteContacts() {
        contactListAdapter.setOnItemClickListener { allContactModel ->
            saveToDataStore()
            binding.backOfCard.visibility = View.VISIBLE
            binding.recyclerViewContactList.visibility = View.GONE
            binding.reminderForContact.text = languageSelector.displayReminderForContactText(allContactModel)
            binding.datePicker.text = utils.getDate()
            binding.beginningTimePicker.text = utils.getHourAndMinute()
            binding.endTimePicker.text = utils.getHourAndMinute()
            binding.createReminderButton.setOnClickListener {
                val date = binding.datePicker.text.toString()
                val beginHour = binding.beginningTimePicker.text.toString()
                val endHour = binding.endTimePicker.text.toString()
                var interval = binding.intervalEditText.text.toString()
                val alarmDate = date.split("/")
                if (alarmDate.isNotEmpty() && beginHour.isNotEmpty() && endHour.isNotEmpty()) {
                    val alarmHour =
                        (beginHour.split(":")[0].toInt() + endHour.split(":")[0].toInt()) / 2
                    val alarmMinute =
                        (beginHour.split(":")[1].toInt() + endHour.split(":")[1].toInt()) / 2
                    val timeOfAlarm = utils.convertToTimeInMillis(alarmMinute, alarmHour,
                        alarmDate[0].toInt(), alarmDate[1].toInt() - 1, alarmDate[2].toInt())
                    val message = languageSelector.displayNotificationText(allContactModel)
                    if (interval.isEmpty()) {
                        interval = "0"
                    }
                    val scheduleAlarmModel =
                        ScheduleAlarmModel(
                            timInMillis = timeOfAlarm, requestCode = requestCode,
                            message = message, interval = interval.toInt()
                        )
                    viewModel.saveToScheduleAlarmModel(scheduleAlarmModel)
                    if (System.currentTimeMillis() < timeOfAlarm) {
                        alarmManagerImpl.setAlarm(
                            timeInMillis = timeOfAlarm, requestCode = requestCode,
                            interval = interval.toInt(), message = message
                        )
                    }
                    val reminderCardDate  = languageSelector.displayReminderCardDateText(alarmDate,utils)
                    val reminderCardInterval = languageSelector.displayReminderCardInterval(interval)
                    val favoriteContactModel = FavoriteContactModel(
                        id = allContactModel.id,
                        name = allContactModel.name, date = date,
                        interval = interval, requestCode = requestCode,
                        firstLetter = allContactModel.name[0].toString(),
                        dateMessage = reminderCardDate, intervalMessage = reminderCardInterval,
                        startHour = beginHour, endHour = endHour
                    )
                    viewModel.saveToFavoriteContactList(favoriteContactModel)
                    binding.backOfCard.visibility = View.GONE

                    binding.recyclerViewContactList.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun saveToDataStore() {
        requestCode += 1
        viewModel.saveToDataStore(REQUEST_CODE_PREFERENCE_KEY, requestCode)
    }

    private fun observe() {
        viewModel.allContactList.observe(viewLifecycleOwner) { contactsFromDb ->
            contactsFromDb?.let { it ->
                list = it
                contactListAdapter.contactList = it
                viewModel.contactListData.observe(viewLifecycleOwner) { contacts ->
                    contacts?.let { c ->
                        if (list.isEmpty()) {
                            viewModel.saveToAllContactList(c)
                        }
                    }
                }
            }
        }
        viewModel.requestCodeFromDataStore.observe(viewLifecycleOwner) { request ->
            request?.let {
                requestCode = request
            }
        }
    }

    private fun buttonClicker() {
        binding.datePicker.setOnClickListener {
            utils.showDatePickerDialog(binding.datePicker, childFragmentManager)
        }
        binding.beginningTimePicker.setOnClickListener {
            utils.showTimePickerDialog(binding.beginningTimePicker, childFragmentManager)
        }
        binding.endTimePicker.setOnClickListener {
            utils.showTimePickerDialog(binding.endTimePicker, childFragmentManager)
        }
        binding.cancelReminderButton.setOnClickListener {
            binding.backOfCard.visibility = View.GONE
            binding.recyclerViewContactList.visibility = View.VISIBLE
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}