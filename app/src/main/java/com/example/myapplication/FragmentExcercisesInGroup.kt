package com.example.myapplication

import AdapterExercise
import MyAdapter
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Exercise
import com.example.myapplication.data.Item
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentExcercisesInGroup : Fragment() {
    private lateinit var adapter: AdapterExercise
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private lateinit var addBtn: ImageButton
    private lateinit var calendarView: CalendarView
    private var param1: String? = null
    private var param2: String? = null
    private var size: Int = 0
    private lateinit var groupName: TextView
    private var selectedDate: Date = Date()
    private lateinit var addExcerciseBtn: TextView
    private lateinit var date: TextView

    private fun ShowInput() {
        addExcerciseBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
            val inflater = requireActivity().layoutInflater
            val dialogLayout = inflater.inflate(R.layout.sportsmens_dialog_input, null)
            val type = dialogLayout.findViewById<Spinner>(R.id.type_edit)
            val sportsmen = dialogLayout.findViewById<TextView>(R.id.sportsmen_edit)
            date = dialogLayout.findViewById<TextView>(R.id.date_edit)
            val plan = dialogLayout.findViewById<EditText>(R.id.plan_edit)
            val array = arrayOf("Плавание", "Интервальный бег")
            val adapterspinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)

            // Set the adapter for the Spinner
            type.adapter = adapterspinner
            sportsmen.text=param1.toString()
            val calendar = Calendar.getInstance()
            val year = calendar.get(android.icu.util.Calendar.YEAR)
            val month = calendar.get(android.icu.util.Calendar.MONTH)
            val day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
            var dateSelected = Date()
            date.setOnClickListener{
                val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    dateSelected = calendar.time
                    date.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(dateSelected!!)
                    date.setError(null)
                }, year, month, day)
                dpd.show()
            }
            with(builder){
                setTitle("Добавление тренировки")
                setPositiveButton("Добавить"){dialog, which->
                    val random = Random()
                    val randomNumber = random.nextInt(1000)
                    itemList[param2]?.add(Exercise(type.selectedItem.toString(), "https://picsum.photos/200?random=$randomNumber", dateSelected, "Item "+(size++).toString()))
                    adapter.notifyItemInserted(itemList[param2]?.size ?: 0)
                    Log.d("SportsmensFragment size", adapter.itemCount.toString())
                    Log.d("SportsmensFragment elements", "Item list: $itemList")
                }//Log.d("Main","Positive")}
                setNegativeButton("Отмена"){dialog, which-> Log.d("Main","Negative")}
            }
            builder.setView(dialogLayout)
            builder.show()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises_in_group, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemList = (requireActivity() as MainActivity).exerciseList

        calendarView = view.findViewById(R.id.CalendarView)
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

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            val calendar = Calendar.getInstance()
            calendar.time = itemList[param2]?.get(0)?.itemDate
            Log.d("Dates", selectedDate.toString()+" " +selectedDate.date.toString() +" "+  selectedDate.month.toString() +" "+selectedDate.year.toString() + " a " + itemList[param2]?.get(0)?.itemDate?.toString() +" " +calendar.get(Calendar.DAY_OF_MONTH)+ " " +calendar.get(Calendar.MONTH) + " " +calendar.get(Calendar.YEAR))
            Log.d("list", (itemList[param2]?.filter {
                calendar.time = it.itemDate
                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                        calendar.get(Calendar.MONTH) == selectedDate.month /*&&
                        calendar.get(Calendar.YEAR) == selectedDate.year*/ } as MutableList<Exercise>?).toString())
            adapter = AdapterExercise(itemList[param2]?.filter {
                val calendar = Calendar.getInstance()
                calendar.time = it.itemDate
                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                        calendar.get(Calendar.MONTH) == selectedDate.month/* &&
                    calendar.get(Calendar.YEAR) == selectedDate.year*/
            } as MutableList<Exercise>?)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        addExcerciseBtn = view.findViewById(R.id.add_btn)
        ShowInput()

        groupName = view.findViewById(R.id.groupName_label)
        groupName.text = param1

        val random = Random()
        val randomNumber = random.nextInt(1000)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.exerciseList)
        recyclerView.layoutManager = layoutManager
        adapter = AdapterExercise(itemList[param2])

        recyclerView.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FragmentExcercisesInGroup.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentExcercisesInGroup().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2,param2)
                }
            }
    }

}