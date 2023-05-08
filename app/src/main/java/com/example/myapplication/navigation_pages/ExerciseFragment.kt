package com.example.myapplication.navigation_pages

import AdapterExercise
import android.os.Bundle
import android.util.ArrayMap
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
    private lateinit var stateList: ArrayMap<Char, String>

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

        val random = Random()
        val randomNumber = random.nextInt(1000)
        var date = Date()
        itemList = (requireActivity() as MainActivity).exerciseList
        stateList = (requireActivity() as MainActivity).statemap
        itemList1 = (itemList["Item 1"]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = it.itemDate
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
}
