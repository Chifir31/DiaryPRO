package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Group

class GroupsAdapter(private val groupsList: ArrayList<Group>): RecyclerView.Adapter<GroupsAdapter.GroupsView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsView {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_item,parent,false)
        return  GroupsView(itemView)
    }

    override fun getItemCount(): Int {
        return groupsList.size
    }

    override fun onBindViewHolder(holder: GroupsView, position: Int) {
        val currentItem = groupsList[position]
        holder.groupName.text = currentItem.name
    }


    class GroupsView(itemView: View):RecyclerView.ViewHolder(itemView){
        val groupName: TextView = itemView.findViewById(R.id.groupName)
    }
}