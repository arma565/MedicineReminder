package com.medicine.reminder

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.medicine.reminder.data.local.AppDatabase
import com.medicine.reminder.data.local.MedicineReminderDao
import com.medicine.reminder.data.model.MedicineReminder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MedicineReminderDataBaseTest {
    companion object {
        private lateinit var context: Context
        private lateinit var database: AppDatabase
        private lateinit var dao: MedicineReminderDao

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            context = InstrumentationRegistry.getInstrumentation().context
            database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
            dao = database.medicineReminderDao()
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            database.close()
        }
    }

    @Before
    fun setup() {
        database.clearAllTables()
    }

    /**
     * Create MedicineReminder test
     * Parameter: MedicineReminder Value: Valid
     * Result: Successfully create MedicineReminder in db
     */
    @Test
    fun testCreateMedicineReminder_MedicineReminder_MedicineReminderCreateSuccessfully() {
        runBlocking {
            val completableDeferred = CompletableDeferred<Boolean>()
            CoroutineScope(IO).launch {
                try {
                    //Arrange
                    val medicineReminder = MedicineReminder(
                        "nazonex", 20, 15
                    )
                    //Act
                    dao.insert(medicineReminder)
                    dao.medicineReminderList().collect {
                        completableDeferred.complete(it.isNotEmpty())
                    }
                } catch (e: Exception) {
                    Assert.fail(e.message)
                }
            }
            //Assert
            Assert.assertTrue(completableDeferred.await())
        }
    }

    /**
     * Update MedicineReminder test
     * Parameter: MedicineReminder Value: Valid
     * Result: Successfully update medicineReminder in db
     */
    @Test
    fun testUpdateMedicineReminder_MedicineReminder_MedicineReminderUpdateSuccessfully() {
        runBlocking {
            val completableDeferred = CompletableDeferred<Boolean>()
            CoroutineScope(IO).launch {
                try {
                    //Arrange
                    val medicineReminder = MedicineReminder(
                        120, "nazonex", 20, 15
                    )
                    dao.insert(medicineReminder)
                    //Act
                    dao.update(
                        MedicineReminder(
                            120,
                            "jelophen",
                            19,
                            40
                        )
                    )
                    dao.medicineReminderList().collect {
                        completableDeferred.complete(it.first().title == "jelophen")
                    }
                } catch (e: Exception) {
                    Assert.fail(e.message)
                }

            }
            //Assert
            Assert.assertTrue(completableDeferred.await())
        }

    }

    /**
     * Get MedicineReminder list test
     * Parameter: No parameter Value: No parameter
     * Result: Successfully get MedicineReminder list from db
     */
    @Test
    fun testGetMedicineReminderList_NoParameter_GetMedicineReminderListSuccessfully() {
        runBlocking {
            val completableDeferred = CompletableDeferred<Boolean>()
            CoroutineScope(IO).launch {
                try {
                    //Arrange
                    val medicineReminder = MedicineReminder(
                        "nazonex", 20, 15
                    )
                    dao.insert(medicineReminder)
                    //Act
                    dao.medicineReminderList().collect {
                        completableDeferred.complete(it.isNotEmpty())
                    }

                } catch (e: Exception) {
                    Assert.fail(e.message)
                }
            }
            //Assert
            Assert.assertTrue(completableDeferred.await())
        }

    }

    /**
     * Delete MedicineReminder test
     * Parameter: MedicineReminder Value: Valid MedicineReminder id
     * Result: Successfully delete MedicineReminder
     */
    @Test
    fun testDeleteMedicineReminder_ValidMedicineReminder_SuccessfullyDeleteMedicineReminder() {
        runBlocking {
            val completableDeferred = CompletableDeferred<Boolean>()
            CoroutineScope(IO).launch {
                try {
                    //Arrange
                    val medicineReminder = MedicineReminder(
                        110, "nazonex", 20, 15
                    )
                    dao.insert(medicineReminder)
                    //Act
                    dao.delete(medicineReminder)
                    dao.medicineReminderList().collect {
                        completableDeferred.complete(it.isEmpty())
                    }
                } catch (e: Exception) {
                    Assert.fail(e.message)
                }
            }
            //Assert
            Assert.assertTrue(completableDeferred.await())
        }
    }
}