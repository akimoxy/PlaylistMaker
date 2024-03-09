package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.domain.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(mail: EmailData)
}