package com.example.myapplication.navigation_pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.GroupsAdapter
import com.example.myapplication.R
import com.example.myapplication.data.Group
import android.util.Log

class GroupsFragment : Fragment() {
    private lateinit var adapter: GroupsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupsArrayList: ArrayList<Group>

    private lateinit var heading: Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInit()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = layoutManager
        adapter = GroupsAdapter(groupsArrayList)
        recyclerView.adapter = adapter

        val addBtn = view.findViewById<Button>(R.id.addgroup)

        addBtn.setOnClickListener{
            addGroup()
        }
    }

    private  fun addGroup(){
        groupsArrayList.add(Group("name2"))
        //adapter.notifyItemInserted(groupsArrayList.size)
        //groupsArrayList.clear()
        adapter.notifyDataSetChanged()
        Log.d("Complete", groupsArrayList.size.toString())
    }

    private fun dataInit(){
        groupsArrayList = arrayListOf<Group>()

        groupsArrayList.add(Group("Name1"))

    }
}