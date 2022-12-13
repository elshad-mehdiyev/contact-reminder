package com.iremeber.rememberfriends.ui.settingpage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.iremeber.rememberfriends.databinding.SettingPageBinding
import com.iremeber.rememberfriends.ui.viewmodel.ContactListViewModel
import com.iremeber.rememberfriends.utils.util.Constants.MUSIC_ON_PREFERENCE_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingPage : Fragment() {
    private var _binding: SettingPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDataFromDataStore(MUSIC_ON_PREFERENCE_KEY)
        musicOnCheck()
        observe()
    }

    private fun musicOnCheck() {
        binding.musicOn.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    viewModel.saveToDataStore(MUSIC_ON_PREFERENCE_KEY, 1)
                }
                false -> {
                    viewModel.saveToDataStore(MUSIC_ON_PREFERENCE_KEY, 0)
                }
            }
        }
    }
    private fun observe() {
        viewModel.requestCodeFromDataStore.observe(viewLifecycleOwner) { isMusicOn ->
            isMusicOn?.let {
                binding.musicOn.isChecked = isMusicOn == 1
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}