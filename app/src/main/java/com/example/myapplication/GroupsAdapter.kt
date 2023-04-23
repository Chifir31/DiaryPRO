package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Group
import com.example.myapplication.navigation_pages.ExercisesInGroup

/**
 * Класс, реализующий адаптер для RecyclerView
 * @property groupsList - Лист групп, выводимых в RecyclerView
 * @author Севастьянов Иван
 */
class GroupsAdapter(
    private val groupsList: MutableList<Group>): RecyclerView.Adapter<GroupsAdapter.GroupsView>() {

    /**
     * Создает новый объект ViewHolder
     * @param parent - где создаем
     * @param viewType
     * @author Севастьянов Иван
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsView {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_item,parent,false)
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

    /**
     * Функция для вывода элемента
     * @param holder
     * @param position - позиция вывода
     * @author Севастьянов Иван
     */
    override fun onBindViewHolder(holder: GroupsView, position: Int) {
        val currentItem = groupsList[position]
        holder.groupName.text = currentItem.name
        holder.detailsBtn.tag = currentItem

        holder.detailsBtn.setOnClickListener{
            val fragment = ExercisesInGroup.newInstance(currentItem.name,currentItem.name)

            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.Groups_fragment,fragment)
                .addToBackStack(null)
                .commit()
        }

    }


    class GroupsView(itemView: View):RecyclerView.ViewHolder(itemView){
        val groupName: TextView = itemView.findViewById(R.id.groupName)
        val detailsBtn: ImageButton = itemView.findViewById(R.id.options)
    }
}