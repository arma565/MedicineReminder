package com.medicine.reminder.di

import android.content.Context
import androidx.room.Room
import com.medicine.reminder.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context) =
        synchronized(AppDatabase::class) {
            Room
                .databaseBuilder(context, AppDatabase::class.java, "MedicineReminder.db")
                .fallbackToDestructiveMigration()
                .build()
        }


    @Singleton
    @Provides
    fun provideDao(db: AppDatabase) = db.medicineReminderDao()


}