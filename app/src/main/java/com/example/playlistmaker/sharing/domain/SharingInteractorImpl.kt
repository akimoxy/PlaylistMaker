package com.example.practicum.playlist.domain.sharing.impl

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.SharingInteractor


class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator, private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return getString(context, R.string.link_to_android_course)
    }

    private fun getSupportEmailData(): EmailData {
        val recipientEmailAdress = getString(context, R.string.recipient_email)
        val subjectEmail = getString(context, R.string.subject_email)
        val mailText = getString(context, R.string.mail_text)
        return EmailData(mailText, recipientEmailAdress, subjectEmail)
    }

    private fun getTermsLink(): String {
        return getString(context, R.string.url_term_of_use)
    }
}