package com.android.kaloriku.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DataHarianDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dataHarian: DataHarian)

    @Update
    fun update(dataHarian: DataHarian)

    @Delete
    fun delete(dataHarian: DataHarian)

    @get:Query("SELECT * from data_harian_table ORDER by id ASC")
    val allDataHarian: LiveData<List<DataHarian>>

}