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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.myapplication.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*


class ProfileSportsmenFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var myImageView: ImageView
    private lateinit var options_btn: ImageView
    private lateinit var edit_btn: ImageView
    private lateinit var lastname: EditText
    private lateinit var profile_id: TextView
    private lateinit var profile_login: TextView
    private lateinit var profile_coach: TextView
    private lateinit var name: EditText
    private lateinit var delete_btn: TextView
    private lateinit var datebirth: EditText
    private lateinit var logout_btn: TextView
    private lateinit var unsign_btn: TextView
    private lateinit var itemList: ArrayMap<String, String>
    private lateinit var height: EditText
    private lateinit var weight: EditText
    private lateinit var css: EditText
    lateinit var preferences: SharedPreferences
    lateinit var editor :  SharedPreferences.Editor
    private lateinit var database: DatabaseReference
override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_profile_s, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference
        database = database.child("users")
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
        profile_id = view.findViewById(R.id.profile_id)
        profile_login = view.findViewById(R.id.profile_login)
        profile_coach = view.findViewById(R.id.profile_coach)
        name = view.findViewById(R.id.profile_nameedit)
        datebirth = view.findViewById(R.id.profile_datebirthedit)
        options_btn = view.findViewById(R.id.options_button)
        edit_btn = view.findViewById(R.id.edit_button)
        logout_btn = view.findViewById(R.id.logout_btn)
        unsign_btn = view.findViewById(R.id.unsign_btn)
        delete_btn = view.findViewById(R.id.delete_btn)
        height = view.findViewById(R.id.profile_heightedit)
        weight = view.findViewById(R.id.profile_weightedit)
        css = view.findViewById(R.id.profile_pulseedit)
        Log.d("profileList",itemList.toString())
        var dateArray = itemList["birthDate"]?.split('/')
        var day = dateArray?.get(0)?.toInt()
        var month = dateArray?.get(1)?.toInt()
        var year = dateArray?.get(2)?.toInt()
        Log.d("Check", Date().year.toString() +'|'+dateArray)
        var tmp1 = 0
        if(Date().month<= month!! && Date().day< day!!)
            tmp1=1
        Log.d("Check", (1900.toString() +"+"+Date().year.toString())+"-"+month+"|"+year+"-"+tmp1)

        lastname.setText(itemList["lastName"].toString())
        profile_id.setText(itemList["id"].toString())
        profile_login.setText(itemList["login"].toString())
        name.setText(itemList["firstName"].toString())
        datebirth.setText(itemList["birthDate"].toString())
        height.setText(itemList["height"].toString())
        weight.setText(itemList["weight"].toString())
        delete_btn.visibility = GONE
        css.setText((220 - ((1900+Date().year)- year!! -tmp1)).toString())
        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val preferences = requireContext().getSharedPreferences("my_prefs", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        delete_btn.setOnClickListener{
            val user = Firebase.auth.currentUser!!

            var email = user.email!!
            database.child("users").child(email.split("@")[0]).removeValue()
            database.child("Exercise").child(email.split("@")[0]).removeValue()

            user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("delete", "User account deleted.")
                    }
                }

        }
        options_btn.setOnClickListener {
            Log.d("check", "works")
            options_btn.visibility = GONE
            logout_btn.visibility = GONE
            delete_btn.visibility = VISIBLE
            edit_btn.visibility = VISIBLE
            lastname.isFocusable = true
            lastname.isFocusableInTouchMode = true
            name.isFocusable = true
            name.isFocusableInTouchMode = true
            height.isFocusable = true
            height.isFocusableInTouchMode = true
            weight.isFocusable = true
            weight.isFocusableInTouchMode = true
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
                itemList["lastName"]=lastname.text.toString()
                itemList["firstName"]=name.text.toString()
                itemList["birthDate"]=datebirth.text.toString()
                itemList["weight"]=weight.text.toString()
                itemList["height"]=height.text.toString()
                var dateArray = itemList["birthDate"]?.split('/')
                var day = dateArray?.get(0)?.toInt()
                var month = dateArray?.get(1)?.toInt()
                var year = dateArray?.get(2)?.toInt()
                Log.d("Check", Date().year.toString() +'|'+dateArray)
                var tmp1 = 0
                if(Date().month.toInt()<= month!! && Date().day< day!!)
                    tmp1=1
                css.setText((220 - ((1900+Date().year)- year!! -tmp1)).toString())
                itemList["css"]=css.text.toString()
                database.child(itemList["id"].toString()).setValue(itemList).addOnCompleteListener {
                    Toast.makeText(requireContext(),"Insert done", Toast.LENGTH_LONG).show()
                    Log.d("R","REG")
                }.addOnFailureListener{err ->
                    Toast.makeText(requireContext(),"Error ${err.message}", Toast.LENGTH_LONG).show()
                }
                editor.putString("profileList", Gson().toJson(itemList))
                editor.apply()
                options_btn.visibility = VISIBLE
                edit_btn.visibility = GONE
                delete_btn.visibility = GONE
                logout_btn.visibility = VISIBLE
                lastname.isFocusable = false
                lastname.isFocusableInTouchMode = false
                name.isFocusable = false
                name.isFocusableInTouchMode = false
                datebirth.isEnabled = false
                height.isFocusable = false
                weight.isFocusable = false
                height.isFocusableInTouchMode = false
                weight.isFocusableInTouchMode = false
            }
        }
        unsign_btn.setOnClickListener {
            val currentUser = Firebase.auth.currentUser
            lateinit var email: String
            currentUser?.let {
                email = it.email.toString()
            }
            val reference = database.child(email.split("@")[0]).child("coach")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {
                    TODO("not implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val value = snapshot.getValue()
                        //var coach = database.child(email.split("@")[0]).child("coach").get()
                        Log.d("check", value.toString())
                        Log.d("check", email.split("@")[0])
                        Log.d("check", database.child(email.split("@")[0]).toString())
                        database.child(value.toString()).child("list_of_sportsmen")
                            .child(email.split("@")[0]).removeValue()
                        database.child(email.split("@")[0]).child("coach").setValue("")
                    } else {
                        // Key doesn't exist in the database
                    }
                }
            })
        }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        datebirth.setText("$dayOfMonth/$month/$year")
    }
}
