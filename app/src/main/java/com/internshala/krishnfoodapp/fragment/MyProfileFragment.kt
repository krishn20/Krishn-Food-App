package com.internshala.krishnfoodapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.internshala.krishnfoodapp.R

private lateinit var nameMyProfile: TextView
private lateinit var numberMyProfile: TextView
private lateinit var emailMyProfile: TextView
private lateinit var addressMyProfile: TextView
private lateinit var sharedPreferences: SharedPreferences

class MyProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        nameMyProfile = view.findViewById(R.id.txtMyProfileName)
        numberMyProfile = view.findViewById(R.id.txtMyProfileNumber)
        emailMyProfile = view.findViewById(R.id.txtMyProfileEmail)
        addressMyProfile = view.findViewById(R.id.txtMyProfileAddress)

        sharedPreferences = this.activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        ) ?: return null

        nameMyProfile.text = sharedPreferences.getString("name", "User's Name")
        val number = "+91-" + sharedPreferences.getString("mobile_number", "+91-1115555555")
        numberMyProfile.text = number
        emailMyProfile.text = sharedPreferences.getString("email", "john@doe.com")
        addressMyProfile.text = sharedPreferences.getString("address", "User's Address")

        return view
    }
}