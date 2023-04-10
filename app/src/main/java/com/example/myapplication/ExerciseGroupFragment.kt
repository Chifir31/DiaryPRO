package com.example.myapplication

import android.graphics.Canvas
import MyAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.GroupsAdapter
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
import java.text.SimpleDateFormat
import java.util.*

class ExerciseGroupFragment : Fragment() {
    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: MutableList<Item>
    private lateinit var addBtn: ImageButton
    private lateinit var groupName: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises_in_group, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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