package com.internshala.krishnfoodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.util.ConnectivityManager
import org.json.JSONException
import org.json.JSONObject

private lateinit var name: EditText
private lateinit var email: EditText
private lateinit var mobile_number: EditText
private lateinit var address: EditText
private lateinit var password: EditText
private lateinit var confirm_password: EditText
private lateinit var registerBtn: Button
private lateinit var sharedPreferences: SharedPreferences
private lateinit var toolbar: Toolbar

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        name = findViewById(R.id.etName)
        email = findViewById(R.id.etMailAddress)
        mobile_number = findViewById(R.id.etMobileRegister)
        address = findViewById(R.id.etDeliveryAddress)
        password = findViewById(R.id.etPassRegister)
        confirm_password = findViewById(R.id.etConfirmPassRegister)
        registerBtn = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbarRegister)

        setUpToolbar()

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Register Button Click Listener

        registerBtn.setOnClickListener {

            val name = name.text.toString()
            val email = email.text.toString()
            val mobileNumber = mobile_number.text.toString()
            val address = address.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirm_password.text.toString()

            if (name.length >= 3 && password.length >= 4 && password == confirmPassword) {

                val queue = Volley.newRequestQueue(this@RegistrationActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)
                jsonParams.put("address", address)
                jsonParams.put("email", email)

                if (ConnectivityManager().checkConnectivity(this@RegistrationActivity)) {

                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {

                                val mainJsonObject = it.getJSONObject("data")
                                val success = mainJsonObject.getBoolean("success")

                                if (success) {

                                    /* ---------------------------------------------------------------------------------------------------------------------------- */

                                    val userInfoJsonObject = mainJsonObject.getJSONObject("data")

                                    val userId = userInfoJsonObject.getString("user_id")
                                    val name = userInfoJsonObject.getString("name")
                                    val email = userInfoJsonObject.getString("email")
                                    val mobileNumber = userInfoJsonObject.getString("mobile_number")
                                    val address = userInfoJsonObject.getString("address")

                                    savePreferences(userId, name, email, mobileNumber, address)

                                    Toast.makeText(
                                        applicationContext,
                                        "Registration Successful. Welcome to the Food App!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(
                                        this@RegistrationActivity,
                                        DashboardActivity::class.java
                                    )
                                    startActivity(intent)

                                    /* ---------------------------------------------------------------------------------------------------------------------------- */

                                }

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                                //Errors and Exceptions Catches

                                else {

                                    val errorMsg = mainJsonObject.getString("errorMessage")
                                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
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
                            Toast.makeText(this, "Volley error occurred", Toast.LENGTH_SHORT).show()
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
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
                    dialog.setTitle("Failure!")
                    dialog.setMessage("Couldn't connect to the Internet.")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegistrationActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            } else {
                Toast.makeText(
                    this,
                    "Please check/correct the credentials according to the given conditions and then continue!",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions Required

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun savePreferences(
        user_id: String,
        name: String,
        email: String,
        mobile_number: String,
        address: String
    ) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("user_id", user_id).apply()
        sharedPreferences.edit().putString("name", name).apply()
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("mobile_number", mobile_number).apply()
        sharedPreferences.edit().putString("address", address).apply()
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}

/* ---------------------------------------------------------------------------------------------------------------------------- */
