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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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

    var sportsmensList1 = arrayListOf<Item>()

    var tmp = arrayListOf<Exercise>(

        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+5", Date().toString(), "JUST DO IT", "p", "", "Item 1"),
        Exercise("Бег", "https://picsum.photos/200?random=$randomNumber+6", Date().toString(), "Kirby the world eater", "p", "", "Item 2"),
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+7", Date().toString(), "Kept you waiting, huh?", "p", "", "Item 3"))
    var tmp1 = arrayListOf<Exercise>(
        Exercise("Бег", "https://picsum.photos/200?random=$randomNumber+8", Date().toString(), "Keep on keeping on", "p", "", "Item 1"),
        Exercise("Плавание", "https://picsum.photos/200?random=$randomNumber+9", Date().toString(), "DO IT", "p", "", "Item 2"),
        Exercise("Бег", "https://picsum.photos/200?random=$randomNumber+10", Date().toString(), "HAPATA", "p", "", "Item 3")
    )

    lateinit var preferences: SharedPreferences
    lateinit var sportsmensList: MutableList<Item>
    lateinit var exerciseList: ArrayMap<String, MutableList<Exercise>>
    var exerciseList1 = ArrayMap<String, MutableList<Exercise>>()
    lateinit var profileList: ArrayMap<String, String>
    lateinit var user: String

    var statemap = ArrayMap<String, String>().apply {
        put("p", "Запланирована")
        put("c","Выполнена")
        put("h", "Выполнена частично")
        put("f", "Не выполнена")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)

        user = ""
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
            val editor = preferences.edit()
            editor.putString("exerciseList", Gson().toJson(exerciseList))
            editor.putString("sportsmensList", Gson().toJson(sportsmensList))
            Log.d("check", sportsmensList.toString())
            editor.apply()
            val currentUser = Firebase.auth.currentUser
            lateinit var email:String
            var role = ""
            currentUser?.let {
                email = it.email.toString()
            }
            database = Firebase.database.reference
            database = database.child("users")
            Log.d("User",email.split("@")[0])
            database.child(email.split("@")[0]).get().addOnSuccessListener {
                if (it.exists()){
                    role = it.child("role").value.toString()
                    Log.d("User",role)
                    user = role
                    if (user == "C") {
                        val reference = database.child(email.split("@")[0]).child("list_of_sportsmen")
                        reference.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("child1", task.toString())
                                val children = task.result!!.children
                                var totalChildren = children.count()
                                var completedChildren = 0
                                for (it in task.result!!.children) {
                                    val random = Random()
                                    val randomNumber = random.nextInt(1000)
                                    Log.d("child1", completedChildren.toString())
                                        val value = it.getValue()
                                        val item = Item(
                                            value.toString().split(",")[2].split("=")[1].substring(0,
                                                value.toString().split(",")[2].split("=")[1].length-1),//Супер тупой костыль без которого крашимся ¯\_(ツ)_/¯
                                            "https://picsum.photos/200?random=$randomNumber",
                                            value.toString().split(",")[0].split("=")[1]
                                        )
                                        sportsmensList1.add(item)
                                        completedChildren++
                                    }
                                if (completedChildren == totalChildren) {
                                    Log.d("child2", completedChildren.toString())
                                    editor.putString(
                                        "sportsmensList",
                                        Gson().toJson(sportsmensList1)
                                    )
                                    editor.apply()
                                    val sportsmensListJson = (preferences.getString("sportsmensList", null))
                                    sportsmensList = sportsmensListJson?.let {
                                        Gson().fromJson(it, object : TypeToken<ArrayList<Item>>() {}.type)
                                    } ?: arrayListOf()
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
                                }

                            }
                            else {
                                task.exception!!.message?.let { it1 -> Log.d("TAG", it1) } // Never ignore potential errors!
                            }
                        }
                    } else if (user == "S") {
                        val reference = Firebase.database.reference.child("Exercise").child(email.split("@")[0])
                        reference.get().addOnCompleteListener { task ->
                            val tempList = arrayListOf<Exercise>()
                            if (task.isSuccessful) {
                                Log.d("child1", task.toString())
                                val children = task.result!!.children
                                var totalChildren = children.count()
                                var completedChildren = 0
                                for (it in task.result!!.children) {
                                    val random = Random()
                                    val randomNumber = random.nextInt(1000)
                                    Log.d("child1", completedChildren.toString())
                                    val img = it.child("img").getValue().toString()
                                    val itemComm = it.child("itemComm").getValue().toString()
                                    val itemDate = it.child("itemDate").getValue().toString()
                                    val itemDesc = it.child("itemDesc").getValue().toString()
                                    val itemId = it.child("itemId").getValue().toString()
                                    val itemState = it.child("itemState").getValue().toString()
                                    val text = it.child("text").getValue().toString()
                                    val item = Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
                                    tempList.add(item)
                                    //exerciseList1.put( email.split("@")[0],item)
                                    completedChildren++
                                }
                                if (completedChildren == totalChildren) {
                                    exerciseList1.put( email.split("@")[0],tempList)
                                    Log.d("child2", completedChildren.toString())
                                    editor.putString(
                                        "exerciseList",
                                        Gson().toJson(exerciseList1)
                                    )
                                    editor.apply()
                                    val exerciseListJson = (preferences.getString("exerciseList", null))
                                    exerciseList =  exerciseListJson?.let {
                                        Gson().fromJson<ArrayMap<String, MutableList<Exercise>>>(it, object : TypeToken<ArrayMap<String, MutableList<Exercise>>>() {}.type)
                                    } ?: ArrayMap()
                                }
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
                        }
                    }

                }else{
                    Log.d("F","User does not exist")
                }
            }.addOnFailureListener{
                Log.d("F","Failed fairbase")
            }
            print(role)
            Log.d("User",user)
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