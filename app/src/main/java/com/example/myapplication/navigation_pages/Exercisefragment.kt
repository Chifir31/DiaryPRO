package com.example.myapplication.navigation_pages

import MyAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Item
import java.text.SimpleDateFormat
import java.util.*


class Exercisefragment : Fragment() {
    lateinit var dateTextView: TextView
    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: MutableList<Item>

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

        itemList = arrayListOf<com.example.myapplication.data.Item>(
            Item("Item 1", "https://example.com/image1.jpg", "Item 1"),
            Item("Item 2", "https://example.com/image2.jpg", "Item 2"),
            Item("Item 3", "https://example.com/image3.jpg", "Item 3"),
            Item("Item 4", "https://example.com/image4.jpg", "Item 4"),
            Item("Item 5", "https://example.com/image5.jpg", "Item 5")
        )

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.exerciseList)
        recyclerView.layoutManager = layoutManager
        adapter = MyAdapter(itemList)

        recyclerView.adapter = adapter
    }

}