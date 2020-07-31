package com.internshala.krishnfoodapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Menu(
    val food_id: String,
    val menu_item_name: String,
    val cost_of_item: String,
    val restaurant_id: String
) : Parcelable