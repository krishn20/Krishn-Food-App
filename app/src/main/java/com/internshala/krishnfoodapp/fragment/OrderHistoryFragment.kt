package com.internshala.krishnfoodapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.adapter.RecyclerHistoryAdapter
import com.internshala.krishnfoodapp.model.Menu
import com.internshala.krishnfoodapp.model.NameDate
import com.internshala.krishnfoodapp.util.ConnectivityManager
import org.json.JSONException

private lateinit var recyclerHistoryFragment: RecyclerView
private lateinit var layoutManagerHistoryFragment: RecyclerView.LayoutManager
private lateinit var adapterHistoryFragment: RecyclerHistoryAdapter
private lateinit var progressLayoutHistory: RelativeLayout
private lateinit var progressBarHistory: ProgressBar
private lateinit var sharedPreferences: SharedPreferences

private var nameDateList = arrayListOf<NameDate>()
private var orderListTotal = arrayListOf(arrayListOf<Menu>())

class OrderHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerHistoryFragment = view.findViewById(R.id.recyclerHistoryFragmentMain)
        progressLayoutHistory = view.findViewById(R.id.progressLayoutHistory)
        progressBarHistory = view.findViewById(R.id.progressBarHistory)
        layoutManagerHistoryFragment = LinearLayoutManager(activity)
        progressLayoutHistory.visibility = View.VISIBLE

        sharedPreferences = this.activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        ) ?: return null
        val userIdHistory = sharedPreferences.getString("user_id", "000")

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userIdHistory"

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,

                Response.Listener {

                    try {
                        progressLayoutHistory.visibility = View.GONE
                        val mainJsonObject = it.getJSONObject("data")
                        val success = mainJsonObject.getBoolean("success")

                        if (success) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val historyListJsonArray = mainJsonObject.getJSONArray("data")
                            if (historyListJsonArray.length() > 0) {

                                nameDateList.clear()
                                orderListTotal.clear()

                                for (i in 0 until historyListJsonArray.length()) {

                                    val historyListJsonObject =
                                        historyListJsonArray.getJSONObject(i)
                                    val nameDateObject = NameDate(
                                        historyListJsonObject.getString("restaurant_name"),
                                        historyListJsonObject.getString("order_placed_at")
                                            .subSequence(0, 8) as String
                                    )

                                    nameDateList.add(nameDateObject)
                                }

                                for (i in 0 until historyListJsonArray.length()) {
                                    val orderList = arrayListOf<Menu>()
                                    val historyListJsonObject =
                                        historyListJsonArray.getJSONObject(i)
                                    val orderListJsonArray =
                                        historyListJsonObject.getJSONArray("food_items")

                                    for (j in 0 until orderListJsonArray.length()) {

                                        val orderListJsonObject =
                                            orderListJsonArray.getJSONObject(j)
                                        val foodItemObject = Menu(
                                            orderListJsonObject.getString("food_item_id"),
                                            orderListJsonObject.getString("name"),
                                            orderListJsonObject.getString("cost"),
                                            ((orderListJsonObject.getString("food_item_id")
                                                .toInt()) / 10).toString()
                                        )
                                        orderList.add(foodItemObject)

                                    }

                                    orderListTotal.add(orderList)
                                }

                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */
                            else {
                                Toast.makeText(
                                    context,
                                    "Empty Order History List",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            adapterHistoryFragment = RecyclerHistoryAdapter(
                                activity as Context,
                                nameDateList,
                                orderListTotal
                            )
                            recyclerHistoryFragment.adapter = adapterHistoryFragment
                            recyclerHistoryFragment.layoutManager = layoutManagerHistoryFragment

                            recyclerHistoryFragment.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerHistoryFragment.context,
                                    (layoutManagerHistoryFragment as LinearLayoutManager).orientation
                                )
                            )
                        }

                        /* ---------------------------------------------------------------------------------------------------------------------------- */

                        //Errors and Exceptions Catches

                        else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"
                    headers["token"] = "d1f3468e9bfbe5"

                    return headers

                }
            }

            queue.add(jsonRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        return view
    }

}