package com.medicine.reminder.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tbl_alarm")
data class MedicineReminder(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String? = "",
    var hour: Int = 0,
    var min: Int = 0
) : Parcelable {

    constructor(title: String, hour: Int, min: Int) : this() {
        this.title = title
        this.hour = hour
        this.min = min
    }
}