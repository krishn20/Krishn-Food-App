package com.internshala.krishnfoodapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.model.Menu


class RecyclerMenuAdapter(
    val context: Context,
    private val menuItemsList: ArrayList<Menu>,
    private val listOfAlreadySelectedItems: ArrayList<Menu>,
    private val dataTransferInterface: DataTransferInterface
) : RecyclerView.Adapter<RecyclerMenuAdapter.RestaurantMenuViewHolder>() {

    private var foodListToSend: ArrayList<Menu> = listOfAlreadySelectedItems

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerMenuAdapter.RestaurantMenuViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_restaurant_menu_single_row, parent, false)
        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuItemsList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {

        val menuItem = menuItemsList[position]

//        Earlier code for UI updation using LBM

//        if (foodListToSend == null) {
//            holder.foodItemNumber.text = (position + 1).toString()
//            holder.foodItemName.text = menuItem.menu_item_name
//            holder.foodItemPrice.text = menuItem.cost_of_item
//
//            holder.addButton.setOnClickListener {
//                if (holder.addButton.isChecked) {
//                    foodListToSend?.remove(menuItem) ?: Toast.makeText(
//                        context,
//                        "Error Occurred",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    foodListToSend?.add(menuItem)
//                }
//            }
//
//            foodListToSend?.let {
//                val intent = Intent("custom-message")
//                intent.putExtra("final_food_list", it)
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
//                    println(1)
//            }
//
//        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        holder.foodItemNumber.text = (position + 1).toString()
        holder.foodItemName.text = menuItem.menu_item_name
        holder.foodItemPrice.text = menuItem.cost_of_item

        foodListToSend.let {
            if (it.isEmpty()) {
                dataTransferInterface.onProceedToCartBtnStateChanged(false, it)
            } else {
                dataTransferInterface.onProceedToCartBtnStateChanged(true, it)
            }
        }

        if (foodListToSend.contains(menuItem)) {
            holder.addButton.isChecked = true
        }

        holder.addButton.setOnClickListener {


            if (holder.addButton.isChecked) {
                foodListToSend.add(menuItem)
                foodListToSend.let {
                    if (it.isEmpty()) {
                        dataTransferInterface.onProceedToCartBtnStateChanged(false, it)
                    } else {
                        dataTransferInterface.onProceedToCartBtnStateChanged(true, it)
                    }
                }
                println(11)
                println(foodListToSend)
            } else {
                foodListToSend.remove(menuItem)
                foodListToSend.let {
                    if (it.isEmpty()) {
                        dataTransferInterface.onProceedToCartBtnStateChanged(false, it)
                    } else {
                        dataTransferInterface.onProceedToCartBtnStateChanged(true, it)
                    }
                }
                println(10)
                println(foodListToSend)
            }

        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

//          Earlier code using LBM method for UI updation

//            foodListToSend.let {
//                val intent = Intent("custom-message")
//                intent.putExtra("final_food_list", it)
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
//                println(2)
//                println(it)
//            }


    }


    class RestaurantMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodItemNumber: TextView = view.findViewById(R.id.txtMenuItemNumber)
        val foodItemName: TextView = view.findViewById(R.id.txtRestaurantMenuItemName)
        val foodItemPrice: TextView = view.findViewById(R.id.txtRestaurantMenuItemPrice)
        val addButton: ToggleButton = view.findViewById(R.id.btnAddItem)
    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Interface method for UI updation using DB values.

    interface DataTransferInterface {
        fun onProceedToCartBtnStateChanged(newState: Boolean, foodListToCart: ArrayList<Menu>)
    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

}
