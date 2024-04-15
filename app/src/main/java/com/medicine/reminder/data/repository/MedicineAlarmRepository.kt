package com.medicine.reminder.data.repository

import com.medicine.reminder.data.local.MedicineReminderDao
import com.medicine.reminder.data.model.MedicineReminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicineAlarmRepository @Inject constructor(
    private val medicineReminderDao: MedicineReminderDao
) {
    suspend fun insert(medicineReminder: MedicineReminder) =
        medicineReminderDao.insert(medicineReminder)

    fun getMedicineReminderList(): Flow<List<MedicineReminder>> =
        medicineReminderDao.medicineReminderList()

    suspend fun update(medicineReminder: MedicineReminder) =
        medicineReminderDao.update(medicineReminder)

    suspend fun delete(medicineReminder: MedicineReminder) =
        medicineReminderDao.delete(medicineReminder)

    suspend fun deleteAll() =
        medicineReminderDao.deleteAll()

}