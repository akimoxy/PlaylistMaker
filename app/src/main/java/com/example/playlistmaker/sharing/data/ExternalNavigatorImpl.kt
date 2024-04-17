package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                link,
            )
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.let(context::startActivity)
    }

    override fun openLink(link: String) {
        Intent().apply {
            Intent(Intent.ACTION_VIEW)
            data = Uri.parse(link)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.let(context::startActivity)
    }

    override fun openEmail(mail: EmailData) {
        val intent = Intent().apply {
            this.action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            val mailTo = arrayOf(mail.recipientEmailAdress)
            putExtra(Intent.EXTRA_EMAIL, mailTo)
            putExtra(Intent.EXTRA_SUBJECT, mail.subjectEmail)
            putExtra(Intent.EXTRA_TEXT, mail.mailText)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}