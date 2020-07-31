package com.internshala.krishnfoodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.model.Menu
import com.internshala.krishnfoodapp.model.NameDate

class RecyclerHistoryAdapter(
    val context: Context,
    private val nameDateList: ArrayList<NameDate>,
    private val orderListTotal: ArrayList<ArrayList<Menu>>
) : RecyclerView.Adapter<RecyclerHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerHistoryAdapter.HistoryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nameDateList.size
    }

    override fun onBindViewHolder(holder: RecyclerHistoryAdapter.HistoryViewHolder, position: Int) {
        holder.resNameHistory.text = nameDateList[position].res_name
        holder.orderDateHistory.text = nameDateList[position].order_date

        val layoutManagerHistoryInside = LinearLayoutManager(context)
        val adapterHistoryInside = RecyclerHistoryInsideAdapter(context, orderListTotal[position])
        holder.recyclerHistoryInside.adapter = adapterHistoryInside
        holder.recyclerHistoryInside.layoutManager = layoutManagerHistoryInside

    }

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resNameHistory: TextView = view.findViewById(R.id.txtRestaurantNameInCard)
        val orderDateHistory: TextView = view.findViewById(R.id.txtOrderDateInCard)
        val recyclerHistoryInside: RecyclerView =
            view.findViewById(R.id.recyclerHistoryFragmentInside)
    }
}