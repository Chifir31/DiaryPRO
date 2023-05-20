package com.example.myapplication.Adapters


import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.example.myapplication.databinding.CalendarCellBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AdapterCalendar(val listener: Listener) : RecyclerView.Adapter<AdapterCalendar.CalendarViewHolder>() {
    val weekList = ArrayList<Date>()
    val statusList = ArrayList<String>()
    var currentMonday = 0
    var isGroup = false
    lateinit var exerciseMap: ArrayMap<String, MutableList<Exercise>>
    lateinit var currentDay: Date
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
        if (statusList[position] == "c"){
            color = R.color.teal_200
        }
        if (statusList[position] == "h"){
            color = R.color.yellow
        }
        if (statusList[position] == "f"){
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

    fun fillWeekList(now: Calendar, allExercise: ArrayMap<String, MutableList<Exercise>> = exerciseMap, itGroup: Boolean = isGroup){
        if (!::currentDay.isInitialized){
            currentDay = now.time
            exerciseMap = allExercise
            isGroup = itGroup
        }
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
        weekList.add(date)
        (1..6).forEach {
            now.add(Calendar.DAY_OF_YEAR, +1)
            date = now.getTime()
            weekList.add(date)
        }
        if (!isGroup) {
            fillStatusList()
        } else {
            while (statusList.size != 7) {
                statusList.add("p")
            }
        }
        Log.d("statusList:", statusList.toString())
        notifyDataSetChanged()
    }

    fun fillStatusList(){
        var weekMapDate = ArrayMap<String, String>()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var position = -1
        if (currentDay !in weekList){
            val dayStr = SimpleDateFormat("dd/MM/yy", Locale("ru")).format(weekList[6])
            val currDayStr =  SimpleDateFormat("dd/MM/yy", Locale("ru")).format(currentDay)

            val firstDate: Date = sdf.parse(dayStr) as Date
            val secondDate: Date = sdf.parse(currDayStr) as Date
            val cmp = firstDate.compareTo(secondDate)
            when {
                cmp > 0 -> {
                    while (statusList.size != 7) {
                        statusList.add("p")
                    }
                    return
                }
//                cmp < 0 -> {
//
//                }
//                else -> {
//
//                }
            }
        } else{
            position = weekList.indexOf(currentDay)
        }
        for (i in 0 until weekList.size){
            weekMapDate[SimpleDateFormat("dd/MM/yy", Locale("ru")).format(weekList[i])] = "n"
        }
        weekMapDate = getStatusWeek(weekMapDate)
        for (i in 0 until weekList.size){
            var status = weekMapDate[SimpleDateFormat("dd/MM/yy", Locale("ru")).format(weekList[i])]
            if (position != -1) {
                if (i >= position){
                    status = "p"
                } else {
                    if (status == "p"){
                        status = "f"
                    }
                }
            }
            if (status != null) {
                statusList.add(status)
            }
        }
    }


    fun getStatusWeek(weekMapDate: ArrayMap<String, String>): ArrayMap<String, String> {
        for (i in 0 until exerciseMap.size) {
            val value: MutableList<Exercise> = exerciseMap.valueAt(i)
            for(j in 0 until value.size) {
                val exercise = value[j]
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = (exercise.itemDate).toLong()
                val trainingDay = SimpleDateFormat("dd/MM/yy", Locale("ru")).format(calendar.getTime())
                if (trainingDay in weekMapDate){
                    if (exercise.itemState == "f"){
                        weekMapDate[trainingDay] = exercise.itemState
                        continue
                    }
                    if (exercise.itemState == "h" && weekMapDate[trainingDay] != "f"){
                        weekMapDate[trainingDay] = exercise.itemState
                        continue
                    }
                    if (exercise.itemState == "c" && (weekMapDate[trainingDay] != "f" && weekMapDate[trainingDay] != "h")){
                        weekMapDate[trainingDay] = exercise.itemState
                        continue
                    }
                    if (exercise.itemState == "p" && (weekMapDate[trainingDay] != "f" && weekMapDate[trainingDay] != "h" && weekMapDate[trainingDay] != "c")){
                        weekMapDate[trainingDay] = exercise.itemState
                    }
                }
            }
        }
        return weekMapDate
    }

    fun setFirstLastDaysOfWeek(): String {
        val firstDay = SimpleDateFormat("dd.MM.yy", Locale("ru")).format(weekList[0])
        val lastDay = SimpleDateFormat("dd.MM.yy", Locale("ru")).format(weekList[6])
        return "$firstDay - $lastDay"
    }

    interface Listener{
        fun onClick(day: Date)
    }
}
