package com.example.watertrak

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM water_table ORDER BY date DESC")
    fun getAll(): Flow<List<ItemEntity>>

    @Query("SELECT id, date, waterDrank FROM water_table ORDER BY date ASC")
    fun getData(): List<ItemEntity>

    @Insert
    fun insertAll(items: List<ItemEntity>)
    @Insert
    fun insert(item: ItemEntity)

    @Query("SELECT avg(waterDrank) from water_table")
    fun average(): Double

    @Query("SELECT min(waterDrank) from water_table")
    fun min(): Double
    @Query("SELECT max(waterDrank) from water_table")
    fun max(): Double

    @Query("DELETE FROM water_table")
    fun deleteAll()
}