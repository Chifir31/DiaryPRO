package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
<<<<<<< Updated upstream
=======
import com.example.myapplication.data.Exercise
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
>>>>>>> Stashed changes
import com.example.myapplication.navigation_pages.GroupsFragment
import com.example.myapplication.navigation_pages.ProfileFragment
import com.example.myapplication.navigation_pages.SportsmensFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
<<<<<<< Updated upstream

class MainActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< Updated upstream
        setContentView(R.layout.c_activity_main)
        val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
        loadFragment(SportsmensFragment())
        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.sportsmens->loadFragment(SportsmensFragment())
                R.id.groups-> loadFragment(GroupsFragment())
                R.id.profile->loadFragment(ProfileFragment())
                else -> {
=======
        val user = "S" //Тип пользователя, в дальнейшем будет считываться с firebase
=======
import java.util.*
import kotlin.collections.ArrayList

class MainActivity: AppCompatActivity()  {
    val random = Random()
    val randomNumber = random.nextInt(1000)
    var sportsmensList = arrayListOf<Item>(
        Item("Item 1", "https://picsum.photos/200?random=$randomNumber", "Item 1"),
        Item("Item 2", "https://picsum.photos/200?random=$randomNumber+1", "Item 2"),
        Item("Item 3", "https://picsum.photos/200?random=$randomNumber+2", "Item 3"),
        Item("Item 4", "https://picsum.photos/200?random=$randomNumber+3", "Item 4"),
        Item("Item 5", "https://picsum.photos/200?random=$randomNumber+4", "Item 5"))

    var GroupsList = arrayListOf<Group>(
        Group("Item 1","Item 1")
    )

    var tmp = arrayListOf<Exercise>(
        Exercise("Push-ups", "https://picsum.photos/200?random=$randomNumber+5", Date(), "Item 1"),
        Exercise("Squats", "https://picsum.photos/200?random=$randomNumber+6", Date(), "Item 2"),
        Exercise("Lunges", "https://picsum.photos/200?random=$randomNumber+7", Date(), "Item 3"))
    var tmp1 = arrayListOf<Exercise>(
        Exercise("Bench press", "https://picsum.photos/200?random=$randomNumber+8", Date(), "Item 1"),
        Exercise("Deadlifts", "https://picsum.photos/200?random=$randomNumber+9", Date(), "Item 2"),
        Exercise("Rows", "https://picsum.photos/200?random=$randomNumber+10", Date(), "Item 3")
    )
    var exerciseList = ArrayMap<String, MutableList<Exercise>>().apply{
        put("Item 1", tmp)
        put("Item 2", tmp1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = "С" //Тип пользователя, в дальнейшем будет считываться с firebase
>>>>>>> Stashed changes
        if (user == "C"){
            setContentView(R.layout.c_activity_main)
            val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
            loadFragment(SportsmensFragment())
            navView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.sportsmens->loadFragment(SportsmensFragment())
                    R.id.groups-> loadFragment(GroupsFragment())
                    R.id.profile->loadFragment(ProfileFragment())
                    else -> {
>>>>>>> Stashed changes

                }
            }
            true
        }

    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}