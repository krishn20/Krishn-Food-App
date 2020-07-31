package com.internshala.krishnfoodapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.krishnfoodapp.R
import com.internshala.krishnfoodapp.util.ConnectivityManager
import org.json.JSONException
import org.json.JSONObject

private lateinit var mobileNumber: EditText
private lateinit var password: EditText
private lateinit var loginBtn: Button
private lateinit var sharedPreferences: SharedPreferences

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Checking if user was already logged in or not

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        mobileNumber = findViewById(R.id.etMobileLogin)
        password = findViewById(R.id.etPassLogin)
        loginBtn = findViewById(R.id.btnLogin)

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Login Button Click Listener

        loginBtn.setOnClickListener {

            val mobNumber = mobileNumber.text.toString()
            val pass = password.text.toString()

            if (pass.length >= 4) {

                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobNumber)
                jsonParams.put("password", pass)

                if (ConnectivityManager().checkConnectivity(this@LoginActivity)) {

                    val jsonRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

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
                                        "Hello Foodie. Welcome back to the Food App!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent =
                                        Intent(this@LoginActivity, DashboardActivity::class.java)
                                    startActivity(intent)

                                    /* ---------------------------------------------------------------------------------------------------------------------------- */

                                }

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                                //Errors and Exceptions catches

                                else {
                                    val errorMsg = mainJsonObject.getString("errorMessage")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "$errorMsg.Please check/correct the credentials and try again! ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "JSON Exception Occurred",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        },

                            Response.ErrorListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Volley Error Occurred!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val header = HashMap<String, String>()
                            header["Content-type"] = "application/json"
                            header["token"] = "d1f3468e9bfbe5"

                            return header
                        }
                    }

                    queue.add(jsonRequest)

                } else {

                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Failure!")
                    dialog.setMessage("Couldn't connect to the Internet.")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }

                    dialog.create()
                    dialog.show()
                }


            } else {
                Toast.makeText(this, "Invalid Mobile Number or Password!", Toast.LENGTH_LONG).show()
            }

        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions Required

    fun onForgotPasswordTextClick(view: View) {
        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    fun onRegisterTextClick(view: View) {
        val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
        startActivity(intent)
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
        finishAffinity()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}

/* ---------------------------------------------------------------------------------------------------------------------------- */
