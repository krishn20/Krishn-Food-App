package com.internshala.krishnfoodapp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.activity.RestaurantMenuActivity
import com.internshala.krishnfoodapp.database.RestaurantDatabase
import com.internshala.krishnfoodapp.database.RestaurantEntity
import com.internshala.krishnfoodapp.model.Menu
import com.internshala.krishnfoodapp.model.Restaurant
import com.squareup.picasso.Picasso

class RecyclerHomeAdapter(
    val context: Context,
    private val listOfRestaurants: ArrayList<Restaurant>,
    private val userId: String?
) : RecyclerView.Adapter<RecyclerHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerHomeAdapter.HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_list_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfRestaurants.size
    }

    override fun onBindViewHolder(holder: RecyclerHomeAdapter.HomeViewHolder, position: Int) {
        val restaurant = listOfRestaurants[position]

        holder.name.text = restaurant.name
        holder.rating.text = restaurant.rating
        val cost = restaurant.cost_for_one + "/per person"
        holder.costForOne.text = cost
        Picasso.get().load(restaurant.restaurant_img).error(R.drawable.img_default_food)
            .into(holder.restaurantImg)

        //Making a Restaurant entity and adding it to the table. This will then be used by the database to perform operations(using DAO).

        val resEntity = RestaurantEntity(
            restaurant.restaurant_id.toInt(),
            userId?.toInt() as Int,
            restaurant.name,
            restaurant.rating,
            restaurant.cost_for_one,
            restaurant.restaurant_img
        )

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Performing UI updation as per the DB values

        val checkFav = DBAsyncTask(context, resEntity, 1).execute()
        val isFav = checkFav.get()

        holder.btnHeart.isChecked = isFav

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        holder.btnHeart.setOnClickListener {
            if (!DBAsyncTask(context, resEntity, 1).execute()
                    .get()
            ) {

                val async =
                    DBAsyncTask(context, resEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to Favorites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.btnHeart.isChecked

                } else {
                    Toast.makeText(
                        context,
                        "Some unexpected error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async =
                    DBAsyncTask(context, resEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from Favorites.",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.btnHeart.isChecked = false

                } else {
                    Toast.makeText(
                        context,
                        "Some unexpected error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        holder.llRestaurants.setOnClickListener {
            val alreadySelectedFoodItemsList = arrayListOf<Menu>()
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurant_id)
            intent.putExtra("restaurant_name", restaurant.name)
            intent.putExtra("already_selected_food_items_list", alreadySelectedFoodItemsList)
            context.startActivity(intent)
        }

    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtRestaurantName)
        val rating: TextView = view.findViewById(R.id.txtRating)
        val costForOne: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val restaurantImg: ImageView = view.findViewById(R.id.imgRestaurant)
        val btnHeart: ToggleButton = view.findViewById(R.id.btnFavHeart)
        val llRestaurants: LinearLayout = view.findViewById(R.id.llRestaurants)
    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {

        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            //Mode 1 -> Check to see if Restaurant in favorites db or not
            //Mode 2 -> Insert restaurant into favorites db
            //Mode 3 -> Delete restaurant from favorites db

            when (mode) {
                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getResById(
                            restaurantEntity.res_id.toString(),
                            restaurantEntity.userId.toString()
                        )
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRes(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRes(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false

        }

    }
}

/* ---------------------------------------------------------------------------------------------------------------------------- */
