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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.AdapterCalendar
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.myapplication.databinding.FragmentSportsmensDialogBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment(),  AdapterCalendar.Listener{
    private lateinit var adapter: AdapterExercise
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private lateinit var itemList1: MutableList<Exercise>
    private  lateinit var  database: DatabaseReference
    private lateinit var stateList: ArrayMap<String, String>
    private lateinit var date: TextView
    private var selectedDate: Date = Date()
    private var param2="Item 1"

    lateinit var binding: FragmentSportsmensDialogBinding
    private lateinit var layoutManager_calendar: RecyclerView.LayoutManager
    private lateinit var calendarView: RecyclerView
    private var adapter_calendar = AdapterCalendar(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSportsmensDialogBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }
        var tempList = ArrayMap<String, MutableList<Exercise>>()
        database = Firebase.database.reference
        database.child("Exercise").child(email.split("@")[0]).get().addOnSuccessListener {
            if (it.exists()) {
                val children = it.children
                children.forEach {
                    val img = it.child("img").getValue().toString()
                    val itemComm = it.child("itemComm").getValue().toString()
                    val itemDate = it.child("itemDate").getValue().toString()
                    val itemDesc = it.child("itemDesc").getValue().toString()
                    val itemId = it.child("itemId").getValue().toString()
                    val itemState = it.child("itemState").getValue().toString()
                    val text = it.child("text").getValue().toString()

                    if (tempList[email.split("@")[0]] == null) {
                        tempList.apply {
                            put(
                                email.split("@")[0],
                                mutableListOf(
                                    Exercise(
                                        text,
                                        img,
                                        itemDate,
                                        itemDesc,
                                        itemState,
                                        itemComm,
                                        itemId
                                    )
                                )
                            )

                        }
                    } else {
                        tempList[email.split("@")[0]]?.add(
                            Exercise(text, img, itemDate, itemDesc, itemState, itemComm, itemId)
                        )
                    }
                }
            } else {
                tempList = ArrayMap<String, MutableList<Exercise>>()
            }
            itemList = tempList

            Log.d("Eee",itemList.size.toString())
            recyclerView = view.findViewById(R.id.list)
            if(itemList[email.split("@")[0]]!=null){
            itemList1 = (itemList[email.split("@")[0]]?.filter {
                val calendar = Calendar.getInstance()
                calendar.time = Date(it.itemDate.toLong())
                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                        calendar.get(Calendar.MONTH) == selectedDate.month &&
                        calendar.get(Calendar.YEAR) == selectedDate.year + 1900
            } as MutableList<Exercise>)
            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            adapter = AdapterExercise(itemList1)
            recyclerView.adapter = adapter
            stateList = (requireActivity() as MainActivity).statemap
            setupListeners()}
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            //dateTextView.text = dateFormat.format(currentDate)

            calendarView = view.findViewById(R.id.CalendarView)
            initCalendar()
            // Get the current time in milliseconds
            val currentTimeMillis = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)


            // Calculate the start and end times for the current week
            val daysFromMonday =
                if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - 2
            val weekStart = currentTimeMillis - daysFromMonday * 24 * 60 * 60 * 1000
            val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000

            // Set the minimum and maximum dates for the calendar view to show only the current week
            //calendarView.minDate = weekStart
            //calendarView.maxDate = weekEnd

        }
    }

    /*itemList = tempList
    val layoutManager = LinearLayoutManager(context)
    recyclerView = view.findViewById(R.id.list)
    recyclerView.layoutManager = layoutManager
    val date = Date()

    calendarView = view.findViewById(R.id.CalendarView)
    initCalendar()


    adapter = AdapterExercise(itemList[email.split("@")[0]]).filter{
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
//        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            selectedDate = Calendar.getInstance().apply {
//                set(year, month, dayOfMonth)
//            }.time
//            val calendar = Calendar.getInstance()
//            calendar.time = itemList[param2]?.get(0)?.itemDate ?: Date()
//            Log.d("Dates", selectedDate.toString() + " " +selectedDate.date.toString() +" "+  selectedDate.month.toString() +" "+selectedDate.year.toString() + " a " + itemList[param2]?.get(0)?.itemDate?.toString() +" " +calendar.get(Calendar.DAY_OF_MONTH)+ " " +calendar.get(Calendar.MONTH) + " " +calendar.get(Calendar.YEAR))
//            Log.d("list", (itemList[param2]?.filter {
//                calendar.time = it.itemDate
//                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
//                        calendar.get(Calendar.MONTH) == selectedDate.month /*&&
//                        calendar.get(Calendar.YEAR) == selectedDate.year*/ } as MutableList<Exercise>?).toString())
//            adapter = AdapterExercise(itemList[param2]?.filter {
//                val calendar = Calendar.getInstance()
//                calendar.time = it.itemDate
//                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
//                        calendar.get(Calendar.MONTH) == selectedDate.month &&
//                        calendar.get(Calendar.YEAR) == selectedDate.year+1900
//            } as MutableList<Exercise>?)
//            adapter.notifyDataSetChanged()
//            recyclerView.adapter = adapter
//            setupListeners()
//
//        }
//        // Set the minimum and maximum dates for the calendar view to show only the current week
//        calendarView.minDate = weekStart
//        calendarView.maxDate = weekEnd
}
*/
    fun EditWindow(position: Int){
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.fragment_diary_dialog, null)
        val type = dialogLayout.findViewById<Spinner>(R.id.type_edit)
        //val sportsmen = dialogLayout.findViewById<TextView>(R.id.sportsmen_edit)
        val toolbar_text = dialogLayout.findViewById<TextView>(R.id.toolbar_text)
        val edit_btn = dialogLayout.findViewById<ImageButton>(R.id.edit_button)
        val plan = dialogLayout.findViewById<TextView>(R.id.plan_edit)
        val state = dialogLayout.findViewById<TextView>(R.id.state_edit)
        var dateSelected = selectedDate
        //val status = dialogLayout.findViewById<TextView>(R.id.status_edit)

        toolbar_text.setText("Тренировка")
        date = dialogLayout.findViewById(R.id.date_edit)
        val array = arrayOf("Плавание", "Бег", "Велосипед", "Лыжи", "ОФП")
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
        //sportsmen.text = param2.toString()
        val calendar = Calendar.getInstance()
        val year = calendar.get(android.icu.util.Calendar.YEAR)
        val month = calendar.get(android.icu.util.Calendar.MONTH)
        val day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
        date.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    dateSelected = calendar.time
                    date.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(dateSelected!!)
                    date.setError(null)
                },
                year,
                month,
                day
            )
            dpd.datePicker.minDate = calendar.timeInMillis
            dpd.show()
        }
        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }
        var tmp = (itemList[email.split("@")[0]]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it.itemDate.toLong())
            calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                    calendar.get(Calendar.MONTH) == selectedDate.month &&
                    calendar.get(Calendar.YEAR) == selectedDate.year+1900
        } as MutableList<Exercise>?)
        var tmp1 = Date(tmp?.get(position)?.itemDate?.toLong()!!)
        date.setText(
            tmp1?.date.toString() + '.' + tmp1?.month.toString() + '.' + (tmp1?.year?.plus(
                1900
            )).toString()
        )
            Log.d("check", tmp?.get(position)?.itemState.toString())
            Log.d("check", stateList[tmp?.get(position)?.itemState].toString())
        state.setText(stateList[tmp?.get(position)?.itemState])
        //status.setText(stateList[tmp?.get(position)?.itemState])
        Log.d("item", tmp.toString())
        type.setSelection(adapterspinner.getPosition(tmp?.get(position)?.text))
        Log.d("ItemDesc", tmp?.get(position)?.itemDesc.toString())
        plan.setText(tmp?.get(position)?.itemDesc.toString())
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

    private fun initCalendar() {

        val dateFormat = SimpleDateFormat("d MMM yyyy, EE", Locale("ru"))
        val date = dateFormat.format(Date())

        val fullDate = view?.findViewById<TextView>(R.id.full_date)
        if (fullDate != null) {
            fullDate.text = date
        }
        calendarView.layoutManager =
            GridLayoutManager(requireActivity(), 7 )
        calendarView.adapter = adapter_calendar
        adapter_calendar.fillWeekList(Calendar.getInstance(), allExercise = itemList, itGroup = false)

        val currentWeek = view?.findViewById<TextView>(R.id.current_week)
        view?.findViewById<ImageButton>(R.id.prevWeek)
            ?.setOnClickListener { adapter_calendar.previousWeekAction()
                if (currentWeek != null) {
                    currentWeek.text = adapter_calendar.setFirstLastDaysOfWeek()
                }}
        view?.findViewById<ImageButton>(R.id.nextWeek)
            ?.setOnClickListener { adapter_calendar.nextWeekAction()
                if (currentWeek != null) {
                    currentWeek.text = adapter_calendar.setFirstLastDaysOfWeek()
                }}
        if (currentWeek != null) {
            currentWeek.text = adapter_calendar.setFirstLastDaysOfWeek()
        }
    }

    override fun onClick(day: Date) {
        val fullDate = view?.findViewById<TextView>(R.id.full_date)
        val dateStr = SimpleDateFormat("d MMM yyyy, EE", Locale("ru")).format(day)
        if (fullDate != null) {
            fullDate.text = dateStr
        }

        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }
        Log.d("Day", day.date.toString() + " " +day.month.toString() + " " + day.year.toString()+1900)
        adapter = AdapterExercise(itemList[email.split("@")[0]]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it.itemDate.toLong())
            calendar.get(Calendar.DAY_OF_MONTH) == day.date &&
                    calendar.get(Calendar.MONTH) == day.month &&
                    calendar.get(Calendar.YEAR) == day.year+1900
        } as MutableList<Exercise>?)
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
        selectedDate=day
        setupListeners()
    }
}
