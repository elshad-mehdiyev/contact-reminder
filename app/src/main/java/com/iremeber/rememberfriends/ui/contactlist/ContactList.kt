package com.iremeber.rememberfriends.ui.contactlist

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.databinding.ContactListBinding
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.language.Language
import com.iremeber.rememberfriends.utils.language.LanguageFactory
import com.iremeber.rememberfriends.utils.util.Constants.REQUEST_CODE_PREFERENCE_KEY
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtil
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtilImpl
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ContactList : Fragment(), DateAndAnimUtil by DateAndAnimUtilImpl() {
    private var _binding: ContactListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactListViewModel by viewModels()
    private val contactListAdapter = ContactListAdapter()
    private lateinit var alarmManagerImpl: AlarmManagerImpl
    private var requestCode = 1000
    private var systemLanguage = "en"
    private lateinit var languageSelector: Language
    private var date = ""
    private var beginHour = ""
    private var interval = ""
    private var message = ""
    private var timeOfAlarm = 0L
    private lateinit var alarmDate: List<String>

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
        languageSelector = LanguageFactory.languageForKey(requireContext(), systemLanguage)
    }

    private fun beginningStateForViews(allContactModel: AllContactModel) {
        binding.reminderForContact.text =
            languageSelector.displayReminderForContactText(allContactModel)
        binding.datePicker.text = getDate()
        binding.beginningTimePicker.text = getHourAndMinute()
        message = languageSelector.displayNotificationText(allContactModel)
    }

    private fun getTextFromViews() {
        date = binding.datePicker.text.toString()
        beginHour = binding.beginningTimePicker.text.toString()
        interval = binding.intervalEditText.text.toString()
        if (interval.isEmpty()) {
            interval = "0"
        }
    }

    private fun getTimeOfAlarm(): Long {
        alarmDate = date.split("/")
        if (alarmDate.isNotEmpty() && beginHour.isNotEmpty()) {
            val alarmHour = beginHour.split(":")[0].toInt()
            val alarmMinute = beginHour.split(":")[1].toInt()
            timeOfAlarm = convertToTimeInMillis(
                alarmMinute, alarmHour,
                alarmDate[0].toInt(), alarmDate[1].toInt() - 1, alarmDate[2].toInt()
            )
        }
        return timeOfAlarm
    }

    private fun saveToScheduleAlarmModel() {
        val scheduleAlarmModel =
            ScheduleAlarmModel(
                timInMillis = getTimeOfAlarm(), requestCode = requestCode,
                message = message, interval = interval.toInt()
            )
        viewModel.saveDataToDb(scheduleAlarmModel = scheduleAlarmModel, source = DataSourceType.SCHEDULE)
    }

    private fun setAlarmForContact() {
        if (System.currentTimeMillis() < getTimeOfAlarm()) {
            alarmManagerImpl.setAlarm(
                timeInMillis = getTimeOfAlarm(), requestCode = requestCode,
                interval = interval.toInt(), message = message
            )
        }
    }

    private fun saveToFavoriteContactModel(allContactModel: AllContactModel) {
        val reminderCardDate =
            languageSelector.displayReminderCardDateText(alarmDate, getDateAndAnim())
        val reminderCardInterval =
            languageSelector.displayReminderCardInterval(interval)
        val favoriteContactModel = FavoriteContactModel(
            id = allContactModel.id,
            name = allContactModel.name, date = date,
            interval = interval, requestCode = requestCode,
            firstLetter = allContactModel.name[0].toString(),
            dateMessage = reminderCardDate, intervalMessage = reminderCardInterval,
            startHour = beginHour
        )
        viewModel.saveDataToDb(contact = favoriteContactModel, source = DataSourceType.FAVORITE)
    }

    private fun showAddReminderLayout() {
        binding.backOfCard.visibility = View.VISIBLE
        binding.recyclerViewContactList.visibility = View.GONE
    }

    private fun hideAddReminderLayout() {
        binding.backOfCard.visibility = View.GONE
        binding.recyclerViewContactList.visibility = View.VISIBLE
    }

    private fun addToFavoriteContacts() {
        contactListAdapter.setOnItemClickListener { allContactModel ->
            saveToDataStore()
            beginningStateForViews(allContactModel)
            showAddReminderLayout()
            binding.createReminderButton.setOnClickListener {
                hideKeyboard()
                getTextFromViews()
                saveToScheduleAlarmModel()
                setAlarmForContact()
                saveToFavoriteContactModel(allContactModel)
                hideAddReminderLayout()
            }
        }
    }

    private fun saveToDataStore() {
        requestCode += 1
        viewModel.saveToDataStore(REQUEST_CODE_PREFERENCE_KEY, requestCode)
    }

    private fun observe() {
        viewModel.contactListData.observe(viewLifecycleOwner) { contacts ->
            contacts?.let { c ->
                contactListAdapter.contactList = c
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
            showDatePickerDialog(requireContext(), binding.datePicker, childFragmentManager)
        }
        binding.beginningTimePicker.setOnClickListener {
            showTimePickerDialog(requireContext(), binding.beginningTimePicker, childFragmentManager)
        }
        binding.cancelReminderButton.setOnClickListener {
            hideKeyboard()
            binding.backOfCard.visibility = View.GONE
            binding.recyclerViewContactList.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}