package com.medicine.reminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.data.repository.MedicineAlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineReminderViewModel @Inject constructor(
    private val repository: MedicineAlarmRepository
) : ViewModel() {
    private lateinit var insertMutableLiveData: LiveData<Long>
    private val getMedicineReminderList = MutableStateFlow<List<MedicineReminder>>(listOf())
    private lateinit var updateMutableLiveData: LiveData<Int>
    private lateinit var deleteMutableLiveData: LiveData<Int>

    /**
     * Insert Method
     * @param medicineReminder: Object of MedicineReminder Model
     * @return: Result as integer
     * This Method Insert Data into Database
     */
    fun insert(medicineReminder: MedicineReminder): LiveData<Long> {
        insertMutableLiveData = MutableLiveData()
        viewModelScope.launch(IO) {
            (insertMutableLiveData as MutableLiveData<Long>).postValue(
                repository.insert(
                    medicineReminder
                )
            )
        }
        return insertMutableLiveData
    }


    /**
     * Get List from MedicineReminder Model
     * @return: return list of MedicineReminderList
     */
    fun getMedicineReminderList(): StateFlow<List<MedicineReminder>> {
        viewModelScope.launch(IO) {
            repository.getMedicineReminderList().collect {
                getMedicineReminderList.emit(it)
            }
        }
        return getMedicineReminderList.asStateFlow()
    }

    /**
     * Update Method
     * @param medicineReminder: Object of MedicineReminder Model
     * @return: Result as integer
     * This Method Update Data from Database
     */
    fun update(medicineReminder: MedicineReminder): LiveData<Int> {
        updateMutableLiveData = MutableLiveData()
        viewModelScope.launch(IO) {
            (updateMutableLiveData as MutableLiveData<Int>).postValue(
                repository.update(
                    medicineReminder
                )
            )
        }
        return updateMutableLiveData
    }


    /**
     * Delete MedicineReminder
     */
    fun delete(medicineReminder: MedicineReminder): LiveData<Int> {
        deleteMutableLiveData = MutableLiveData()
        viewModelScope.launch(IO) {
            (deleteMutableLiveData as MutableLiveData).postValue(repository.delete(medicineReminder))
        }
        return deleteMutableLiveData
    }

    /**
     * Delete All
     */
    fun deleteAll() {
        viewModelScope.launch(IO) {
            repository.deleteAll()
        }
    }

}