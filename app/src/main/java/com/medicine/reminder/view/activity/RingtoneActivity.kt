package com.medicine.reminder.view.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import com.medicine.reminder.R
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.data.services.MedicineService
import com.medicine.reminder.databinding.ActivityRingtoneBinding
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RingtoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRingtoneBinding
    private lateinit var medicineReminder: MedicineReminder
    private lateinit var uri: Uri
    private lateinit var notificationManager: NotificationManager
    private var ringtone: Ringtone? = null
    private var channelId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alertDialog = AlertDialog.Builder(this@RingtoneActivity).create()
        binding = DataBindingUtil.bind(this@RingtoneActivity.layoutInflater.inflate(R.layout.activity_ringtone,null))!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            uri = intent.extras?.getParcelable("ringtoneKey", Uri::class.java)!!
            medicineReminder =
                intent.extras?.getParcelable("medicineReminder", MedicineReminder::class.java)!!
        }
        uri = intent.extras?.getParcelable("ringtoneKey")!!
        medicineReminder = intent.extras?.getParcelable("medicineReminder")!!
        channelId = "${medicineReminder.id}"
        binding.medicineReminder = medicineReminder

        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(this@RingtoneActivity, uri)
            ringtone?.play()
        }




        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                medicineReminder.title,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationCompat = NotificationCompat.Builder(this@RingtoneActivity, channelId)
            .setContentTitle(medicineReminder.title)
            .setContentText(getString(R.string.it_s_time_to_use_your_medicine))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Medicine Name: " + medicineReminder.title + "\n" +
                            "Hour: " + medicineReminder.hour + "\n" +
                            "Min: " + medicineReminder.min
                )
            )
            .setSmallIcon(R.drawable.medicine)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()
        notificationManager.notify(medicineReminder.id, notificationCompat)


        binding.btnDismiss.setOnClickListener {
            if (ringtone != null) {
                val intentStopService = Intent(this@RingtoneActivity, MedicineService::class.java)
                val pendingIntentStop = PendingIntent.getBroadcast(
                    this@RingtoneActivity,
                    medicineReminder.id,
                    intentStopService,
                    PendingIntent.FLAG_IMMUTABLE
                )
                (this@RingtoneActivity.getSystemService(ALARM_SERVICE) as AlarmManager).cancel(
                    pendingIntentStop
                )
                ringtone?.stop()
                ringtone = null
                notificationManager.cancel(
                    medicineReminder.id
                )

                alertDialog.dismiss()
                this@RingtoneActivity.finishAndRemoveTask()
            }
        }

        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setView(binding.root)
        alertDialog.show()
    }

    override fun onStop() {
        if (ringtone != null){
            ringtone?.stop()
            ringtone = null
        }
        super.onStop()
    }
}