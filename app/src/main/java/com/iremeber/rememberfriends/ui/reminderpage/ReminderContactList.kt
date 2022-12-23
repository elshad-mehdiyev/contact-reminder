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
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.databinding.ReminderContactListBinding
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.language.Language
import com.iremeber.rememberfriends.utils.language.LanguageFactory
import com.iremeber.rememberfriends.utils.util.UtilsWithContext
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReminderContactList : Fragment() {
    private var _binding: ReminderContactListBinding? = null
    private val binding get() = _binding!!
    private val recyclerAdapter = ReminderContactListAdapter()
    private val viewModel: ReminderContactViewModel by viewModels()
    private lateinit var utils: UtilsWithContext
    private lateinit var alarmManagerImpl: AlarmManagerImpl
    private var systemLanguage = "en"
    private lateinit var languageSelector: Language
    private var updateDate = ""
    private var updateInterval = ""
    private var updateBeginHour = ""
    private var updateEndHour = ""
    private lateinit var messageDate: List<String>
    private var updateMessage = ""
    private var updateIntervalMessage = ""


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
    }

    private fun observe() {
        viewModel.favoriteContactList.observe(viewLifecycleOwner) { favoriteModel ->
            favoriteModel?.let {
                recyclerAdapter.reminderAdapterList = it.reversed()
            }
        }

    }

    private fun showDateTimeDialog() {
        recyclerAdapter.setTextClickListener { view ->
            when (view.id) {
                R.id.editorDateCardBack -> {
                    utils.showDatePickerDialog(view, childFragmentManager)
                }
                R.id.editorHourStartCardBack -> {
                    utils.showTimePickerDialog(view, childFragmentManager)
                }
                R.id.editorHourEndCardBack -> {
                    utils.showTimePickerDialog(view, childFragmentManager)
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
        updateEndHour =
            requireActivity().findViewById<TextView>(R.id.editorHourEndCardBack).text.toString()
        messageDate = updateDate.split("/")
        updateMessage = languageSelector.displayReminderCardDateText(messageDate, utils)
        if (updateInterval.isEmpty()) updateInterval = "0"
        updateIntervalMessage =
            languageSelector.displayReminderCardInterval(updateInterval)
    }
    private fun getTimeOfAlarm(): Long {
        val alarmHour =
            (updateBeginHour.split(":")[0].toInt() + updateEndHour.split(":")[0].toInt()) / 2
        val alarmMinute =
            (updateBeginHour.split(":")[1].toInt() + updateEndHour.split(":")[1].toInt()) / 2
        return utils.convertToTimeInMillis(
            alarmMinute, alarmHour,
            messageDate[0].toInt(), messageDate[1].toInt() - 1, messageDate[2].toInt()
        )
    }
    private fun updateFavoriteContactList(model: FavoriteContactModel) {
        viewModel.updateFavoriteContactList(
            date = updateDate,
            interval = updateInterval,
            beginHour = updateBeginHour,
            endHour = updateEndHour,
            requestCode = model.requestCode,
            dateMessage = updateMessage,
            intervalMessage = updateIntervalMessage
        )
    }
    private fun updateScheduleAlarmModel(model: FavoriteContactModel) {
        viewModel.updateScheduleAlarmModel(
            newTimeInMillis = getTimeOfAlarm() , requestCode = model.requestCode,
            interval = updateInterval.toInt()
        )
    }
    private fun updateAlarm(model: FavoriteContactModel) {
        if (System.currentTimeMillis() < getTimeOfAlarm()) {
            alarmManagerImpl.cancelAlarm(model.requestCode)
            alarmManagerImpl.setAlarm(
                timeInMillis = getTimeOfAlarm(), requestCode = model.requestCode,
                interval = updateInterval.toInt(), message = updateMessage
            )
        }
    }
    private fun flipCard() {
        recyclerAdapter.setEditorIconClickListener { model, viewFront, viewBack ->
            when (viewFront.id) {
                R.id.backOfCard -> {
                    utils.flipCard(requireContext(), viewFront, viewBack)
                }
                R.id.frontOfCard -> {
                    getUpdatedTextFromViews()
                    updateFavoriteContactList(model)
                    updateScheduleAlarmModel(model)
                    updateAlarm(model)
                    utils.flipCard(requireContext(), viewFront, viewBack)
                }
            }
        }
    }

    private fun getTimeOfMillisForUndoReminderCard(favoriteModel: FavoriteContactModel): Long {
        val messageDate = favoriteModel.date.split("/")
        val alarmHour =
            (favoriteModel.startHour.split(":")[0].toInt() + favoriteModel.endHour.split(":")[0].toInt()) / 2
        val alarmMinute =
            (favoriteModel.startHour.split(":")[1].toInt() + favoriteModel.endHour.split(":")[1].toInt()) / 2
        return utils.convertToTimeInMillis(
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

                viewModel.deleteFromFavoriteContactList(favoriteModel.requestCode)

                viewModel.deleteFromScheduleAlarmModel(favoriteModel.requestCode)

                alarmManagerImpl.cancelAlarm(favoriteModel.requestCode)

                val timeOfAlarm = getTimeOfMillisForUndoReminderCard(favoriteModel)


                val allContactModel = AllContactModel(
                    id = favoriteModel.id,
                    name = favoriteModel.name,
                    firstLetter = favoriteModel.firstLetter
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
                        viewModel.saveToFavoriteContactList(favoriteModel)
                        viewModel.saveToScheduleAlarmModel(scheduleAlarmModel)
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
        utils = UtilsWithContext(requireContext())
        alarmManagerImpl = AlarmManagerImpl(requireContext())
        binding.recyclerReminderPage.layoutManager = LinearLayoutManager(context)
        binding.recyclerReminderPage.adapter = recyclerAdapter
        languageSelector = LanguageFactory.languageForKey(systemLanguage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}