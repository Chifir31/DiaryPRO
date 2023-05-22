package com.example.myapplication.navigation_pages

import AdapterExercise
import android.content.SharedPreferences
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.example.myapplication.data.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var preferences: SharedPreferences
    lateinit var editor :  SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences =
            requireContext().getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        editor = preferences.edit()
        dateTextView = view.findViewById(R.id.date_textview)
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        //itemList = (requireActivity() as MainActivity).exerciseList
        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }

//        var tempList = ArrayMap<String, MutableList<Exercise>>()
        database = Firebase.database.reference
        Log.d("kill me2", email.split("@")[0])
//        database.child("Exercise").child(email.split("@")[0]).get().addOnSuccessListener {
//            if(it.exists()){
//                val children = it.children
//                children.forEach{
//                    val img = it.child("img").getValue().toString()
//                    val itemComm = it.child("itemComm").getValue().toString()
//                    val itemDate = it.child("itemDate").getValue().toString()
//                    val itemDesc = it.child("itemDesc").getValue().toString()
//                    val itemId = it.child("itemId").getValue().toString()
//                    val itemState = it.child("itemState").getValue().toString()
//                    val text = it.child("text").getValue().toString()
//                    Log.d("null",text)
//
//                    Log.d("kill me2",itemDate)
//                    if(tempList[email.split("@")[0]]==null){
//                        tempList.apply {
//                            put(
//                                email.split("@")[0],
//                                mutableListOf(
//                                    Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
//                                )
//                            )
//
//                        }
//
//                    }else{
//                        Log.d("kill me4","check fun")
//                        tempList[email.split("@")[0]]?.add(
//                            Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
//                        )
//                    }
//                }
//
//
//            }else{
//                tempList[email.split("@")[0]]?.add(
//                    Exercise("text","0","0","0","0","0","0")
//                )
//            }
        itemList = (requireActivity() as MainActivity).exerciseList
        Log.d("size1", itemList.size.toString())

        Log.d("kill me", itemList.size.toString())

        val random = Random()
        val randomNumber = random.nextInt(1000)
        var date = Date()
        //itemList = tempList
        Log.d("Eee", itemList.size.toString())
        stateList = (requireActivity() as MainActivity).statemap
        Log.d("email", email.split("@")[0])
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
        setupListeners()
        swipeRefreshLayout.setOnRefreshListener {
            val tempList = arrayListOf<Exercise>()
            // Refresh the data here
            // Call your data update function or fetch new data from the server
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
                        val item =
                            Exercise(text, img, itemDate, itemDesc, itemState, itemComm, itemId)
                        tempList.add(item)
                        //exerciseList1.put( email.split("@")[0],item)
                        completedChildren++
                    }
                    if (completedChildren == totalChildren) {
                        Log.d("itemList", itemList.toString())
                        var exerciseList1 = ArrayMap<String, MutableList<Exercise>>()
                        exerciseList1.put(email.split("@")[0], tempList)
                        Log.d("child2", completedChildren.toString())
                        editor.putString(
                            "exerciseList",
                            Gson().toJson(exerciseList1)
                        )
                        editor.apply()
                        val exerciseListJson = (preferences.getString("exerciseList", null))
                        itemList = exerciseListJson?.let {
                            Gson().fromJson<ArrayMap<String, MutableList<Exercise>>>(
                                it,
                                object :
                                    TypeToken<ArrayMap<String, MutableList<Exercise>>>() {}.type
                            )
                        } ?: ArrayMap()
                        var date = Date()
                        itemList1 = (itemList[email.split("@")[0]]?.filter {
                            val calendar = Calendar.getInstance()
                            calendar.time = Date(it.itemDate.toLong())
                            calendar.get(Calendar.DAY_OF_MONTH) == date.date &&
                                    calendar.get(Calendar.MONTH) == date.month &&
                                    calendar.get(Calendar.YEAR) == date.year + 1900
                        } as MutableList<Exercise>)
                        adapter = AdapterExercise(itemList1)
                        recyclerView.adapter = adapter
                        setupListeners()
                        swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(requireContext(), "Обновлено", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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
    fun setupListeners(){
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
}
