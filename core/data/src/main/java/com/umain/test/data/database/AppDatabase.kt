package com.umain.test.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RestaurantEntity::class, FilterEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): RestaurantEntityDao
    abstract fun filterDao(): FilterEntityDao
}