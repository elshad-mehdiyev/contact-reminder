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
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.databinding.ReminderContactListBinding
import com.iremeber.rememberfriends.ui.viewmodel.ContactListViewModel
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.util.Constants.REQUEST_CODE_PREFERENCE_KEY
import com.iremeber.rememberfriends.utils.util.UtilsWithContext
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReminderContactList : Fragment() {
    private var _binding: ReminderContactListBinding? = null
    private val binding get() = _binding!!
    private val recyclerAdapter = ReminderContactListAdapter()
    private val viewModel: ContactListViewModel by viewModels()
    private lateinit var utils: UtilsWithContext
    private lateinit var alarmManagerImpl: AlarmManagerImpl
    private var requestCode = 0

    private var systemLanguage = "en"

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
        systemLanguage = Locale.getDefault().language
        initObjects()
        flipCard()
        observe()
        deleteFavoriteModelItem()
        showDateTimeDialog()
    }

    private fun observe() {
        viewModel.favoriteContactList.observe(viewLifecycleOwner) { favoriteModel ->
            favoriteModel?.let {
                recyclerAdapter.reminderAdapterList = it.reversed()
            }
        }
        viewModel.requestCodeFromDataStore.observe(viewLifecycleOwner) { request ->
            request?.let {
                requestCode = request
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

    private fun flipCard() {
        recyclerAdapter.setEditorIconClickListener { model, viewFront, viewBack ->
            when (viewFront.id) {
                R.id.backOfCard -> {
                    utils.flipCard(requireContext(), viewFront, viewBack)
                }
                R.id.frontOfCard -> {
                    val updateDate =
                        requireActivity().findViewById<TextView>(R.id.editorDateCardBack).text.toString()
                    var updateInterval =
                        requireActivity().findViewById<EditText>(R.id.editorIntervalCardBack).text.toString()
                    val updateBeginHour =
                        requireActivity().findViewById<TextView>(R.id.editorHourStartCardBack).text.toString()
                    val updateEndHour =
                        requireActivity().findViewById<TextView>(R.id.editorHourEndCardBack).text.toString()
                    requestCode = model.requestCode + 1
                    viewModel.saveToDataStore(REQUEST_CODE_PREFERENCE_KEY, requestCode)
                    val messageDate = updateDate.split("/")
                    val updateMessage = when {
                        systemLanguage.contains("az") -> {
                            "${messageDate[0]} ${utils.formatMonth(messageDate[1])} " +
                                    "${messageDate[2]} tarixi üçün xatırladıcı planlaşdırılıb."
                        }
                        systemLanguage.contains("tr") -> {
                            "${messageDate[0]} ${utils.formatMonth(messageDate[1])} " +
                                    "${messageDate[2]} tarihi için bir hatırlatıcı programlandı."
                        }
                        else -> {
                            "A reminder is scheduled for the ${messageDate[0]} " +
                                    "${utils.formatMonth(messageDate[1])} " + messageDate[2]
                        }
                    }
                    if (updateInterval.isEmpty()) updateInterval = "0"
                    val updateIntervalMessage: String
                    when {
                        systemLanguage.contains("az") -> {
                            updateIntervalMessage = if (updateInterval == "0") {
                                "Yalniz Bir dəfə"
                            } else {
                                "Təkrarlanma intervalı $updateInterval gün."
                            }
                        }
                        systemLanguage.contains("tr") -> {
                            updateIntervalMessage = if (updateInterval == "0") {
                                "Sadece Bir defa"
                            } else {
                                "tekrarlanma aralığı $updateInterval gün."
                            }
                        }
                        else -> {
                            updateIntervalMessage = if (updateInterval == "0") {
                                "Only once"
                            } else {
                                "Repetition interval $updateInterval days."
                            }
                        }
                    }
                    viewModel.updateFavoriteContactList(
                        date = updateDate,
                        interval = updateInterval,
                        beginHour = updateBeginHour,
                        endHour = updateEndHour,
                        requestCode = requestCode,
                        dateMessage = updateMessage,
                        intervalMessage = updateIntervalMessage,
                        updateRequestCode = model.updateRequestCode
                    )
                    val alarmHour =
                        (updateBeginHour.split(":")[0].toInt() + updateEndHour.split(":")[0].toInt()) / 2
                    val alarmMinute =
                        (updateBeginHour.split(":")[1].toInt() + updateEndHour.split(":")[1].toInt()) / 2
                    val timeOfAlarm = utils.convertToTimeInMillis(alarmMinute, alarmHour,
                        messageDate[0].toInt(), messageDate[1].toInt() - 1, messageDate[2].toInt())

                    viewModel.updateScheduleAlarmModel(
                        newTimeInMillis = timeOfAlarm, requestCode = requestCode,
                        interval = updateInterval.toInt(), updateRequestCode = model.updateRequestCode
                    )
                    if (System.currentTimeMillis() < timeOfAlarm) {
                        alarmManagerImpl.cancelAlarm(model.requestCode)
                        alarmManagerImpl.setAlarm(
                            timeInMillis = timeOfAlarm, requestCode = requestCode,
                            interval = updateInterval.toInt(), message = updateMessage
                        )
                    }
                    utils.flipCard(requireContext(), viewFront, viewBack)
                }
            }
        }
    }
    private fun deleteFavoriteModelItem() {
        val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(
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

                val messageDate = favoriteModel.date.split("/")
                val alarmHour =
                    (favoriteModel.startHour.split(":")[0].toInt() + favoriteModel.endHour.split(":")[0].toInt()) / 2
                val alarmMinute =
                    (favoriteModel.startHour.split(":")[1].toInt() + favoriteModel.endHour.split(":")[1].toInt()) / 2
                val timeOfAlarm = utils.convertToTimeInMillis(alarmMinute, alarmHour,
                    messageDate[0].toInt(), messageDate[1].toInt() - 1, messageDate[2].toInt())
                val message = when {
                    systemLanguage.contains("az") -> {
                        "${favoriteModel.name} ilə  əlaqə  saxlamaq  vaxtıdır."
                    }
                    systemLanguage.contains("tr") -> {
                        "${favoriteModel.name} ile konuşma zamanı geldi."
                    }
                    else -> {
                        "It's time to talk with ${favoriteModel.name}"
                    }
                }
                val scheduleAlarmModel = ScheduleAlarmModel(timInMillis = timeOfAlarm,
                    requestCode = favoriteModel.requestCode, interval = favoriteModel.interval.toInt(),
                    message = message, updateRequestCode = favoriteModel.updateRequestCode)
                Snackbar.make(requireView(),getString(R.string.snackbar_delete_text),Snackbar.LENGTH_LONG).apply {
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
        utils = UtilsWithContext(requireContext())
        alarmManagerImpl = AlarmManagerImpl(requireContext())
        binding.recyclerReminderPage.layoutManager = LinearLayoutManager(context)
        binding.recyclerReminderPage.adapter = recyclerAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}