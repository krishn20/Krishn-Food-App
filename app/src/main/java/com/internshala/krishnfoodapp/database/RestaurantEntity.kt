package com.internshala.krishnfoodapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(

    //Saving userId and resId both so that different users having different favorites get stored and retrieved respectively.

    @PrimaryKey val res_id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "res_name") val resName: String,
    @ColumnInfo(name = "res_rating") val resRating: String,
    @ColumnInfo(name = "res_cost_for_one") val resCostForOne: String,
    @ColumnInfo(name = "res_image") val resImage: String
)