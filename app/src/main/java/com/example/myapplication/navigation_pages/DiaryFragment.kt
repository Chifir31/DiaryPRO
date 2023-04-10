package com.example.myapplication.navigation_pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment() {
    private lateinit var dateTextView: TextView
    private lateinit var calendarView: CalendarView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTextView = view.findViewById(R.id.date_textview)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate)

        calendarView = view.findViewById(R.id.CalendarView )
        // Get the current time in milliseconds
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
    }
}

