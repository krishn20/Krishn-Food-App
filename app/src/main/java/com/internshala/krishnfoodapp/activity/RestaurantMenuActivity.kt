package com.internshala.krishnfoodapp.activity

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.adapter.RecyclerMenuAdapter
import com.internshala.krishnfoodapp.model.Menu
import com.internshala.krishnfoodapp.util.ConnectivityManager
import org.json.JSONException


private lateinit var recyclerMenuActivity: RecyclerView
private lateinit var layoutManagerMenuActivity: RecyclerView.LayoutManager
private lateinit var adapterMenuActivity: RecyclerMenuAdapter
private lateinit var progressLayoutMenu: RelativeLayout
private lateinit var progressBarMenu: ProgressBar
private lateinit var toolbarMenuActivity: androidx.appcompat.widget.Toolbar
private lateinit var proceedToCartButton: Button

private var listOfMenuItems = arrayListOf<Menu>()
private var list_of_already_selected_items = arrayListOf<Menu>()

var restaurant_id: String? = "000"
var restaurant_name: String? = "Restaurant Name"

class RestaurantMenuActivity : AppCompatActivity(), RecyclerMenuAdapter.DataTransferInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        progressLayoutMenu = findViewById(R.id.progressLayoutMenu)
        progressBarMenu = findViewById(R.id.progressBarMenu)
        recyclerMenuActivity = findViewById(R.id.recyclerRestaurantMenuActivity)
        layoutManagerMenuActivity = LinearLayoutManager(this)
        toolbarMenuActivity = findViewById(R.id.toolbarMenuActivity)
        proceedToCartButton = findViewById(R.id.btnProceedToCart)
        progressLayoutMenu.visibility = View.VISIBLE

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //I used LocalBroadcastManager to send the immediate UI changes earlier. Then I used Interfaces for the same. As Interfaces are easier I kept using them and commented out the LBM method instead of deleting it.

//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter("custom-message"))

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        if (intent != null) {
            restaurant_id = intent.getStringExtra("restaurant_id")
            restaurant_name = intent.getStringExtra("restaurant_name")
            list_of_already_selected_items =
                intent.getParcelableArrayListExtra("already_selected_food_items_list")!!
        } else {
            finish()
            Toast.makeText(
                this@RestaurantMenuActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (restaurant_id == "000" && restaurant_name == "Restaurant Name") {
            finish()
            Toast.makeText(
                this@RestaurantMenuActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        setUpToolbar()

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Request passing for Menu Items activity

        val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurant_id"

        if (ConnectivityManager().checkConnectivity(this@RestaurantMenuActivity)) {

            val jsonRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,

                Response.Listener {

                    try {
                        progressLayoutMenu.visibility = View.GONE
                        val mainJsonObject = it.getJSONObject("data")
                        val success = mainJsonObject.getBoolean("success")

                        if (success) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val restaurantMenuListJsonArray = mainJsonObject.getJSONArray("data")
                            println(restaurantMenuListJsonArray)
                            if (restaurantMenuListJsonArray != null && restaurantMenuListJsonArray.length() > 0) {
                                listOfMenuItems.clear()

                                for (i in 0 until restaurantMenuListJsonArray.length()) {

                                    val menuItemJsonObject =
                                        restaurantMenuListJsonArray.getJSONObject(i)
                                    val menuObject = Menu(
                                        menuItemJsonObject.getString("id"),
                                        menuItemJsonObject.getString("name"),
                                        menuItemJsonObject.getString("cost_for_one"),
                                        menuItemJsonObject.getString("restaurant_id")
                                    )

                                    listOfMenuItems.add(menuObject)
                                }

                                println(listOfMenuItems)

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            //Errors and Exception Catches

                            else {
                                Toast.makeText(
                                    this,
                                    "Welcome back to your Favorite Restaurant!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            adapterMenuActivity =
                                RecyclerMenuAdapter(
                                    this,
                                    listOfMenuItems,
                                    list_of_already_selected_items,
                                    this
                                )
                            recyclerMenuActivity.adapter = adapterMenuActivity
                            recyclerMenuActivity.layoutManager = layoutManagerMenuActivity

                            recyclerMenuActivity.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerMenuActivity.context,
                                    (layoutManagerMenuActivity as LinearLayoutManager).orientation
                                )
                            )

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                        } else {
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
            val dialog = AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantMenuActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    //LBM code for UI updation. Now done using Interferences.


//    private var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context?, intent: Intent) {
//            val finalFoodItemList: ArrayList<Menu> = intent.getParcelableArrayListExtra<Menu>("final_food_list")
//            println(4)
//            println(finalFoodItemList)
//
//            if(finalFoodItemList.isEmpty()){
//                proceedToCartButton.visibility = View.INVISIBLE
//            }
//            else{
//                proceedToCartButton.visibility = View.VISIBLE
//                proceedToCartButton.setOnClickListener {
//                    val intentToCart = Intent(this@RestaurantMenuActivity, CartActivity::class.java)
//                    intentToCart.putExtra("final_list", finalFoodItemList)
//                    intentToCart.putExtra("restaurant_id_for_cart", restaurant_id)
//                    intentToCart.putExtra("restaurant_name_for_cart", restaurant_name)
//                    startActivity(intentToCart)
//                }
//            }
//        }
//    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions required

    private fun setUpToolbar() {
        setSupportActionBar(toolbarMenuActivity)
        supportActionBar?.title = restaurant_name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            super.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onProceedToCartBtnStateChanged(
        newState: Boolean,
        foodListToCart: ArrayList<Menu>
    ) {

        if (newState) {
            proceedToCartButton.visibility = View.VISIBLE
            proceedToCartButton.setOnClickListener {
                val intentToCart = Intent(this@RestaurantMenuActivity, CartActivity::class.java)
                intentToCart.putExtra("final_list", foodListToCart)
                intentToCart.putExtra("restaurant_id_for_cart", restaurant_id)
                intentToCart.putExtra("restaurant_name_for_cart", restaurant_name)
                startActivity(intentToCart)
            }
        } else {
            proceedToCartButton.visibility = View.INVISIBLE
        }
    }

}

/* ---------------------------------------------------------------------------------------------------------------------------- */


