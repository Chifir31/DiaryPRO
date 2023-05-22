package com.example.myapplication.navigation_pages

import AdapterExercise
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExerciseFragment : Fragment() {
    lateinit var dateTextView: TextView
    private lateinit var adapter: AdapterExercise
    private lateinit var add_button: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private lateinit var itemList1: MutableList<Exercise>
    private lateinit var stateList: ArrayMap<String, String>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTextView = view.findViewById(R.id.date_textview)
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate)

        //itemList = (requireActivity() as MainActivity).exerciseList
        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }

        var tempList = ArrayMap<String, MutableList<Exercise>>()
        database = Firebase.database.reference
        Log.d("kill me2",email.split("@")[0])
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

                    Log.d("kill me2",itemDate)
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
                tempList[email.split("@")[0]]?.add(
                    Exercise("text","0","0","0","0","0","0")
                )
            }
            itemList = tempList
            Log.d("size1",itemList.size.toString())

            Log.d("kill me",itemList.size.toString())

            val random = Random()
            val randomNumber = random.nextInt(1000)
            var date = Date()
            //itemList = tempList
            Log.d("Eee",itemList.size.toString())
            stateList = (requireActivity() as MainActivity).statemap
            itemList1 = (itemList[email.split("@")[0]]?.filter {
                val calendar = Calendar.getInstance()
                calendar.time = Date(it.itemDate.toLong())
                calendar.get(Calendar.DAY_OF_MONTH) == date.date &&
                        calendar.get(Calendar.MONTH) == date.month &&
                        calendar.get(Calendar.YEAR) == date.year + 1900
            } as MutableList<Exercise>)
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.list)
            recyclerView.layoutManager = layoutManager
            adapter = AdapterExercise(itemList1)
            recyclerView.adapter = adapter
            adapter.setOnOpenClickListener(object : AdapterExercise.OnOpenClickListener {
                override fun onOpenClick(position: Int) {
                    val exercise = itemList1[position]
                    val fragment = ExerciseFragmentDialog.newInstance(exercise.text, exercise.itemId, position)
                    val fragmentManager = (requireContext() as AppCompatActivity).supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_exercise, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            })
        }


        /*itemList = tempList
        Log.d("size2",itemList.size.toString())
        if(itemList.size == 0){
            itemList.apply {
                put(
                    email.split("@")[0],
                    mutableListOf(
                        Exercise("text","img","0","itemDesc","itemState","itemComm","itemId")
                    )
                )
            }
        }
        Log.d("kill me",itemList.size.toString())

        val random = Random()
        val randomNumber = random.nextInt(1000)
        var date = Date()
        //itemList = tempList
        Log.d("Eee",itemList.size.toString())
        stateList = (requireActivity() as MainActivity).statemap
        itemList1 = (itemList[email.split("@")[0]]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it.itemDate.toLong())
            calendar.get(Calendar.DAY_OF_MONTH) == date.date &&
                    calendar.get(Calendar.MONTH) == date.month &&
                    calendar.get(Calendar.YEAR) == date.year + 1900
        } as MutableList<Exercise>)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = layoutManager
        adapter = AdapterExercise(itemList1)
        recyclerView.adapter = adapter
        adapter.setOnOpenClickListener(object : AdapterExercise.OnOpenClickListener {
            override fun onOpenClick(position: Int) {
                val exercise = itemList1[position]
                val fragment = ExerciseFragmentDialog.newInstance(exercise.text, exercise.itemId, position)
                val fragmentManager = (requireContext() as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_exercise, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })*/
    }
}
