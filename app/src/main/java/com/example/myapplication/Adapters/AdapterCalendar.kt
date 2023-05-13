package com.example.myapplication.Adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.CalendarCellBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AdapterCalendar(val listener: Listener) : RecyclerView.Adapter<AdapterCalendar.CalendarViewHolder>() {
    val weekList = ArrayList<Date>()
    var currentMonday = 0
    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = CalendarCellBinding.bind(itemView)
        fun bind (day: Date, listener: Listener) = with(binding){
            val date = day.getTime()
            val dateStr = SimpleDateFormat("dd", Locale("ru")).format(date)
            cellDayText.text = dateStr
            itemView.setOnClickListener {
                listener.onClick(day)
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

    fun fillWeekList(now: Calendar){
        val dayOfWeek = 2; // Monday
        val weekday = now.get(Calendar.DAY_OF_WEEK)

        val days = dayOfWeek - weekday + currentMonday;
        now.add(Calendar.DAY_OF_YEAR, days)
        val date = now.getTime()
        weekList.add(date)
        (1..6).forEach {
            now.add(Calendar.DAY_OF_YEAR, +1)
            val date = now.getTime()
            weekList.add(date)
        }
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClick(day: Date)
    }
}
