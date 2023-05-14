package com.example.myapplication.navigation_pages

import AdapterExercise
import android.os.Bundle
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment() {
    private lateinit var dateTextView: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var adapter: AdapterExercise
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private  lateinit var  database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTextView = view.findViewById(R.id.date_textview)

        val currentUser = Firebase.auth.currentUser
        lateinit var email:String
        currentUser?.let {
            email = it.email.toString()
        }
        var tempList = ArrayMap<String, MutableList<Exercise>>()
        database = Firebase.database.reference
        database.child("Exercise").child(email.split("@")[0]).get().addOnSuccessListener {
            if(it.exists()){
                val children = it.children
                children.forEach{
                    val img = it.child("img").getValue().toString()
                    val itemComm = it.child("itemComm").getValue().toString()
                    val itemDate = it.child("itemDate").getValue().toString()
                    val itemDesc = it.child("itemDesc").getValue().toString()
                    val itemId = it.child("itemId").getValue().toString()
                    val itemState = it.child("itemState").getValue().toString()
                    val text = it.child("text").getValue().toString()

                    if(tempList[email.split("@")[0]]==null){
                        tempList.apply {
                            put(
                                email.split("@")[0],
                                mutableListOf(
                                    Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
                                )
                            )

                        }
                    }else{
                        tempList[email.split("@")[0]]?.add(
                            Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
                        )
                    }
                }
            }else{
                tempList = ArrayMap<String, MutableList<Exercise>>()
            }
            itemList = tempList
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.list)
            recyclerView.layoutManager = layoutManager
            adapter = AdapterExercise(itemList[email.split("@")[0]])
            recyclerView.adapter = adapter

            val currentDate = Date()
            val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(currentDate)

            calendarView = view.findViewById(R.id.CalendarView )
            // Get the current time in milliseconds
            val currentTimeMillis = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)



            // Calculate the start and end times for the current week
            val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - 2
            val weekStart = currentTimeMillis - daysFromMonday * 24 * 60 * 60 * 1000
            val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000

            // Set the minimum and maximum dates for the calendar view to show only the current week
            calendarView.minDate = weekStart
            calendarView.maxDate = weekEnd

        }

        /*itemList = tempList
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = layoutManager
        adapter = AdapterExercise(itemList[email.split("@")[0]])
        recyclerView.adapter = adapter

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate)

        calendarView = view.findViewById(R.id.CalendarView )
        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)



        // Calculate the start and end times for the current week
        val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - 2
        val weekStart = currentTimeMillis - daysFromMonday * 24 * 60 * 60 * 1000
        val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000

        // Set the minimum and maximum dates for the calendar view to show only the current week
        calendarView.minDate = weekStart
        calendarView.maxDate = weekEnd*/
    }
}

