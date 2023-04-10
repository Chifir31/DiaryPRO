package com.example.myapplication.navigation_pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.GroupsAdapter
import com.example.myapplication.R
import com.example.myapplication.data.Group
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.ExerciseGroupFragment
import com.example.myapplication.SwipeGesture
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Класс, реализующий фрагмент групп
 * @author Севастьянов Иван, Шестак Вячеслав
 */
class GroupsFragment : Fragment() {
    private lateinit var adapter: GroupsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupsArrayList: ArrayList<Group>

    /**
     * Функция для инициализации фрагмента
     * @return Фрагмент
     * @author Шестак Вячеслав
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)
        return view
    }

    /**
     * Функция для инициализации внутренних элементов фрагмента
     * @param view
     * @param savedInstanceState
     * @author Севастьянов Иван
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInit()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = layoutManager
        adapter = GroupsAdapter(groupsArrayList)

        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : GroupsAdapter.onItemClickListener {
            override fun onItemClicked(position: Int) {
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.container,ExerciseGroupFragment())
                    .addToBackStack(null)
                    .commit()
            }

        })


        val swipeToDeleteCallback = object : SwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                ConfirmDelete(position)
            }
<<<<<<< Updated upstream
=======
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Set the delete button visibility based on the swipe direction
                    val itemView = viewHolder.itemView
                    val deleteBtn = itemView.findViewById<TextView>(R.id.group_delete_button)
                    if (dX > 0) {
                        deleteBtn.visibility= View.VISIBLE
                    } else if (dX < 0) {
                        deleteBtn.visibility = View.GONE
                    }
                    itemView.translationX = dX
                    // Draw the swipe background color
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
>>>>>>> Stashed changes
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val addBtn = view.findViewById<ImageButton>(R.id.addgroup)
        addBtn.setOnClickListener{
            ShowInput()
        }
    }

    /**
     * Функция для подтверждения удаления элемента списка
     * @param position - Позиция, удаляемого элемента
     * @author Севастьянов Иван
     */
    private fun ConfirmDelete(position: Int){
        val builder = AlertDialog.Builder(requireContext())
        with(builder){
            setTitle("Подтвердите удаление")
            setPositiveButton("Подтвердить"){dialog, which->
                Log.d("Main","Pos")
                groupsArrayList.removeAt(position)
                recyclerView.adapter?.notifyItemRemoved(position)
            }
            setNegativeButton("Отмена"){dialog, which->
                dialog.dismiss()
                recyclerView.adapter?.notifyDataSetChanged()
            }
            builder.show()
        }
    }

    /**
     * Функция для вывода окна для ввода названия новой группы
     * @author Севастьянов Иван
     */
    private fun ShowInput(){
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.new_group_input, null)
        val builder = AlertDialog.Builder(requireContext())
        val edit = dialogLayout.findViewById<EditText>(R.id.input)

        with(builder){
            setTitle("Введите название группы")
            setPositiveButton("Добавить"){dialog, which->
                Log.d("Main","Positive")
                if (edit.text.toString().isNotEmpty()) {
                    InsertGroup(edit.text.toString())
                }
            }
            setNegativeButton("Отмена"){dialog, which-> dialog.dismiss()}
        }
        builder.setView(dialogLayout)
        builder.show()
    }

    /**
     * Функция для добавления новой группы в список
     * @author Севастьянов Иван
     */
    private fun InsertGroup(name: String){
        val newGroup = Group(name)
        groupsArrayList.add(0,newGroup)
        adapter.notifyItemInserted(0)
    }

    /**
     * Инициализация данных
     * @author Севастьянов Иван
     */
    private fun dataInit(){
        groupsArrayList = arrayListOf<Group>()

        groupsArrayList.add(Group("Name1"))
        groupsArrayList.add(Group("Name2"))
        groupsArrayList.add(Group("Name3"))
        groupsArrayList.add(Group("Name4"))
        groupsArrayList.add(Group("Name5"))
        groupsArrayList.add(Group("Name6"))

    }
}