package com.internshala.krishnfoodapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.adapter.RecyclerFavoritesAdapter
import com.internshala.krishnfoodapp.database.RestaurantDatabase
import com.internshala.krishnfoodapp.database.RestaurantEntity

private lateinit var recyclerFavorite: RecyclerView
private lateinit var progressLayout: RelativeLayout
private lateinit var progressBar: ProgressBar
private lateinit var imageFavToShowWhenEmptyList: ImageView
private lateinit var textFavToShowWhenEmptyList: TextView
private lateinit var layoutManager: GridLayoutManager
private lateinit var adapterRecyclerFavorites: RecyclerFavoritesAdapter
private lateinit var favLine: TextView
private lateinit var favBar: View
private lateinit var sharedPreferences: SharedPreferences
private lateinit var userId: String

private var dbResList = listOf<RestaurantEntity>()

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorite = view.findViewById(R.id.recyclerFavorite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        imageFavToShowWhenEmptyList = view.findViewById(R.id.imgToShowWhenEmpty)
        textFavToShowWhenEmptyList = view.findViewById(R.id.txtToShowWhenEmpty)
        favLine = view.findViewById(R.id.txtFavResBelow)
        favBar = view.findViewById(R.id.favBar)

        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        ) ?: return null
        userId = sharedPreferences.getString("user_id", "user_id").toString()

        layoutManager = GridLayoutManager(activity as Context, 2)
        dbResList = RetrieveFavorites(activity as Context).execute().get()

        progressLayout.visibility = View.VISIBLE

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        if (activity != null) {
            progressLayout.visibility = View.GONE

            if (dbResList.isEmpty()) {
                favLine.visibility = View.GONE
                favBar.visibility = View.GONE
                imageFavToShowWhenEmptyList.visibility = View.VISIBLE
                textFavToShowWhenEmptyList.visibility = View.VISIBLE
                Toast.makeText(
                    context,
                    "You have no favorite restaurants as of now!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                imageFavToShowWhenEmptyList.visibility = View.GONE
                textFavToShowWhenEmptyList.visibility = View.GONE
                adapterRecyclerFavorites = RecyclerFavoritesAdapter(activity as Context, dbResList)
                recyclerFavorite.adapter = adapterRecyclerFavorites
                recyclerFavorite.layoutManager = layoutManager
            }
        }

        return view
    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Using the Async Task and DB to get all favorite restaurants

    class RetrieveFavorites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
            return db.restaurantDao().getAllRestaurants(userId.toString())
        }

    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

}