package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.data.Exercise
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
import com.example.myapplication.data.User
import com.example.myapplication.navigation_pages.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MainActivity: AppCompatActivity()  {
    private lateinit var navView : BottomNavigationView
    private lateinit var database: DatabaseReference
    val random = Random()
    val randomNumber = random.nextInt(1000)
    /*var sportsmensList = arrayListOf<Item>(
        Item("Item 1", "https://picsum.photos/200?random=$randomNumber", "Item 1"),
        Item("Item 2", "https://picsum.photos/200?random=$randomNumber+1", "Item 2"),
        Item("Item 3", "https://picsum.photos/200?random=$randomNumber+2", "Item 3"),
        Item("Item 4", "https://picsum.photos/200?random=$randomNumber+3", "Item 4"),
        Item("Item 5", "https://picsum.photos/200?random=$randomNumber+4", "Item 5"))*/
    var temp = arrayListOf<String>(
        "Anton",
        "Alexey")

    var GroupsList = arrayListOf<Group>(
        Group("Item 1", "Item 1", temp),
        Group("Item 2", "Item 2", temp)
    )
/*
    var tmp = arrayListOf<Exercise>(
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+5", Date(), "JUST DO IT", 'p', "", "Item 1"),
        Exercise("Интервальный бег", "https://picsum.photos/200?random=$randomNumber+6", Date(), "Kirby the world eater", 'p', "", "Item 2"),
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+7", Date(), "Kept you waiting, huh?", 'p', "", "Item 3"))
    var tmp1 = arrayListOf<Exercise>(
        Exercise("Интервальный бег", "https://picsum.photos/200?random=$randomNumber+8", Date(), "Keep on keeping on", 'p', "", "Item 1"),
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+9", Date(), "DO IT", 'p', "", "Item 2"),
        Exercise("Интервальный бег", "https://picsum.photos/200?random=$randomNumber+10", Date(), "HAPATA", 'p', "", "Item 3")
    )
    var exerciseList1 = ArrayMap<String, MutableList<Exercise>>().apply{
        put("Item 1", tmp)
        put("Item 2", tmp1)
    }*/

    lateinit var preferences: SharedPreferences
    lateinit var sportsmensList: MutableList<Item>
    lateinit var exerciseList: ArrayMap<String, MutableList<Exercise>>
    lateinit var profileList: ArrayMap<String, String>
    var user = "C"

    var statemap = ArrayMap<Char, String>().apply {
        put('p', "Запланирована")
        put('c',"Выполнена")
        put('h', "Выполнена частично")
        put('f', "Не выполнена")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        val sportsmensListJson = (preferences.getString("sportsmensList", null))
        //var editor = preferences.edit()
        //editor.putString("exerciseList", Gson().toJson(exerciseList1))
        //editor.putBoolean("isLoggedIn", false)
        //editor.apply()
        sportsmensList = sportsmensListJson?.let {
            Gson().fromJson<MutableList<Item>>(it, object : TypeToken<ArrayList<Item>>() {}.type)
        } ?: arrayListOf()
        val exerciseListJson = (preferences.getString("exerciseList", null))
        exerciseList =  exerciseListJson?.let {
            Gson().fromJson<ArrayMap<String, MutableList<Exercise>>>(it, object : TypeToken<ArrayMap<String, MutableList<Exercise>>>() {}.type)
        } ?: ArrayMap()
        val profileListJson = (preferences.getString("profileList", null))
        profileList =  profileListJson?.let {
            Gson().fromJson<ArrayMap<String, String>>(it, object : TypeToken<ArrayMap<String, String>>() {}.type)
        } ?: ArrayMap()
        database = FirebaseDatabase.getInstance().getReference("users")
        val isLoggedIn = preferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            //getRole()
            startActivity(intent)
            finish()
        } else {
            val editor = preferences.edit()
            editor.putString("exerciseList", Gson().toJson(exerciseList))
            editor.putString("sportsmensList", Gson().toJson(sportsmensList))
            Log.d("check", sportsmensList.toString())
            editor.apply()
            //было сделано чтение из бд ^
            //user = "C" //Надо сделать чтение из бд
            val currentUser = Firebase.auth.currentUser
            lateinit var email:String
            currentUser?.let {
                email = it.email.toString()
            }
            database = Firebase.database.reference
            database.child("users").child(email.split("@")[0]).get().addOnSuccessListener {
                if (it.exists()){
                    user = it.child("role").value.toString()
                    Log.d("ROLE = ", user)
                    if (user == "C") {
                        Log.d("WAT", "WAT")
                        setContentView(R.layout.c_activity_main)
                        navView = findViewById(R.id.c_bottom_navigation)
                        loadFragment(SportsmensFragment())
                        navView?.setOnItemSelectedListener {
                            when (it.itemId) {
                                R.id.sportsmens -> loadFragment(SportsmensFragment())
                                R.id.groups -> loadFragment(GroupsFragment())
                                R.id.profile -> loadFragment(ProfileFragment())
                                else -> {

                                }
                            }
                            true
                        }
                    } else if (user == "S") {
                        setContentView(R.layout.s_activity_main)
                        navView = findViewById(R.id.s_bottom_navigation)
                        loadFragment(ExerciseFragment())
                        navView?.setOnItemSelectedListener {
                            when (it.itemId) {
                                R.id.exercise -> loadFragment(ExerciseFragment())
                                R.id.diary -> loadFragment(DiaryFragment())
                                R.id.profile -> loadFragment(ProfileSportsmenFragment())
                                else -> {

                                }
                            }
                            true
                        }
                    }

                }else{
                    Log.d("Huiy","User does not exist")
                }
            }.addOnFailureListener{
                Log.d("Huiy","Pizdec")
            }
            Log.d("ROLE1 = ", user)
        }
    }
    override fun onBackPressed() {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_sportsmens)
        var currentFragment1 = supportFragmentManager.findFragmentById(R.id.fragment_groups)
        var currentFragment2 = supportFragmentManager.findFragmentById(R.id.fragment_exercise)
        Log.d("MainActivity", "currentFragment: $currentFragment")
        Log.d("MainActivity", "currentFragment: $currentFragment1")
        if (user == "C" && R.id.sportsmens != navView.selectedItemId) {
            if(R.id.groups == navView.selectedItemId){
                if(currentFragment1 is ExercisesInGroup){
                    super.onBackPressed()
                    setNavViewVisibility(true)
                }
                else {
                    navView.selectedItemId = R.id.sportsmens
                    loadFragment(SportsmensFragment())
                }
            }
            else{
                navView.selectedItemId = R.id.sportsmens
                loadFragment(SportsmensFragment())
            }
        }
        else if (currentFragment is SportsmensFragmentDialog) {
// Handle back button press inside the dialog fragment
            super.onBackPressed()
            setNavViewVisibility(true)
        }
        else if (user == "S" && R.id.exercise != navView.selectedItemId) {

            navView.selectedItemId = R.id.exercise
            loadFragment(ExerciseFragment())
        }
        else if (currentFragment2 is ExerciseFragmentDialog) {
// Handle back button press inside the dialog fragment
            super.onBackPressed()
            setNavViewVisibility(true)
        }
        else {
            super.onBackPressed()
            finishAffinity()
        }
    }

        private fun loadFragment(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        }

        // Add a method to hide/show the navigation view
        fun setNavViewVisibility(visible: Boolean) {
            navView.visibility = if (visible) View.VISIBLE else View.GONE
        }

}