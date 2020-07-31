package com.internshala.krishnfoodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.model.Menu

class RecyclerHistoryInsideAdapter(val context: Context, private val orderList: ArrayList<Menu>) :
    RecyclerView.Adapter<RecyclerHistoryInsideAdapter.HistoryInsideViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerHistoryInsideAdapter.HistoryInsideViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_cart_single_row, parent, false)
        return HistoryInsideViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerHistoryInsideAdapter.HistoryInsideViewHolder,
        position: Int
    ) {
        holder.foodItemName.text = orderList[position].menu_item_name
        holder.foodItemPrice.text = orderList[position].cost_of_item
    }

    class HistoryInsideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodItemName: TextView = view.findViewById(R.id.txtFoodItemNameCart)
        val foodItemPrice: TextView = view.findViewById(R.id.txtFoodItemPriceCart)
    }

}