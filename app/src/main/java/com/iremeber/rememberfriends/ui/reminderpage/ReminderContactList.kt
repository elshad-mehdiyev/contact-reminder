package com.iremeber.rememberfriends.ui.reminderpage


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.enums.UpdateSourceType
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateScheduleAlarmModel
import com.iremeber.rememberfriends.databinding.ReminderContactListBinding
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.language.Language
import com.iremeber.rememberfriends.utils.language.LanguageFactory
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtilImpl
import com.iremeber.rememberfriends.di.HiltAndroidApp
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReminderContactList : Fragment(), DateAndAnimUtil by DateAndAnimUtilImpl() {
    private var _binding: ReminderContactListBinding? = null
    private val binding get() = _binding!!
    private val recyclerAdapter = ReminderContactListAdapter()
    private val viewModel: ReminderContactViewModel by viewModels()
    private lateinit var alarmManagerImpl: AlarmManagerImpl
    private var systemLanguage = "en"
    private lateinit var languageSelector: Language
    private var updateDate = ""
    private var updateInterval = ""
    private var updateBeginHour = ""
    private lateinit var messageDate: List<String>
    private var updateMessage = ""
    private var updateIntervalMessage = ""
    private var notificationMessage = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = requireActivity().getColor(R.color.background_gradient_end_color)
        _binding = ReminderContactListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObjects()
        observe()
        deleteFavoriteModelItem()
        showDateTimeDialog()
        flipCard()
        showStopAlarm()
    }

    private fun observe() {
        viewModel.favoriteContactList.observe(viewLifecycleOwner) { favoriteModel ->
            favoriteModel?.let {
                recyclerAdapter.reminderAdapterList = it.reversed()
            }
        }
    }

    private fun showStopAlarm() {
        val alarmRingtoneState = (context?.applicationContext as HiltAndroidApp).alarmRingtoneState
        if (alarmRingtoneState.value != null) {
            binding.stopAlarm.visibility = View.VISIBLE
        }
        binding.stopAlarm.setOnClickListener {
            alarmRingtoneState.value?.stop()
            alarmRingtoneState.value = null
            binding.stopAlarm.visibility = View.GONE
        }
    }

    private fun showDateTimeDialog() {
        recyclerAdapter.setTextClickListener { view ->
            when (view.id) {
                R.id.editorDateCardBack -> {
                    showDatePickerDialog(requireContext(), view, childFragmentManager)
                }
                R.id.editorHourStartCardBack -> {
                    showTimePickerDialog(requireContext(), view, childFragmentManager)
                }
            }
        }
    }

    private fun getUpdatedTextFromViews() {
        updateDate =
            requireActivity().findViewById<TextView>(R.id.editorDateCardBack).text.toString()
        updateInterval =
            requireActivity().findViewById<EditText>(R.id.editorIntervalCardBack).text.toString()
        updateBeginHour =
            requireActivity().findViewById<TextView>(R.id.editorHourStartCardBack).text.toString()

        messageDate = updateDate.split("/")
        updateMessage = languageSelector.displayReminderCardDateText(messageDate, getDateAndAnim())
        if (updateInterval.isEmpty()) updateInterval = "0"
        updateIntervalMessage =
            languageSelector.displayReminderCardInterval(updateInterval)
    }

    private fun getTimeOfAlarm(): Long {
        val alarmHour = updateBeginHour.split(":")[0].toInt()
        val alarmMinute = updateBeginHour.split(":")[1].toInt()
        return convertToTimeInMillis(
            alarmMinute, alarmHour,
            messageDate[0].toInt(), messageDate[1].toInt() - 1, messageDate[2].toInt()
        )
    }

    private fun updateFavoriteContactList(model: FavoriteContactModel) {
        val updateReminderCardModel = UpdateReminderCardModel(
            date = updateDate,
            interval = updateInterval,
            beginHour = updateBeginHour,
            requestCode = model.requestCode,
            dateMessage = updateMessage,
            intervalMessage = updateIntervalMessage
        )
        viewModel.updateData(
            reminderCard = updateReminderCardModel,
            updateDataSourceType = UpdateSourceType.REMINDER_CARD
        )
    }

    private fun updateScheduleAlarmModel(model: FavoriteContactModel) {
        val updateScheduleAlarmModel = UpdateScheduleAlarmModel(
            newTimeInMillis = getTimeOfAlarm(),
            requestCode = model.requestCode,
            interval = updateInterval.toInt()
        )
        viewModel.updateData(
            updateScheduleAlarmModel = updateScheduleAlarmModel,
            updateDataSourceType = UpdateSourceType.SCHEDULE
        )
    }

    private fun updateAlarm(model: FavoriteContactModel) {
        if (System.currentTimeMillis() < getTimeOfAlarm()) {
            alarmManagerImpl.cancelAlarm(model.requestCode)
            alarmManagerImpl.setAlarm(
                timeInMillis = getTimeOfAlarm(), requestCode = model.requestCode,
                interval = updateInterval.toInt(), message = notificationMessage
            )
        }
    }

    private fun flipCard() {
        recyclerAdapter.setEditorIconClickListener { model, viewFront, viewBack ->
            when (viewFront.id) {
                R.id.backOfCard -> {
                    flipCard(requireContext(), viewFront, viewBack)
                }
                R.id.frontOfCard -> {
                    createNotificationText(model)
                    getUpdatedTextFromViews()
                    updateFavoriteContactList(model)
                    updateScheduleAlarmModel(model)
                    updateAlarm(model)
                    flipCard(requireContext(), viewFront, viewBack)
                }
            }
        }
    }

    private fun createNotificationText(model: FavoriteContactModel) {
        val allContactModel =
            AllContactModel(id = model.id, name = model.name, firstLetter = model.firstLetter, phoneNumber = "")
        notificationMessage = languageSelector.displayNotificationText(allContactModel)
    }

    private fun getTimeOfMillisForUndoReminderCard(favoriteModel: FavoriteContactModel): Long {
        val messageDate = favoriteModel.date.split("/")
        val alarmHour = favoriteModel.startHour.split(":")[0].toInt()
        val alarmMinute = favoriteModel.startHour.split(":")[1].toInt()
        return convertToTimeInMillis(
            alarmMinute, alarmHour,
            messageDate[0].toInt(), messageDate[1].toInt() - 1, messageDate[2].toInt()
        )
    }

    private fun deleteFavoriteModelItem() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.bindingAdapterPosition

                val favoriteModel = recyclerAdapter.reminderAdapterList[position]

                viewModel.deleteData(favoriteModel.requestCode, source = DataSourceType.FAVORITE)

                viewModel.deleteData(favoriteModel.requestCode, source = DataSourceType.SCHEDULE)

                alarmManagerImpl.cancelAlarm(favoriteModel.requestCode)

                val timeOfAlarm = getTimeOfMillisForUndoReminderCard(favoriteModel)


                val allContactModel = AllContactModel(
                    id = favoriteModel.id,
                    name = favoriteModel.name,
                    firstLetter = favoriteModel.firstLetter,
                    phoneNumber = ""
                )
                val message = languageSelector.displayNotificationText(allContactModel)

                val scheduleAlarmModel = ScheduleAlarmModel(
                    timInMillis = timeOfAlarm,
                    requestCode = favoriteModel.requestCode,
                    interval = favoriteModel.interval.toInt(),
                    message = message
                )
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackbar_delete_text),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(getString(R.string.snackbar_undo)) {
                        viewModel.saveDataToDb(contact = favoriteModel,source = DataSourceType.FAVORITE)
                        viewModel.saveDataToDb(scheduleAlarmModel = scheduleAlarmModel, source = DataSourceType.SCHEDULE)
                        alarmManagerImpl.reScheduleAlarms()
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(binding.recyclerReminderPage)
        }
        observe()
    }

    private fun initObjects() {
        systemLanguage = Locale.getDefault().language
        alarmManagerImpl = AlarmManagerImpl(requireContext())
        binding.recyclerReminderPage.layoutManager = LinearLayoutManager(context)
        binding.recyclerReminderPage.adapter = recyclerAdapter
        languageSelector = LanguageFactory.languageForKey(requireContext(), systemLanguage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}