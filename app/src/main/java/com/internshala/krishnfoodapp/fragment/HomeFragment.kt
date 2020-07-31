package com.internshala.krishnfoodapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.adapter.RecyclerHomeAdapter
import com.internshala.krishnfoodapp.model.Restaurant
import com.internshala.krishnfoodapp.util.ConnectivityManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

private lateinit var recyclerHomeFragment: RecyclerView
private lateinit var layoutManagerHomeFragment: RecyclerView.LayoutManager
private lateinit var adapterHomeFragment: RecyclerHomeAdapter
private lateinit var progressLayoutHome: RelativeLayout
private lateinit var progressBarHome: ProgressBar
private lateinit var sharedPreferences: SharedPreferences

private var listOfRestaurants = arrayListOf<Restaurant>()

/* ---------------------------------------------------------------------------------------------------------------------------- */

//All the required Comparators for filtering the Restaurants List

private var costComparator = Comparator<Restaurant> { res1, res2 ->
    if (res1.cost_for_one.compareTo(res2.cost_for_one, true) == 0) {
        res1.name.compareTo(res2.name, true)
    } else {
        res1.cost_for_one.compareTo(res2.cost_for_one, true)
    }
}

private var nameComparator = Comparator<Restaurant> { res1, res2 ->
    res1.name.compareTo(res2.name, true)
}

private var ratingComparator = Comparator<Restaurant> { res1, res2 ->
    res1.rating.compareTo(res2.rating, true)
}

/* ---------------------------------------------------------------------------------------------------------------------------- */

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        progressLayoutHome = view.findViewById(R.id.progressLayoutHome)
        progressBarHome = view.findViewById(R.id.progressBarHome)
        recyclerHomeFragment = view.findViewById(R.id.recyclerHomeFragment)
        layoutManagerHomeFragment = LinearLayoutManager(activity)
        progressLayoutHome.visibility = View.VISIBLE

        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        ) ?: return null
        val userId = sharedPreferences.getString("user_id", "user_id")

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,

                Response.Listener {

                    try {
                        progressLayoutHome.visibility = View.GONE
                        val mainJsonObject = it.getJSONObject("data")
                        val success = mainJsonObject.getBoolean("success")

                        if (success) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val restaurantListJsonArray = mainJsonObject.getJSONArray("data")
                            listOfRestaurants.clear()

                            for (i in 0 until restaurantListJsonArray.length()) {

                                val restaurantJsonObject = restaurantListJsonArray.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )

                                listOfRestaurants.add(restaurantObject)
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            adapterHomeFragment =
                                RecyclerHomeAdapter(activity as Context, listOfRestaurants, userId)
                            recyclerHomeFragment.adapter = adapterHomeFragment
                            recyclerHomeFragment.layoutManager = layoutManagerHomeFragment

                            recyclerHomeFragment.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerHomeFragment.context,
                                    (layoutManagerHomeFragment as LinearLayoutManager).orientation
                                )
                            )

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

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

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions Required

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.cost_low_to_high -> {
                item.isCheckable
                item.isChecked = true
                Collections.sort(listOfRestaurants, costComparator)
            }

            R.id.cost_high_to_low -> {
                item.isCheckable
                item.isChecked = true
                Collections.sort(listOfRestaurants, costComparator)
                listOfRestaurants.reverse()
            }

            R.id.rating -> {
                item.isCheckable
                item.isChecked = true
                Collections.sort(listOfRestaurants, ratingComparator)
                listOfRestaurants.reverse()
            }

            R.id.name_alphabetic -> {
                item.isCheckable
                item.isChecked = true
                Collections.sort(listOfRestaurants, nameComparator)
            }
        }

        adapterHomeFragment.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

}