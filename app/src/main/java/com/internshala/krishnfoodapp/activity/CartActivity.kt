package com.internshala.krishnfoodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.adapter.RecyclerCartAdapter
import com.internshala.krishnfoodapp.model.Menu
import com.internshala.krishnfoodapp.util.ConnectivityManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private lateinit var recyclerCartActivity: RecyclerView
private lateinit var layoutManagerCartActivity: LinearLayoutManager
private lateinit var adapterCartActivity: RecyclerCartAdapter
private lateinit var placeOrderButton: Button
private lateinit var toolbarCart: Toolbar
private lateinit var resName: TextView
private lateinit var sharedPreferences: SharedPreferences
private lateinit var popView: View
private lateinit var popupWindow: PopupWindow

var finalFoodItemListInCart = arrayListOf<Menu>()
var userIdCart: String? = "0000"
var restaurantIdCart: String? = "000"
var restaurantNameCart: String? = "Restaurant Name"
var totalPrice: Int = 0


class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerCartActivity = findViewById(R.id.recyclerCartActivity)
        layoutManagerCartActivity = LinearLayoutManager(this)
        placeOrderButton = findViewById(R.id.btnPlaceOrder)
        toolbarCart = findViewById(R.id.toolbarCartActivity)
        resName = findViewById(R.id.txtRestaurantNameInCart)

        //I have used Parcelable instead of Gson to pass complex(user defined) data items between different activities and fragments.

        if (intent != null) {
            finalFoodItemListInCart = intent.getParcelableArrayListExtra("final_list")
            restaurantIdCart = intent.getStringExtra("restaurant_id_for_cart")
            restaurantNameCart = intent.getStringExtra("restaurant_name_for_cart")
        } else {
            finish()
            Toast.makeText(this@CartActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT)
                .show()
        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        setUpToolbar()
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        userIdCart = sharedPreferences.getString("user_id", "0000")
        resName.text = restaurantNameCart

        //Using a PopUp Window to show that the order has been placed.

        popView = LayoutInflater.from(this).inflate(R.layout.popup_view, null)
        popupWindow = PopupWindow(
            popView,
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        popupWindow.isOutsideTouchable = false

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Calculating total price of selected food items and also making a json array of selected items to pass on as params in the PUT Request to the server.

        for (i in 0 until finalFoodItemListInCart.size) {
            totalPrice += (finalFoodItemListInCart[i].cost_of_item).toInt()
        }

        val price = "Place Order(Total Rs. $totalPrice)"
        placeOrderButton.text = price

        val jsonArray = JSONArray()

        for (i in 0 until finalFoodItemListInCart.size) {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("food_item_id", finalFoodItemListInCart[i].food_id)
            } catch (e: JSONException) {
                Toast.makeText(this, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
            }

            jsonArray.put(jsonObject)
        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Sending items for displaying on screen using Recycler View

        adapterCartActivity = RecyclerCartAdapter(this, finalFoodItemListInCart)
        recyclerCartActivity.adapter = adapterCartActivity
        recyclerCartActivity.layoutManager = layoutManagerCartActivity

        recyclerCartActivity.addItemDecoration(
            DividerItemDecoration(
                recyclerCartActivity.context,
                layoutManagerCartActivity.orientation
            )
        )

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Place Order Button Click Functionality

        placeOrderButton.setOnClickListener {

            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonParams = JSONObject()
            jsonParams.put("user_id", userIdCart)
            jsonParams.put("restaurant_id", restaurantIdCart)
            jsonParams.put("total_cost", totalPrice)
            jsonParams.put("food", jsonArray)

            if (ConnectivityManager().checkConnectivity(this@CartActivity)) {

                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,

                    Response.Listener {

                        try {
                            val mainJsonObject = it.getJSONObject("data")
                            val success = mainJsonObject.getBoolean("success")

                            if (success) {

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                                //Showing PopUp Window Message which on clicking will redirect the user back to the Home Fragment/Restaurants List Activity.

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    popupWindow.elevation = 10.0F
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    popupWindow.enterTransition
                                    popupWindow.exitTransition
                                }

                                val buttonPopup = popView.findViewById<Button>(R.id.btnOrderPlaced)
                                buttonPopup.setOnClickListener {
                                    popupWindow.dismiss()
                                }

                                popupWindow.setOnDismissListener {
                                    Toast.makeText(
                                        applicationContext,
                                        "Check your Order History for your placed orders.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intentBackToDashboard =
                                        Intent(this, DashboardActivity::class.java)
                                    startActivity(intentBackToDashboard)
                                    totalPrice = 0
                                    finish()
                                }

                                TransitionManager.beginDelayedTransition(findViewById(R.id.rootLayoutForPopup))
                                popupWindow.showAtLocation(
                                    findViewById(R.id.rootLayoutForPopup), Gravity.CENTER, 0, 0
                                )
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            //Exception/error catches

                            else {
                                Toast.makeText(
                                    this,
                                    "Some Error Occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                this,
                                "Some unexpected error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },

                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Failure!")
                dialog.setMessage("Couldn't connect to the Internet")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }


        }
    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions required

    private fun setUpToolbar() {
        setSupportActionBar(toolbarCart)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {

        if (popupWindow.isShowing) {
            Toast.makeText(
                applicationContext,
                "Check your Order History for your placed orders.",
                Toast.LENGTH_SHORT
            ).show()
            val intentBackToDashboard = Intent(this, DashboardActivity::class.java)
            startActivity(intentBackToDashboard)
            totalPrice = 0
            finish()
        } else {
            totalPrice = 0
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            totalPrice = 0
            super.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}

/* ---------------------------------------------------------------------------------------------------------------------------- */