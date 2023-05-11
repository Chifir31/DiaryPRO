package com.example.myapplication.Adapters


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.CalendarCellBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarRecycleAdapter(val listener: Listener) : RecyclerView.Adapter<CalendarRecycleAdapter.CalendarViewHolder>() {
    val weekList = ArrayList<String>()
    var currentMonday = 0
    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = CalendarCellBinding.bind(itemView)
        fun bind (day: String, listener: Listener) = with(binding){
            val current_day: String = day[0].toString() + day[1].toString()
            cellDayText.text = current_day
            itemView.setOnClickListener {
                listener.onClick(day)
                //itemView.setBackgroundResource(R.color.teal_200)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)
        return  CalendarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(weekList[position], listener)

    }

    override fun getItemCount(): Int {
        return weekList.size
    }

    fun fillWeekList(now: Calendar){
        val dayOfWeek = 2; // Monday
        val weekday = now.get(Calendar.DAY_OF_WEEK)

        val days = dayOfWeek - weekday + currentMonday;
        now.add(Calendar.DAY_OF_YEAR, days)
        var date = now.getTime()
        var dateStr = SimpleDateFormat("dd MMM yyyy, EE", Locale("ru")).format(date)
        weekList.add(dateStr)
        (1..6).forEach {
            now.add(Calendar.DAY_OF_YEAR, +1)
            date = now.getTime()
            dateStr = SimpleDateFormat("dd MMM yyyy, EE", Locale("ru")).format(date)
            weekList.add(dateStr)
        }
        notifyDataSetChanged()
    }

    fun previousWeekAction() {
        weekList.clear()
        val now = Calendar.getInstance()
        val weekday = now.get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = 2; // Monday
        val days = dayOfWeek - weekday;
        if (days < 0) currentMonday -= 7
        now.add(Calendar.DAY_OF_YEAR, days)
        fillWeekList(now)
    }

    fun nextWeekAction() {
        weekList.clear()
        val now = Calendar.getInstance()
        val weekday = now.get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = 2; // Monday
        val days = dayOfWeek - weekday;
        if (days < 0) currentMonday += 7
        now.add(Calendar.DAY_OF_YEAR, days)
        fillWeekList(now)
    }

    interface Listener{
        fun onClick(day: String)
    }
}

//class MainActivity : AppCompatActivity(), CalendarRecycleAdapter.Listener{
//    lateinit var binding: ActivityMainBinding
//    private val adapter = CalendarRecycleAdapter(this)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        initCalendar()
//    }
//
//    private fun initCalendar(){
//
//        val dateFormat = SimpleDateFormat("d MMM yyyy, EE", Locale("ru"))
//        val date = dateFormat.format(Date())
//
//        binding.apply {
//            calendarRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
//            calendarRecyclerView.adapter = adapter
//            fullDate.text = date
//            prevWeek.setOnClickListener{ adapter.previousWeekAction() }
//            nextWeek.setOnClickListener { adapter.nextWeekAction() }
//            adapter.fillWeekList(Calendar.getInstance())
//        }
//    }
//
//    override fun onClick(day: String) {
//        val dayOfWeek = 2; // Monday
//        val now = Calendar.getInstance()
//        val weekday = now.get(Calendar.DAY_OF_WEEK)
//        binding.fullDate.text = day
//    }
//
//}