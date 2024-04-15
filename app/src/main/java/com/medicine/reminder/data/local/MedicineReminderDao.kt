package com.medicine.reminder.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.medicine.reminder.data.model.MedicineReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicineReminder : MedicineReminder) : Long

    @Query("SELECT * FROM tbl_alarm ORDER BY id DESC")
    fun medicineReminderList() : Flow<List<MedicineReminder>>

    @Update
    suspend fun update(medicineReminder: MedicineReminder) : Int

    @Delete
    suspend fun delete(medicineReminder: MedicineReminder) : Int

    @Query("DELETE FROM tbl_alarm")
    suspend fun deleteAll()

}