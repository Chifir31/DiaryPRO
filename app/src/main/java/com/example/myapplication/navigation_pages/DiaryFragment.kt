package com.example.myapplication.navigation_pages

import AdapterExercise
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment() {
    private lateinit var calendarView: CalendarView
    private lateinit var adapter: AdapterExercise
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private lateinit var stateList: ArrayMap<Char, String>
    private lateinit var date: TextView
    private var selectedDate: Date = Date()
    private var param2="Item 1"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        itemList = (requireActivity() as MainActivity).exerciseList
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = layoutManager
        val date = Date()
        adapter = AdapterExercise(itemList[param2]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = it.itemDate
            calendar.get(Calendar.DAY_OF_MONTH) == date.date &&
                    calendar.get(Calendar.MONTH) == date.month &&
                    calendar.get(Calendar.YEAR) == date.year + 1900
        } as MutableList<Exercise>)
        recyclerView.adapter = adapter
        stateList = (requireActivity() as MainActivity).statemap
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        calendarView = view.findViewById(R.id.CalendarView )
        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        setupListeners()


        // Calculate the start and end times for the current week
        val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - 2
        val weekStart = currentTimeMillis - daysFromMonday * 24 * 60 * 60 * 1000
        val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            val calendar = Calendar.getInstance()
            calendar.time = itemList[param2]?.get(0)?.itemDate ?: Date()
            Log.d("Dates", selectedDate.toString() + " " +selectedDate.date.toString() +" "+  selectedDate.month.toString() +" "+selectedDate.year.toString() + " a " + itemList[param2]?.get(0)?.itemDate?.toString() +" " +calendar.get(Calendar.DAY_OF_MONTH)+ " " +calendar.get(Calendar.MONTH) + " " +calendar.get(Calendar.YEAR))
            Log.d("list", (itemList[param2]?.filter {
                calendar.time = it.itemDate
                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                        calendar.get(Calendar.MONTH) == selectedDate.month /*&&
                        calendar.get(Calendar.YEAR) == selectedDate.year*/ } as MutableList<Exercise>?).toString())
            adapter = AdapterExercise(itemList[param2]?.filter {
                val calendar = Calendar.getInstance()
                calendar.time = it.itemDate
                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                        calendar.get(Calendar.MONTH) == selectedDate.month &&
                        calendar.get(Calendar.YEAR) == selectedDate.year+1900
            } as MutableList<Exercise>?)
            adapter.notifyDataSetChanged()
            recyclerView.adapter = adapter
            setupListeners()

        }
        // Set the minimum and maximum dates for the calendar view to show only the current week
        calendarView.minDate = weekStart
        calendarView.maxDate = weekEnd
    }

    fun EditWindow(position: Int){
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.sportsmens_dialog_input, null)
        val type = dialogLayout.findViewById<Spinner>(R.id.type_edit)
        val sportsmen = dialogLayout.findViewById<TextView>(R.id.sportsmen_edit)
        val toolbar_text = dialogLayout.findViewById<TextView>(R.id.toolbar_text)
        val edit_btn = dialogLayout.findViewById<ImageButton>(R.id.edit_button)
        val plan = dialogLayout.findViewById<EditText>(R.id.plan_edit)
        var dateSelected = selectedDate
        val status = dialogLayout.findViewById<TextView>(R.id.status_edit)

        toolbar_text.setText("Тренировка")
        date = dialogLayout.findViewById<TextView>(R.id.date_edit)
        val array = arrayOf("Плавание", "Бег", "Езда на велосипеде", "Лыжи", "ОФП")
        val adapterspinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)
        // Set the adapter for the Spinner
        fun state(boolean: Boolean){
            type.isEnabled=boolean
            plan.isFocusable=boolean
            plan.isFocusableInTouchMode=boolean
            date.isEnabled=boolean
        }
        state(false)
        type.adapter = adapterspinner
        sportsmen.text = param2.toString()
        val calendar = Calendar.getInstance()
        val year = calendar.get(android.icu.util.Calendar.YEAR)
        val month = calendar.get(android.icu.util.Calendar.MONTH)
        val day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
        date.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        val minuteInterval = (minute + 7) / 15 * 15
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minuteInterval)
                        dateSelected = calendar.time
                        date.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(dateSelected!!)
                        date.setError(null)
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true)
                    tpd.show()
                    date.setError(null)
                },
                year,
                month,
                day
            )
            dpd.datePicker.minDate = calendar.timeInMillis
            dpd.show()
        }
        var tmp = (itemList[param2]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = it.itemDate
            calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                    calendar.get(Calendar.MONTH) == selectedDate.month &&
                    calendar.get(Calendar.YEAR) == selectedDate.year+1900
        } as MutableList<Exercise>?)
        var tmp1 = tmp?.get(position)?.itemDate
        date.setText(
            tmp1?.date.toString() + '.' + tmp1?.month.toString() + '.' + (tmp1?.year?.plus(
                1900
            )).toString()+ ' ' + tmp1?.hours.toString() + ':' + tmp1?.minutes.toString()
        )
        status.setText(stateList[tmp?.get(position)?.itemState])
        Log.d("item", tmp.toString())
        type.setSelection(adapterspinner.getPosition(tmp?.get(position)?.text))
        Log.d("ItemDesc", tmp?.get(position)?.itemDesc.toString())
        plan.setText(tmp?.get(position)?.itemDesc.toString())
        with(builder) {
            setPositiveButton("Сохранить") { dialog, which ->
                val random = Random()
                val randomNumber = random.nextInt(1000)
                tmp?.get(position)?.text = type.selectedItem.toString()
                tmp?.get(position)?.img = "https://picsum.photos/200?random=$randomNumber"
                tmp?.get(position)?.itemDate = dateSelected
                tmp?.get(position)?.itemDesc = plan.text.toString()
                //tmp?.get(position)?.itemId = "Item " + (size++).toString()
                //(type.selectedItem.toString(), "https://picsum.photos/200?random=$randomNumber", dateSelected, plan.toString(), "Item "+(size++).toString())
                //itemList[param2]?.add(Exercise(type.selectedItem.toString(), "https://picsum.photos/200?random=$randomNumber", dateSelected, plan.toString(), "Item "+(size++).toString()))
                adapter = AdapterExercise(itemList[param2]?.filter {
                    val calendar = Calendar.getInstance()
                    calendar.time = it.itemDate
                    calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                            calendar.get(Calendar.MONTH) == selectedDate.month &&
                            calendar.get(Calendar.YEAR) == selectedDate.year + 1900
                } as MutableList<Exercise>?)
                adapter.notifyItemInserted(itemList[param2]?.size ?: 0)
                recyclerView.adapter = adapter
                setupListeners()
                //editor.putString("exerciseList", Gson().toJson(itemList))
                //editor.apply()
                Log.d("SportsmensFragment size", adapter.itemCount.toString())
                Log.d("SportsmensFragment elements", "Item list: $itemList")
                edit_btn.visibility= View.VISIBLE
                state(false)
            }//Log.d("Main","Positive")}
            setNegativeButton("Отмена") { dialog, which -> Log.d("Main", "Negative")
                edit_btn.visibility= View.VISIBLE
                state(false)}
        }
        builder.setView(dialogLayout)
        builder.show()
    }
    fun setupListeners(){
        adapter.setOnOpenClickListener(object : AdapterExercise.OnOpenClickListener {
            override fun onOpenClick(position: Int) {
                EditWindow(position)
            }
        })
    }
}

