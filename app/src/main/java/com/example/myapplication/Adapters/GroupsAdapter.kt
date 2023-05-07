package com.example.myapplication.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myapplication.R
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
import com.example.myapplication.navigation_pages.ExercisesInGroup

/**
 * Класс, реализующий адаптер для RecyclerView
 * @property groupsList - Лист групп, выводимых в RecyclerView
 * @author Севастьянов Иван
 */
class GroupsAdapter(
    private val groupsList: MutableList<Item>): RecyclerView.Adapter<GroupsAdapter.GroupsView>() {
    //new
    private val deleteButtonsVisible = mutableSetOf<String>()
    private var onDeleteClickListener: OnDeleteClickListener? = null
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }


    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }
    /**
     * Создает новый объект ViewHolder
     * @param parent - где создаем
     * @param viewType
     * @author Севастьянов Иван
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsView {
        //old
        // val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_item,parent,false)
        //new
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_example,parent,false)
        return  GroupsView(itemView)
    }

    /**
     * Функция для получения кол-ва элементов в списке
     * @return Количество элементов в списке
     * @author Севастьянов Иван
     */
    override fun getItemCount(): Int {
        return groupsList.size
    }
    //new
    fun getItem(position: Int): String {
        //Log.d("Id: ",itemList[position].itemId)
        return groupsList[position].itemId
    }
    fun getVisibility(position: Int) : Boolean{
        return deleteButtonsVisible.contains(getItem(position))
    }
    fun showDeleteButton(position: Int) {
        deleteButtonsVisible.add(getItem(position))
        notifyItemChanged(position)
    }

    fun hideDeleteButton(position: Int) {
        deleteButtonsVisible.remove(getItem(position))
        notifyItemChanged(position)
    }

    /**
     * Функция для вывода элемента
     * @param holder
     * @param position - позиция вывода
     * @author Севастьянов Иван
     */
    override fun onBindViewHolder(holder: GroupsView, position: Int) {
        val currentItem = groupsList[position]
        holder.groupName.text = currentItem.text
        //new
        //holder.itemPicture.visibility=GONE
        Glide.with(holder.itemView)
            .load(currentItem.img)
            .transform(CircleCrop())
            .into(holder.itemPicture)
        holder.detailsBtn.tag = currentItem

        holder.detailsBtn.setOnClickListener{
            //old
            //val fragment = ExercisesInGroup.newInstance(currentItem.name,currentItem.name)
            //new
            val fragment = ExercisesInGroup.newInstance(currentItem.text,currentItem.itemId)
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_groups,fragment)
                .addToBackStack(null)
                .commit()
        }
        //new
        holder.itemDeleteButton.setOnClickListener {
            // Delete item from list and update RecyclerView
            onDeleteClickListener?.onDeleteClick(position)
        }
        holder.itemDeleteButton.visibility = if (deleteButtonsVisible.contains(getItem(position))) View.VISIBLE else GONE
        Log.d("deleteButtonsVisible", deleteButtonsVisible.toString())
    }


    class GroupsView(itemView: View):RecyclerView.ViewHolder(itemView){
        //old
        // val groupName: TextView = itemView.findViewById(R.id.groupName)
        //val detailsBtn: ImageButton = itemView.findViewById(R.id.options)
        //new
        val groupName: TextView = itemView.findViewById(R.id.item_text)
        val itemPicture: ImageView = itemView.findViewById(R.id.item_image)
        val detailsBtn: TextView = itemView.findViewById(R.id.item_open_button)
        val itemDeleteButton: TextView = itemView.findViewById(R.id.item_delete_button)
    }
}