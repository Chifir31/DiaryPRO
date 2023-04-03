package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.navigation_pages.GroupsFragment
import com.example.myapplication.navigation_pages.ProfileFragment
import com.example.myapplication.navigation_pages.Exercisefragment
import com.example.myapplication.navigation_pages.DiaryFragment
import com.example.myapplication.navigation_pages.SportsmensFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = "C" //Тип пользователя, в дальнейшем будет считываться с firebase
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

                    }
                }
                true
            }
        }else if (user == "S"){
            setContentView(R.layout.s_activity_main)
            val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
            loadFragment(Exercisefragment())
            navView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.exercise->loadFragment(Exercisefragment())
                    R.id.diary-> loadFragment(DiaryFragment())
                    R.id.profile->loadFragment(ProfileFragment())
                    else -> {

                    }
                }
                true
            }
        }

    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}