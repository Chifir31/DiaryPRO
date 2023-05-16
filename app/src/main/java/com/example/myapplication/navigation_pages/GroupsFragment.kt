package com.example.myapplication.navigation_pages

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.GroupsAdapter
import com.example.myapplication.R
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.MainActivity
import com.example.myapplication.data.Exercise
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * Класс, реализующий фрагмент групп
 * @author Севастьянов Иван, Шестак Вячеслав
 */
class GroupsFragment : Fragment() {
    private lateinit var adapter: GroupsAdapter
    private lateinit var recyclerView: RecyclerView
    //old
    //private lateinit var groupsArrayList: MutableList<Group>
    //new
    private lateinit var groupsArrayList: MutableList<Group>
    private lateinit var dateTextView: TextView
    private lateinit var addBtn: ImageButton
    private lateinit var rootNode: FirebaseDatabase
    private lateinit var database: DatabaseReference

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

        dateTextView = view.findViewById(R.id.date_textview)
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate)

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = layoutManager
        adapter = GroupsAdapter(groupsArrayList)

        recyclerView.adapter = adapter

        //new
        adapter.setOnDeleteClickListener(object : GroupsAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                //Log.d("Pos", "$position")
                ConfirmDelete(position)
            }
        })
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 0.5f
            }
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Do nothing, we don't support drag and drop
                return false
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                // Adjust the swipe velocity threshold here (default is 2000f)
                return 1000f
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Remove the item from the dataset
                val position = viewHolder.absoluteAdapterPosition
                //adapter.ItemViewHolder(viewHolder.itemView).itemDeleteButton.visibility=VISIBLE
                //adapter.removeItem(position)
                if (direction==ItemTouchHelper.RIGHT) {
                    //deleteBtn.visibility= VISIBLE
                    adapter.showDeleteButton(position)
                } else if (direction==ItemTouchHelper.LEFT) {
                    //deleteBtn.visibility = GONE
                    adapter.hideDeleteButton(position)
                }
                Log.d("TAG", "Item swiped")
                adapter.notifyDataSetChanged()//viewHolder.absoluteAdapterPosition)
            }
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {//Log.d("SportsmensFragment", "onChildDraw called")

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    // Adjust the maximum swipe distance here
                    val maxSwipeDistance = 400f  // Set your desired distance
                    val itemView = viewHolder.itemView
                    val itemPosition = viewHolder.absoluteAdapterPosition
                    val deleteBtn = itemView.findViewById<TextView>(R.id.item_delete_button)
                    // Set the delete button visibility based on the swipe direction
                    if (dX > 0) {
                        deleteBtn.visibility= View.VISIBLE
                    } else if (dX <= 0) {
                        deleteBtn.visibility = View.GONE
                    }
                    //In case when deleteButton still in set when swipe to the right was weak
                    if(adapter.getVisibility(itemPosition))
                        deleteBtn.visibility = View.VISIBLE
                    // Restrict the item movement to the defined maximum swipe distance
                    val limitedDX = when {
                        dX > maxSwipeDistance -> maxSwipeDistance
                        dX < -maxSwipeDistance -> -maxSwipeDistance
                        else -> dX
                    }

                super.onChildDraw(c, recyclerView, viewHolder, limitedDX, dY, actionState, isCurrentlyActive)
                    }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        addBtn = view.findViewById<ImageButton>(R.id.addgroup)
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
                //groupsArrayList.removeAt(position)
                //recyclerView.adapter?.notifyItemRemoved(position)
                database = Firebase.database.reference

                val currentUser = Firebase.auth.currentUser
                lateinit var email: String
                currentUser?.let {
                    email = it.email.toString()
                }

                database.child("groups").child(email.split("@")[0]).child(groupsArrayList[position].name).removeValue()
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
        //old
        //val newGroup = Group(name,name)
        //new
        //val newGroup = Item(name,"https://picsum.photos/200?random",name)
        val newGroup = Group(name,name,arrayListOf<String>(), mutableListOf<Exercise>())
        groupsArrayList.add(0,newGroup)
        adapter.notifyItemInserted(0)

        //rootNode = FirebaseDatabase.getInstance()
        database = Firebase.database.reference

        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }

        database.child("groups").child(email.split("@")[0]).child(name).setValue(newGroup)
    }

    /**
     * Инициализация данных
     * @author Севастьянов Иван
     */
    private fun dataInit(){
        groupsArrayList = mutableListOf<Group>()
        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }

        database = Firebase.database.reference
        database.child("groups").child(email.split("@")[0]).get().addOnSuccessListener {
            if(it.exists()){
                var children = it.children
                children.forEach{
                    var name = it.child("name").getValue().toString()
                    var itemId = it.child("itemId").getValue().toString()
                    var membersArray = mutableListOf<String>()
                    var members = it.child("members").children
                    members.forEach {
                        membersArray.add(it.getValue().toString())
                    }
                    groupsArrayList.add(Group(name,itemId, mutableListOf<String>(), mutableListOf<Exercise>()))
                }
            }
        }
    }
}