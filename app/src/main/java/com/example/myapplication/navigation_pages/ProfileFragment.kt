package com.example.myapplication.navigation_pages

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myapplication.LoginActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson


class ProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var myImageView: ImageView
    private lateinit var options_btn: ImageView
    private lateinit var edit_btn: ImageView
    private lateinit var lastname: EditText
    private lateinit var name: EditText
    private lateinit var datebirth: EditText
    private lateinit var logout_btn: TextView
    private lateinit var itemList: ArrayMap<String, String>
    lateinit var preferences: SharedPreferences
    lateinit var editor :  SharedPreferences.Editor
override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences= requireContext().getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        editor= preferences.edit()
        itemList = (requireActivity() as MainActivity).profileList
        myImageView = view.findViewById(R.id.circle_image)
        val randomImageUrl = "https://picsum.photos/200" // Replace with URL of your image
        Glide.with(this)
            .load(randomImageUrl)
            .transform(CircleCrop())
            .placeholder(R.drawable.circular_background) // placeholder image while the actual image loads
            //.error(R.drawable.error_image) // image to display if there is an error loading the actual image
            .into(myImageView)
        lastname = view.findViewById(R.id.profile_lastnameedit)
        name = view.findViewById(R.id.profile_nameedit)
        datebirth = view.findViewById(R.id.profile_datebirthedit)
        options_btn = view.findViewById(R.id.options_button)
        edit_btn = view.findViewById(R.id.edit_button)
        logout_btn = view.findViewById(R.id.logout_btn)
        Log.d("profileList",itemList.toString())
        lastname.setText(itemList["lastname"].toString())
        name.setText(itemList["firsname"].toString())
        datebirth.setText(itemList["datebirth"].toString())
        logout_btn.setOnClickListener {
            //FirebaseAuth.getInstance().signOut() - Отключено на время отладки и разработки
            val preferences = requireContext().getSharedPreferences("my_prefs", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        options_btn.setOnClickListener {
            Log.d("check", "works")
            options_btn.visibility = GONE
            edit_btn.visibility = VISIBLE
            lastname.isFocusable = true
            lastname.isFocusableInTouchMode = true
            name.isFocusable = true
            name.isFocusableInTouchMode = true
            val c: Calendar
            c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            datebirth.isEnabled=true
            datebirth.setOnClickListener {
                DatePickerDialog(requireContext(), this, year, month, day).show()
                datebirth.setError(null)}
            edit_btn.setOnClickListener {
                itemList["lastname"]=lastname.text.toString()
                itemList["firsname"]=name.text.toString()
                itemList["datebirth"]=datebirth.text.toString()
                editor.putString("profileList", Gson().toJson(itemList))
                editor.apply()
                options_btn.visibility = VISIBLE
                edit_btn.visibility = GONE
                lastname.isFocusable = false
                lastname.isFocusableInTouchMode = false
                name.isFocusable = false
                name.isFocusableInTouchMode = false
                datebirth.isEnabled = false
            }
        }

    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        datebirth.setText("$dayOfMonth/$month/$year")
    }
}
