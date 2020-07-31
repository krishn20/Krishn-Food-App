package com.internshala.krishnfoodapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.fragment.*

private lateinit var drawerLayout: DrawerLayout
private lateinit var coordinatorLayout: CoordinatorLayout
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var frameLayout: FrameLayout
private lateinit var navigationView: NavigationView
private lateinit var headerView: View
private lateinit var sharedPreferences: SharedPreferences

private var previousMenuItem: MenuItem? = null


class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbar = findViewById(R.id.toolbarDashboard)
        coordinatorLayout = findViewById(R.id.coordinator_layout_dashboard)
        drawerLayout = findViewById(R.id.drawer_layout_dashboard)
        frameLayout = findViewById(R.id.frame_layout_dashboard)
        navigationView = findViewById(R.id.navigation_view_dashboard)
        headerView = navigationView.getHeaderView(0)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Setting up Toolbar, Home Fragment and Home Button

        setUpToolbar()
        openHomeDashboard()
        setDrawerCredentials()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@DashboardActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Navigation View Item Selections

        navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.home -> {
                    it.isCheckable = true
                    it.isChecked = true
                    previousMenuItem = it
                    openHomeDashboard()
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    it.isCheckable = true
                    it.isChecked = true
                    previousMenuItem = it
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_dashboard, MyProfileFragment()).commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.favorites -> {
                    it.isCheckable = true
                    it.isChecked = true
                    previousMenuItem = it
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_dashboard, FavoritesFragment()).commit()
                    supportActionBar?.title = "Favorite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.order_history -> {
                    it.isCheckable = true
                    it.isChecked = true
                    previousMenuItem = it
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_dashboard, OrderHistoryFragment()).commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.faq -> {
                    it.isCheckable = true
                    it.isChecked = true
                    previousMenuItem = it
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_dashboard, FaqFragment()).commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@DashboardActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to logout?")

                    dialog.setPositiveButton("Yes") { text, listener ->

                        sharedPreferences.edit().clear().apply()

                        Toast.makeText(
                            this,
                            "We'll miss you ! Login again soon !",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    dialog.setNegativeButton("No") { text, listener ->
                        previousMenuItem?.let { it1 -> navigationView.setCheckedItem(it1) }
                    }

                    dialog.create()
                    dialog.show()
                    drawerLayout.closeDrawers()
                }
            }

            return@setNavigationItemSelectedListener true

        }

    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions Required

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openHomeDashboard() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_dashboard, HomeFragment()).commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    private fun setDrawerCredentials() {
        val drawerNameHeader: TextView = headerView.findViewById(R.id.txtNameHeader)
        val drawerMobileHeader: TextView = headerView.findViewById(R.id.txtMobileHeader)
        drawerNameHeader.text = sharedPreferences.getString("name", "User's Name")
        val mobileNumber = "+91-" + sharedPreferences.getString("mobile_number", "1115555555")
        drawerMobileHeader.text = mobileNumber

        println(sharedPreferences.getString("user_id", "000000"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START, true)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame_layout_dashboard)
        when (frag) {
            !is HomeFragment -> openHomeDashboard()
            else -> finishAffinity()
        }
    }

}

/* ---------------------------------------------------------------------------------------------------------------------------- */
