package com.medicine.reminder.data.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.view.activity.RingtoneActivity


@Suppress("DEPRECATION")
class MedicineService : BroadcastReceiver() {
    private lateinit var ringtoneKey: Uri
    private lateinit var medicineReminder: MedicineReminder
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.extras == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ringtoneKey = intent.getParcelableExtra("ringtoneKey", Uri::class.java)!!
            medicineReminder =
                intent.getParcelableExtra("medicineReminder", MedicineReminder::class.java)!!
        }
        ringtoneKey = intent.getParcelableExtra("ringtoneKey")!!
        medicineReminder = intent.getParcelableExtra("medicineReminder")!!

        val intentRingtoneActivity = Intent(context, RingtoneActivity::class.java)
        intentRingtoneActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intentRingtoneActivity.putExtra("ringtoneKey", ringtoneKey)
        intentRingtoneActivity.putExtra("medicineReminder", medicineReminder)
        context.startActivity(intentRingtoneActivity)
    }
}