package com.example.myapplication.navigation_pages

import AdapterSportsmens
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class SportsmensFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var dateTextView: TextView
    private lateinit var add_button: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterSportsmens
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var itemList: MutableList<Item>
    private var size : Int = 0
    lateinit var preferences: SharedPreferences
    lateinit var editor :  SharedPreferences.Editor
    private lateinit  var database: DatabaseReference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the listener for back stack changes
        preferences= requireContext().getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        editor= preferences.edit()
        toolbar = view.findViewById(R.id.toolbar)

        dateTextView = view.findViewById(R.id.date_textview)
        add_button=view.findViewById(R.id.add_button)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = dateFormat.format(currentDate)

        //Загружаем список спортсменов
        val tempList = (requireActivity() as MainActivity).sportsmensList
        if (tempList.size == 0) {
            //database = Firebase.database.reference
            val currentUser = Firebase.auth.currentUser
            lateinit var email: String
            currentUser?.let {
                email = it.email.toString()
            }
            database.child(email.split("@")[0]).get().addOnSuccessListener {
                if (it.exists()) {
                    val reference = database.child(email.split("@")[0])
                        .child("list_of_sportsmen")
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {
                            TODO("not implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val random = Random()
                            val randomNumber = random.nextInt(1000)
                            val children = snapshot!!.children
                            children.forEach {
                                val value = it.getValue()
                                val item = Item(
                                    value.toString(),
                                    "https://picsum.photos/200?random=$randomNumber",
                                    value.toString()
                                )
                                tempList.add(item)
                            }
                        }

                    })
                } else {
                    Log.d("S", "User does not exist")
                }
            }
        }

        itemList = tempList
        size = itemList.size+1
        recyclerView = view.findViewById(R.id.recycler_sportsmens)
        layoutManager = LinearLayoutManager(activity)
        adapter = AdapterSportsmens(itemList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        ShowInput()
        adapter.setOnDeleteClickListener(object : AdapterSportsmens.OnDeleteClickListener {
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
                val position = viewHolder.absoluteAdapterPosition
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
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    // Adjust the maximum swipe distance here
                    val maxSwipeDistance = 400f  // Set your desired distance
                    val itemView = viewHolder.itemView
                    val itemPosition = viewHolder.absoluteAdapterPosition
                    val deleteBtn = itemView.findViewById<TextView>(R.id.item_delete_button)
                    // Set the delete button visibility based on the swipe direction
                    if (dX > 0) {
                        deleteBtn.visibility = VISIBLE
                    } else if (dX <= 0) {
                        deleteBtn.visibility = GONE
                    }
                    //In case when deleteButton still in set when swipe to the right was weak
                    if(adapter.getVisibility(itemPosition))
                        deleteBtn.visibility = VISIBLE

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
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = Firebase.database.reference
        database = database.child("users")
        val view = inflater.inflate(R.layout.fragment_sportsmens, container, false)
        // Inflate the layout for this fragment
        return view
    }
    /* This function shows an AlertDialog with an EditText view to allow the user to input data.
    The data is then added to the itemList and the adapter is notified of the change.
    The size and elements of the itemList are logged for debugging purposes.*/
    private fun ShowInput() {
        add_button.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = requireActivity().layoutInflater
            val dialogLayout = inflater.inflate(R.layout.c_activity_input, null)
            val edit = dialogLayout.findViewById<EditText>(R.id.input)

            with(builder){
                setTitle("Введите ID спортсмена")
                setPositiveButton("Добавить"){dialog, which->
                    val random = Random()
                    val randomNumber = random.nextInt(1000)

                    //Поиск спортсмена в бд
                    //database = Firebase.database.reference
                    database.child(edit.text.toString()).get().addOnSuccessListener {
                        if (it.exists()) {
                            //user = it.child("role").value.toString()
                            val role = it.child("role").value.toString()
                            if (role == "S"){
                                //Добавление спортсмена к тренеру
                                val currentUser = Firebase.auth.currentUser
                                lateinit var email: String
                                currentUser?.let {
                                    email = it.email.toString()
                                }
                                database.child(email.split("@")[0]).get()
                                    .addOnSuccessListener {
                                        if (it.exists()) {
                                            val sportsmen_list =
                                                database.child(email.split("@")[0])
                                                    .child("list_of_sportsmen")
                                                    .push() //Хз нужен ли здесь push, но он должен фиксть ошибки с одновременным обращением к элементу
                                            sportsmen_list.setValue(edit.text.toString())
                                            itemList.add(
                                                Item(
                                                    edit.text.toString(),
                                                    "https://picsum.photos/200?random=$randomNumber",
                                                    "Item " + (size++).toString()
                                             )
                                            )
                                            adapter.notifyItemInserted(itemList.size)
                                            editor.putString("sportsmensList", Gson().toJson(itemList))
                                            editor.apply()
                                        } else {
                                            Log.d("S", "User does not exist")
                                        }
                                    }

                            /*itemList.add(Item(edit.text.toString(), "https://picsum.photos/200?random=$randomNumber", "Item "+(size++).toString()))
                            adapter.notifyItemInserted(itemList.size)
                            editor.putString("sportsmensList", Gson().toJson(itemList))
                            editor.apply()*/
                                Log.d("SportsmensFragment size", adapter.itemCount.toString())
                                Log.d("SportsmensFragment elements", "Item list: $itemList")

                            }
                        }else{
                            Log.d("H","No sportsmen with such login")//Добавить вывод сообщения об ошибки
                        }
                    }

                    /*itemList.add(Item(edit.text.toString(), "https://picsum.photos/200?random=$randomNumber", "Item "+(size++).toString()))
                    adapter.notifyItemInserted(itemList.size)
                    editor.putString("sportsmensList", Gson().toJson(itemList))
                    editor.apply()*/
                    Log.d("SportsmensFragment size", adapter.itemCount.toString())
                    Log.d("SportsmensFragment elements", "Item list: $itemList")
                }//Log.d("Main","Positive")}
                setNegativeButton("Отмена"){dialog, which-> Log.d("Main","Negative")}
            }
            builder.setView(dialogLayout)
            builder.show()

        }
    }
    /* This function shows an AlertDialog to confirm the deletion of an item.
    If the user confirms the deletion, the adapter is notified to remove the item
    and the position, size, and elements of the itemList are logged for debugging purposes.*/
    fun ConfirmDelete(position: Int){
        val builder = AlertDialog.Builder(requireContext())
        with(builder){
            setTitle("Подтвердите удаление")
            setPositiveButton("Подтвердить"){dialog, which->
                //adapter.hideDeleteButton(position)

                Log.d("Main","Pos")
                Log.d("SportsmensFragment position", position.toString())
                Log.d("SportsmensFragment size", adapter.itemCount.toString())
                Log.d("SportsmensFragment elements", "Item list: $itemList")
                adapter.removeItem(position)
                Log.d("SportsmensFragment elements", "Item list: $itemList")
                editor.putString("sportsmensList", Gson().toJson(itemList))
                editor.apply()
                //adapter.notifyItemRemoved(position)
                //recyclerView.adapter?.notifyItemRemoved(position)
            }
            setNegativeButton("Отмена"){dialog, which->
                dialog.dismiss()
                //recyclerView.adapter?.notifyDataSetChanged()
            }
            builder.show()
        }
    }
}
// This is an InputActivity class that sets the content view to R.layout.c_activity_input.
class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_activity_input)
    }
}


