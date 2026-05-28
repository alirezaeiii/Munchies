package com.umain.test.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RestaurantEntityDao {
    @Query("SELECT * FROM restaurant")
    suspend fun getAll(): List<RestaurantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(properties: List<RestaurantEntity>)
}
