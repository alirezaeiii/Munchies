package com.umain.test.data.di

import android.content.Context
import androidx.room.Room
import com.umain.test.data.database.AppDatabase
import com.umain.test.data.database.FilterEntityDao
import com.umain.test.data.database.RestaurantEntityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "android-job-assignment")
            .build()

    @Singleton
    @Provides
    fun provideRestaurantEntityDao(appDatabase: AppDatabase): RestaurantEntityDao = appDatabase.propertyDao()

    @Singleton
    @Provides
    fun provideFilterEntityDao(appDatabase: AppDatabase): FilterEntityDao = appDatabase.filterDao()
}