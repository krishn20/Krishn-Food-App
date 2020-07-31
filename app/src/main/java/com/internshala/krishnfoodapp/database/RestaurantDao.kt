package com.internshala.krishnfoodapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRes(resEntity: RestaurantEntity)

    @Delete
    fun deleteRes(resEntity: RestaurantEntity)

    //Making SQL queries with both userId and resId so that different users having different favorites get retrieved respectively.

    @Query("SELECT * FROM restaurants WHERE user_id = :userId")
    fun getAllRestaurants(userId: String): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE user_id = :userId AND res_id = :resId")
    fun getResById(resId: String, userId: String): RestaurantEntity

}