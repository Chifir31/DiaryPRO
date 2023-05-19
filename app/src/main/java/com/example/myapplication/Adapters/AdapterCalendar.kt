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
    val statusList = ArrayList<Int>()
    var currentMonday = 0
    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = CalendarCellBinding.bind(itemView)
        fun bind (day: Date, listener: Listener, color: Int) = with(binding){
            val date = day.getTime()
            val dateStr = SimpleDateFormat("EE\ndd", Locale("ru")).format(date)
            cellDayText.text = dateStr
            itemView.setBackgroundResource(color)
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
        var color = R.color.black
        if (statusList[position] == 1){
            color = R.color.teal_200
        }
        if (statusList[position] == 0){
            color = R.color.yellow
        }
        if (statusList[position] == -1){
            color = R.color.red
        }
        holder.bind(weekList[position], listener, color)

    }

    override fun getItemCount(): Int {
        return weekList.size
    }


    fun previousWeekAction() {
        weekList.clear()
        statusList.clear()
        val now = Calendar.getInstance()
        val weekday = now.get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = 2; // Monday
        val days = dayOfWeek - weekday;
        currentMonday -= 7
        now.add(Calendar.DAY_OF_YEAR, days)
        fillWeekList(now)
    }

    fun nextWeekAction() {
        weekList.clear()
        statusList.clear()
        val now = Calendar.getInstance()
        val weekday = now.get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = 2; // Monday
        val days = dayOfWeek - weekday;
        currentMonday += 7
        now.add(Calendar.DAY_OF_YEAR, days)
        fillWeekList(now)
    }

    fun fillWeekList(now: Calendar){
        val dayOfWeek = 2; // Monday
        val weekday = now.get(Calendar.DAY_OF_WEEK)
        if (weekday != 1) {
            val days = dayOfWeek - weekday + currentMonday
            now.add(Calendar.DAY_OF_YEAR, days)
        } else{
            val days = -6 + currentMonday
            now.add(Calendar.DAY_OF_YEAR, days)
        }
        var date = now.getTime()

        Log.d("FFF", now.get(Calendar.DAY_OF_WEEK).toString() )
        Log.d("FFF", weekday.toString())
        if (now.get(Calendar.DAY_OF_WEEK) == weekday){
            fillStatusList()
        } else {
            statusList.add((-1..1).random())
        }

        weekList.add(date)

        (1..6).forEach {
            now.add(Calendar.DAY_OF_YEAR, +1)
            if (now.get(Calendar.DAY_OF_WEEK) == weekday){
                fillStatusList()
            } else {
                if (statusList.size != 7) {
                    statusList.add((-1..1).random())
                }
            }
            date = now.getTime()
            weekList.add(date)
        }
        Log.d("statusList:", statusList.toString())
        notifyDataSetChanged()
    }

    fun fillStatusList(){
        while (statusList.size != 7){
            statusList.add(2)
        }
    }


    interface Listener{
        fun onClick(day: Date)
    }
}
