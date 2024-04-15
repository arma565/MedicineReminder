package com.medicine.reminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.medicine.reminder.data.model.MedicineReminder

@Database(entities = [MedicineReminder::class], version = 5, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineReminderDao(): MedicineReminderDao
}