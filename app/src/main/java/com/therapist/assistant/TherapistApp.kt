package com.therapist.assistant

import android.app.Application
import com.therapist.assistant.data.BackupWorker
import com.therapist.assistant.notification.NotificationHelper

class TherapistApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        BackupWorker.schedule(this)
    }
}
