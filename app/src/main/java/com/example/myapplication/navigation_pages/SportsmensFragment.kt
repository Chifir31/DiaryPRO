package com.example.myapplication.navigation_pages

import MyAdapter
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Group
import com.example.myapplication.data.Item
import java.text.SimpleDateFormat
import java.util.*

class SportsmensFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var dateTextView: TextView
    private lateinit var add_button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var itemList: MutableList<Item>
    private var size : Int = 0
    private fun ShowInput(position: Int) {
        add_button.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = requireActivity().layoutInflater
            val dialogLayout = inflater.inflate(R.layout.c_activity_input, null)
            val edit = dialogLayout.findViewById<EditText>(R.id.input)

            with(builder){
                setTitle("Введите ID спортсмена")
                setPositiveButton("Добавить"){dialog, which-> itemList.add(Item(edit.text.toString(), "https://example.com/image1.jpg", "Item"+(size++).toString()))
                    adapter.notifyItemInserted(position)
                    Log.d("SportsmensFragment", adapter.itemCount.toString())
                    Log.d("SportsmensFragment", "Item list: $itemList")
                    Log.d("SportsmensFragment", "New item added at position: $position")
                }//Log.d("Main","Positive")}
                setNegativeButton("Отмена"){dialog, which-> Log.d("Main","Negative")}
            }
            builder.setView(dialogLayout)
            builder.show()

        }
    }

    fun ConfirmDelete(position: Int){
        val builder = AlertDialog.Builder(requireContext())
        with(builder){
            setTitle("Подтвердите удаление")
            setPositiveButton("Подтвердить"){dialog, which->
                //adapter.hideDeleteButton(position)
                //notifyItemRemoved(position)
                Log.d("Main","Pos")
                adapter.removeItem(position)
                //recyclerView.adapter?.notifyItemRemoved(position)
            }
            setNegativeButton("Отмена"){dialog, which->
                dialog.dismiss()
                //recyclerView.adapter?.notifyDataSetChanged()
            }
            builder.show()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sportsmens, container, false)
        toolbar = view.findViewById(R.id.toolbar)

        dateTextView = view.findViewById(R.id.date_textview)
        add_button=view.findViewById(R.id.add_button)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
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
        size = itemList.size+1
        recyclerView = view.findViewById(R.id.recycler_sportsmens)
        layoutManager = LinearLayoutManager(activity)
        adapter = MyAdapter(itemList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        ShowInput(itemList.size)
        adapter.setOnDeleteClickListener(object : MyAdapter.OnDeleteClickListener {
            override fun onDeleteClick(position: Int) {
                Log.d("Pos", "$position")
                ConfirmDelete(position)
            }
        })
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                // disable item removal based on swipe gesture
                return Float.MAX_VALUE
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
            ) {Log.d("SportsmensFragment", "onChildDraw called")

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Set the delete button visibility based on the swipe direction
                    Log.d("SportsmensFragment", "onChildDraw surely called")
                    val itemView = viewHolder.itemView
                    itemView.translationY = 0f
                    //l deleteBtn = itemView.findViewById<TextView>(R.id.item_delete_button)

                    itemView.translationX = dX
                    // Draw the swipe background color

            }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        // Inflate the layout for this fragment
        return view
    }
}

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_activity_input)
    }
}


