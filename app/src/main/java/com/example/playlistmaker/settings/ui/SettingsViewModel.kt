package com.example.playlistmaker.settings.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    app: Application
) : AndroidViewModel(app) {

    private var switchAppThemeLiveData = MutableLiveData<Boolean>()
    private val context = getApplication<Application>().applicationContext
    //контекст здесь,чтобы передать состояние темы приложению при первом включении, когда еще нет префов. Надеюсь утечек не будет))
    init {
        Log.d("TEST", "init!")
        switchAppThemeLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun getLoadingLiveData(): LiveData<Boolean> = switchAppThemeLiveData

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {  // 1
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    return SettingsViewModel(
                        sharingInteractor = Creator.getSharingInterator(application),
                        settingsInteractor = (application as App).provideSettingsInteractor(),
                        application
                    ) as T
                }
            }
    }

    fun firstInitTheme(): Boolean {
        return settingsInteractor.switchBtnState(context)
    }
    fun onSwitchClick(isChecked: Boolean): Boolean {
        return settingsInteractor.switchTheme(isChecked)

    }
    fun shareApp() {
        sharingInteractor.shareApp()
    }
    fun termsOfUse() {
        sharingInteractor.openTerms()
    }
    fun sendToSupport() {
        sharingInteractor.openSupport()
    }
}