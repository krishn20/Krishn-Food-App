package com.internshala.krishnfoodapp.activity

import android.content.Intent
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

private lateinit var mobileNumberForgot: EditText
private lateinit var emailAddForgot: EditText
private lateinit var nextButton: Button

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mobileNumberForgot = findViewById(R.id.etMobileForgot)
        emailAddForgot = findViewById(R.id.etEmailForgot)
        nextButton = findViewById(R.id.btnNext)

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        //Sending PUT Request for Forgot Password Activity

        nextButton.setOnClickListener {

            val mobNumber = mobileNumberForgot.text.toString()
            val emailAdd = emailAddForgot.text.toString()

            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobNumber)
            jsonParams.put("email", emailAdd)

            if (ConnectivityManager().checkConnectivity(this@ForgotPasswordActivity)) {

                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams, Response.Listener {

                        try {

                            val mainJsonObject = it.getJSONObject("data")
                            val success = mainJsonObject.getBoolean("success")

                            if (success) {

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                                val firstTry = mainJsonObject.getBoolean("first_try")

                                if (firstTry) {
                                    Toast.makeText(
                                        applicationContext,
                                        "An OTP has been sent to your registered email address.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intentForgotToReset = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intentForgotToReset.putExtra("mobile_number_forgot", mobNumber)
                                    startActivity(intentForgotToReset)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "An OTP has been sent to your registered email address. You have sent reset requests more than once. Please ensure that you write/save your login credentials somewhere safe to avoid further problems",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intentForgotToReset = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intentForgotToReset.putExtra("mobile_number_forgot", mobNumber)
                                    startActivity(intentForgotToReset)
                                }

                                /* ---------------------------------------------------------------------------------------------------------------------------- */

                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            //Exceptions and Error Catches

                            else {
                                val errorMsg = mainJsonObject.getString("errorMessage")
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "$errorMsg.Please check/correct the credentials and try again! ",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "JSON Exception Occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    },

                    Response.ErrorListener {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
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
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                }

                dialog.create()
                dialog.show()
            }

            /* ---------------------------------------------------------------------------------------------------------------------------- */

        }

    }

    /* ---------------------------------------------------------------------------------------------------------------------------- */

    override fun onBackPressed() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}

/* ---------------------------------------------------------------------------------------------------------------------------- */
