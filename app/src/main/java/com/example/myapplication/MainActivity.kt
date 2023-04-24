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
import com.example.myapplication.navigation_pages.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class MainActivity: AppCompatActivity()  {
    private lateinit var navView : BottomNavigationView
    val random = Random()
    val randomNumber = random.nextInt(1000)
    /*var sportsmensList = arrayListOf<Item>(
        Item("Item 1", "https://picsum.photos/200?random=$randomNumber", "Item 1"),
        Item("Item 2", "https://picsum.photos/200?random=$randomNumber+1", "Item 2"),
        Item("Item 3", "https://picsum.photos/200?random=$randomNumber+2", "Item 3"),
        Item("Item 4", "https://picsum.photos/200?random=$randomNumber+3", "Item 4"),
        Item("Item 5", "https://picsum.photos/200?random=$randomNumber+4", "Item 5"))*/

    var GroupsList = arrayListOf<Group>(
        Group("Item 1", "Item 1"),
        Group("Item 2", "Item 2")
    )

    /*var tmp = arrayListOf<Exercise>(
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+5", Date(), "JUST DO IT", "Item 1"),
        Exercise("Интервальный бег", "https://picsum.photos/200?random=$randomNumber+6", Date(), "Kirby the world eater", "Item 2"),
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+7", Date(), "Kept you waiting, huh?", "Item 3"))
    var tmp1 = arrayListOf<Exercise>(
        Exercise("Интервальный бег", "https://picsum.photos/200?random=$randomNumber+8", Date(), "Keep on keeping on", "Item 1"),
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+9", Date(), "DO IT", "Item 2"),
        Exercise("Интервальный бег", "https://picsum.photos/200?random=$randomNumber+10", Date(), "HAPATA", "Item 3")
    )
    var exerciseList = ArrayMap<String, MutableList<Exercise>>().apply{
        put("Item 1", tmp)
        put("Item 2", tmp1)
    }*/
    lateinit var preferences: SharedPreferences
    lateinit var sportsmensList: MutableList<Item>
    lateinit var exerciseList: ArrayMap<String, MutableList<Exercise>>
    lateinit var profileList: ArrayMap<String, String>


    val user = "C" //Тип пользователя, в дальнейшем будет считываться с firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        val sportsmensListJson = (preferences.getString("sportsmensList", null))
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
        val isLoggedIn = preferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            /*val editor = preferences.edit()
            editor.putString("exerciseList", Gson().toJson(exerciseList))
            editor.putString("sportsmensList", Gson().toJson(sportsmensList))
            editor.apply()*/
            if (user == "C") {
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
                loadFragment(Exercisefragment())
                navView?.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.exercise -> loadFragment(Exercisefragment())
                        R.id.diary -> loadFragment(DiaryFragment())
                        R.id.profile -> loadFragment(ProfileFragment())
                        else -> {

                        }
                    }
                    true
                }
            }

        }
    }
        override fun onBackPressed() {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_sportsmens)
            Log.d("MainActivity", "currentFragment: $currentFragment")
            if (user == "C" && R.id.sportsmens != navView.selectedItemId) {
                navView.selectedItemId = R.id.sportsmens
                loadFragment(SportsmensFragment())
            } else if (currentFragment is SportsmensFragmentDialog) {
                // Handle back button press inside the dialog fragment
                super.onBackPressed()
                setNavViewVisibility(true)
            } else if (user == "S" && R.id.exercise != navView.selectedItemId) {
                navView.selectedItemId = R.id.exercise
                loadFragment(Exercisefragment())
            } else {
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