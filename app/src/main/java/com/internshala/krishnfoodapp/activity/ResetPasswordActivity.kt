package com.internshala.krishnfoodapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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

private lateinit var otp: EditText
private lateinit var newPassword: EditText
private lateinit var confirmNewPassword: EditText
private lateinit var buttonSubmit: Button
private lateinit var sharedPreferences: SharedPreferences

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        otp = findViewById(R.id.etOTP)
        newPassword = findViewById(R.id.etNewPassword)
        confirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        buttonSubmit = findViewById(R.id.btnSubmit)

        val mobNumberFromForgotActivity = intent.getStringExtra("mobile_number_forgot")

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Reset Password Submit Button Click Listener

        buttonSubmit.setOnClickListener {

            val otp = otp.text.toString()
            val password = newPassword.text.toString()
            val confirmPassword = confirmNewPassword.text.toString()

            if (password.length >= 4 && confirmPassword.length >= 4 && password == confirmPassword) {

                val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobNumberFromForgotActivity)
                jsonParams.put("password", password)
                jsonParams.put("otp", otp)

                if (ConnectivityManager().checkConnectivity(this@ResetPasswordActivity)) {

                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {

                            try {

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                                val mainJsonObject = it.getJSONObject("data")
                                val success = mainJsonObject.getBoolean("success")

                                if (success) {
                                    val successMessage = mainJsonObject.getString("successMessage")

                                    sharedPreferences.edit().clear().apply()
                                    Toast.makeText(
                                        applicationContext,
                                        "$successMessage. Please Login now to continue.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intentResetToLogin = Intent(
                                        this@ResetPasswordActivity,
                                        LoginActivity::class.java
                                    )
                                    startActivity(intentResetToLogin)
                                    finish()

                                    /* ---------------------------------------------------------------------------------------------------------------------------- */

                                }

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                                //Errors and Exception Catches

                                else {
                                    val errorMsg = mainJsonObject.getString("errorMessage")
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        "$errorMsg / Invalid OTP entered",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "JSON Exception Occurred",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        },

                        Response.ErrorListener {
                            Toast.makeText(
                                this@ResetPasswordActivity,
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
                        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                    }

                    dialog.create()
                    dialog.show()
                }

            } else {
                Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show()
            }

        }

        /* ---------------------------------------------------------------------------------------------------------------------------- */

    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    //Other Functions Required

    //I added moveTaskToBack so that once user asks for the OTP he/she doesn't go back to the previous activity accidentally and this closes the app and save this particular state.
    // User can come back to the state again and enter the OTP when received with the new password to continue.
    // Otherwise if the user wants to go back to the Login Activity again without entering/using OTP usage, they'll have to restart the App from here.

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}

/* ---------------------------------------------------------------------------------------------------------------------------- */
