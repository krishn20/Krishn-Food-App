package com.internshala.krishnfoodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.model.Menu

class RecyclerCartAdapter(val context: Context, private val finalFoodList: ArrayList<Menu>) :
    RecyclerView.Adapter<RecyclerCartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return finalFoodList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = finalFoodList[position]

        holder.foodItemNameCart.text = item.menu_item_name
        val price = "Rs. " + item.cost_of_item
        holder.foodItemPriceCart.text = price

    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodItemPriceCart: TextView = view.findViewById(R.id.txtFoodItemPriceCart)
        val foodItemNameCart: TextView = view.findViewById(R.id.txtFoodItemNameCart)
    }

}