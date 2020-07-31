package com.internshala.krishnfoodapp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.activity.RestaurantMenuActivity
import com.internshala.krishnfoodapp.database.RestaurantDatabase
import com.internshala.krishnfoodapp.database.RestaurantEntity
import com.internshala.krishnfoodapp.model.Menu
import com.squareup.picasso.Picasso

class RecyclerFavoritesAdapter(
    val context: Context,
    private val dbResList: List<RestaurantEntity>
) : RecyclerView.Adapter<RecyclerFavoritesAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_favorite_single_row, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dbResList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {

        val res = dbResList[position]

        holder.textResName.text = res.resName
        val cost = "Rs." + res.resCostForOne + "/person"
        holder.textResCostForOne.text = cost
        holder.textResRating.text = res.resRating
        Picasso.get().load(res.resImage).error(R.drawable.img_default_food).into(holder.imgResImage)

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Performing item updation as per the DB values

        val checkFav = DBAsyncTask(context, res, 1).execute()
        val isFav = checkFav.get()

        holder.btnFavHeart.isChecked = isFav

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        holder.btnFavHeart.setOnClickListener {
            if (!DBAsyncTask(context, res, 1).execute()
                    .get()
            ) {

                val async =
                    DBAsyncTask(context, res, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to Favorites!",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.btnFavHeart.isChecked

                } else {
                    Toast.makeText(
                        context,
                        "Some unexpected error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async =
                    DBAsyncTask(context, res, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from Favorites.",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.btnFavHeart.isChecked = false

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

        holder.rlContent.setOnClickListener {
            val alreadySelectedFoodItemsList = arrayListOf<Menu>()
            val intentFromFav = Intent(context, RestaurantMenuActivity::class.java)
            intentFromFav.putExtra("restaurant_id", res.res_id.toString())
            intentFromFav.putExtra("restaurant_name", res.resName)
            intentFromFav.putExtra("already_selected_food_items_list", alreadySelectedFoodItemsList)
            context.startActivity(intentFromFav)
        }
    }

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textResName: TextView = view.findViewById(R.id.txtFavResName)
        val textResCostForOne: TextView = view.findViewById(R.id.txtFavResCostForOne)
        val textResRating: TextView = view.findViewById(R.id.txtFavResRating)
        val imgResImage: ImageView = view.findViewById(R.id.imgFavResImage)
        val btnFavHeart: ToggleButton = view.findViewById(R.id.btnHeartFavorites)
        val rlContent: RelativeLayout = view.findViewById(R.id.rlFavContent)
    }

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
