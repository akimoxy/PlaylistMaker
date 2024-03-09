package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(
            Intent.EXTRA_TEXT,
            link,
        )
        intent.type = "text/plain"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    override fun openLink(link: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    override fun openEmail(mail: EmailData) {
        val intent = Intent().apply {
            this.action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, mail.recipientEmailAdress)
            putExtra(Intent.EXTRA_SUBJECT, mail.subjectEmail)
            putExtra(Intent.EXTRA_TEXT, mail.mailText)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}