package com.example.myapplication.navigation_pages

import AdapterExercise
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.AdapterCalendar
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.databinding.CalendarCellBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SportsmensFragmentDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class SportsmensFragmentDialog : Fragment(), DatePickerDialog.OnDateSetListener, AdapterCalendar.Listener {
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_text: TextView
    private lateinit var dateTextView: TextView
    private lateinit var add_button: TextView
    private lateinit var edit_button: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterExercise
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private lateinit var stateList: ArrayMap<Char, String>
    private lateinit var date: TextView
    private var selectedDate: Date = Date()
    private var size: Int = 0
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mainActivity: MainActivity
    lateinit var preferences: SharedPreferences
    lateinit var editor :  SharedPreferences.Editor

    lateinit var binding: CalendarCellBinding
    private lateinit var layoutManager_calendar: RecyclerView.LayoutManager
    private lateinit var calendarView: RecyclerView
    private var adapter_calendar = AdapterCalendar(this)
    private lateinit var calendarView1: CalendarView
    private lateinit var fullDate: TextView
    private lateinit var prevWeek: Button
    private lateinit var nextWeek: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onResume() {
        super.onResume()
        // Hide the navigation view when this fragment is opened
        mainActivity.setNavViewVisibility(false)
    }
    fun ConfirmDelete(position: Int){
        val builder = AlertDialog.Builder(requireContext())
        with(builder){
            setTitle("Подтвердите удаление")
            setPositiveButton("Подтвердить"){dialog, which->
                //adapter.hideDeleteButton(position)

                Log.d("Main","Pos")
                Log.d("SportsmensFragmentDialog position", position.toString())
                Log.d("SportsmensFragmentDialog size", adapter.itemCount.toString())
                Log.d("SportsmensFragmentDialog elements", "Item list: $itemList")
                itemList[param2]?.removeAt(position)
                adapter.removeItem(position)
                editor.putString("exerciseList", Gson().toJson(itemList))
                editor.apply()
                Log.d("SportsmensFragmentDialog elements", "Item list: $itemList")
                //adapter.notifyItemRemoved(position)
                //recyclerView.adapter?.notifyItemRemoved(position)
            }
            setNegativeButton("Отмена"){dialog, which->
                dialog.dismiss()
                //recyclerView.adapter?.notifyDataSetChanged()
            }
            builder.show()
        }
    }
    private fun ShowInput() {
        add_button.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
            val inflater = requireActivity().layoutInflater
            val dialogLayout = inflater.inflate(R.layout.sportsmens_dialog_input, null)
            val type = dialogLayout.findViewById<Spinner>(R.id.type_edit)
            val sportsmen = dialogLayout.findViewById<TextView>(R.id.sportsmen_edit)
            date = dialogLayout.findViewById<TextView>(R.id.date_edit)
            val plan = dialogLayout.findViewById<EditText>(R.id.plan_edit)
            val array = arrayOf("Плавание", "Бег", "Езда на велосипеде", "Лыжи", "ОФП")
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
                val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val minuteInterval = (minute + 7) / 15 * 15
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minuteInterval)
                    dateSelected = calendar.time
                    date.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(dateSelected!!)
                    date.setError(null)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true)
                tpd.show()
            }, year, month, day)
                dpd.datePicker.minDate = calendar.timeInMillis
                dpd.show()
            }
            with(builder){
                setPositiveButton("Добавить"){dialog, which->
                    val random = Random()
                    val randomNumber = random.nextInt(1000)
                    if(itemList[param2]==null){
                        var newsize = size++
                        itemList.apply {
                            put(
                                param2.toString(),
                                mutableListOf(
                                    Exercise(
                                        type.selectedItem.toString(),
                                        "https://picsum.photos/200?random=$randomNumber",
                                        dateSelected,
                                        plan.text.toString(),
                                        'p',
                                        "",
                                        "Item " + (newsize).toString()
                                    )
                                )
                            )

                        }
                        }
                    else {
                        itemList[param2]?.add(
                            Exercise(
                                type.selectedItem.toString(),
                                "https://picsum.photos/200?random=$randomNumber",
                                dateSelected,
                                plan.text.toString(),
                                'p',
                                "",
                                "Item " + (size++).toString()
                            )
                        )
                    }
                    editor.putString("exerciseList", Gson().toJson(itemList))
                    editor.apply()
                    adapter = AdapterExercise(itemList[param2]?.filter {
                        val calendar = Calendar.getInstance()
                        calendar.time = it.itemDate
                        calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                                calendar.get(Calendar.MONTH) == selectedDate.month &&
                    calendar.get(Calendar.YEAR) == selectedDate.year+1900
                    } as MutableList<Exercise>?)
                    adapter.notifyItemInserted(itemList[param2]?.size ?: 0)
                    recyclerView.adapter = adapter
                    setupListeners()
                    Log.d("SportsmensFragment size", adapter.itemCount.toString())
                    Log.d("SportsmensFragment elements", "Item list: $itemList")
                }//Log.d("Main","Positive")}
                setNegativeButton("Отмена"){dialog, which-> Log.d("Main","Negative")}
            }
            builder.setView(dialogLayout)
            builder.show()
        }
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
        val status = dialogLayout.findViewById<TextView>(R.id.status_edit)
        var dateSelected = selectedDate
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
        sportsmen.text = param1.toString()
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

        edit_btn.visibility= VISIBLE
        edit_btn.setOnClickListener {
            toolbar_text.setText("Изменение тренировки")
            edit_btn.visibility= GONE
            state(true)
        }
        with(builder) {
            setPositiveButton("Сохранить") { dialog, which ->
                val random = Random()
                val randomNumber = random.nextInt(1000)
                tmp?.get(position)?.text = type.selectedItem.toString()
                tmp?.get(position)?.img = "https://picsum.photos/200?random=$randomNumber"
                tmp?.get(position)?.itemDate = dateSelected
                tmp?.get(position)?.itemDesc = plan.text.toString()
                tmp?.get(position)?.itemId = "Item " + (size++).toString()
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
                editor.putString("exerciseList", Gson().toJson(itemList))
                editor.apply()
                Log.d("SportsmensFragment size", adapter.itemCount.toString())
                Log.d("SportsmensFragment elements", "Item list: $itemList")
                edit_btn.visibility= VISIBLE
                state(false)
            }//Log.d("Main","Positive")}
            setNegativeButton("Отмена") { dialog, which -> Log.d("Main", "Negative")
                edit_btn.visibility= VISIBLE
                state(false)}
        }
        builder.setView(dialogLayout)
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        binding = CalendarCellBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sportsmens_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences= requireContext().getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        editor= preferences.edit()
        itemList = (requireActivity() as MainActivity).exerciseList
        //Log.d("Check param", param1.toString() + " "+ param2.toString())
        //Log.d("Check getting list\n", itemList[param2].toString())
        toolbar = view.findViewById(R.id.toolbar)
        toolbar_text=view.findViewById(R.id.toolbar_text)
        toolbar_text.setText("Дневник тренировок\n"+param1.toString())
        size = itemList["Item1"]?.size ?: 0
        add_button=view.findViewById(R.id.add_btn)

        calendarView = view.findViewById(R.id.CalendarView)
        initCalendar()

        stateList = (requireActivity() as MainActivity).statemap
        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // Calculate the start and end times for the current week
        val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - 2
        val weekStart = currentTimeMillis - daysFromMonday * 24 * 60 * 60 * 1000
        val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000
        // Set the minimum and maximum dates for the calendar view to show only the current week

        //calendarView.minDate = weekStart
        //calendarView.maxDate = weekEnd
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
//                    calendar.get(Calendar.YEAR) == selectedDate.year+1900
//            } as MutableList<Exercise>?)
//            adapter.notifyDataSetChanged()
//            recyclerView.adapter = adapter
//            setupListeners()
//
//        }
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.recycler_sportsmens_dialog)
        layoutManager = LinearLayoutManager(activity)
        adapter = AdapterExercise(itemList[param2]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = it.itemDate
            calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
                    calendar.get(Calendar.MONTH) == selectedDate.month/* &&
                    calendar.get(Calendar.YEAR) == selectedDate.year*/
        } as MutableList<Exercise>?)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        print(itemList[param2]?.get(0)?.itemDate)
        ShowInput()
        setupListeners()
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                // disable item removal based on swipe gesture
                return Float.MAX_VALUE
            }
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Do nothing, we don't support drag and drop
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Remove the item from the dataset
                val position = viewHolder.absoluteAdapterPosition
                //adapter.ItemViewHolder(viewHolder.itemView).itemDeleteButton.visibility=VISIBLE
                //adapter.removeItem(position)
                if (direction==ItemTouchHelper.RIGHT) {
                    //deleteBtn.visibility= VISIBLE
                    adapter.showDeleteButton(position)
                } else if (direction==ItemTouchHelper.LEFT) {
                    //deleteBtn.visibility = GONE
                    adapter.hideDeleteButton(position)
                }
                Log.d("TAG", "Item swiped")
                adapter.notifyDataSetChanged()//viewHolder.absoluteAdapterPosition)
            }
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {//Log.d("SportsmensFragment", "onChildDraw called")

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Set the delete button visibility based on the swipe direction
                    //Log.d("SportsmensFragment", "onChildDraw surely called")
                    val itemView = viewHolder.itemView
                    itemView.translationY = 0f
                    //l
                    val deleteBtn = itemView.findViewById<TextView>(R.id.item_delete_button)
                    itemView.translationX = dX
                    // Draw the swipe background color
//                    if(dX>0)
//                      deleteBtn.visibility=VISIBLE
//                    else
//                        deleteBtn.visibility=GONE

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SportsmensFragmentDialog.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SportsmensFragmentDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        date.setText("$dayOfMonth/$month/$year")
    }

    fun setupListeners(){
        adapter.setOnDeleteClickListener(object : AdapterExercise.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                ConfirmDelete(position)
            }
        })
        adapter.setOnOpenClickListener(object : AdapterExercise.OnOpenClickListener {
            override fun onOpenClick(position: Int) {
                EditWindow(position)
            }
        })
    }
    private fun initCalendar(){

        val dateFormat = SimpleDateFormat("d MMM yyyy, EE", Locale("ru"))
        val date = dateFormat.format(Date())
        val CalendarView = layoutInflater.inflate(R.layout.calendar, null)
        fullDate =  CalendarView.findViewById<TextView>(R.id.full_date)
        prevWeek =   CalendarView.findViewById<Button>(R.id.prevWeek)
        nextWeek =  CalendarView.findViewById<Button>(R.id.nextWeek)

        binding.apply {
            calendarView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            calendarView.adapter = adapter_calendar
            fullDate.text = date
            prevWeek.setOnClickListener{ adapter_calendar.previousWeekAction() }
            nextWeek.setOnClickListener { adapter_calendar.nextWeekAction() }
            adapter_calendar.fillWeekList(Calendar.getInstance())
        }
    }

    override fun onClick(day: String) {
        val dayOfWeek = 2; // Monday
        val now = Calendar.getInstance()
        val weekday = now.get(Calendar.DAY_OF_WEEK)
        binding.cellDayText.text = day
        Log.d("Click", "Click")
    }
}