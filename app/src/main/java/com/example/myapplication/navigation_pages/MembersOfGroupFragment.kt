package com.example.myapplication.navigation_pages

import AdapterSportsmens
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MembersOfGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MembersOfGroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_text: TextView
    private lateinit var adapter: AdapterSportsmens
    private lateinit var add_button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var itemList: MutableList<Item>
    private lateinit var database: DatabaseReference
    private var size : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_members_of_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        toolbar_text=view.findViewById(R.id.toolbar_text)
        toolbar_text.setText("Список спортсменов\n"+param1.toString())
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        database = Firebase.database.reference
        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }

        var membersArray = mutableListOf<String>()
        database.child("groups").child(email.split("@")[0]).child(param1.toString())
            .child("members").get().addOnSuccessListener {
                if (it.exists()){
                    var children = it.children
                    children.forEach {
                        val member = it.getValue().toString()
                        database.child("users").child(it.getValue().toString()).get().addOnSuccessListener {
                            if (it.exists()){
                                membersArray.add(member)
                            }
                        }

                    }
                }else{
                    membersArray = mutableListOf<String>()
                }
            }

        var tempList = mutableListOf<Item>()
        membersArray.forEach {
            database.child("users").child(it).addListenerForSingleValueEvent(object :
                ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val random = Random()
                    val randomNumber = random.nextInt(1000)
                    val children = snapshot.children
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

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        itemList = tempList
        size = itemList.size+1
        recyclerView = view.findViewById(R.id.recycler_sportsmens)
        layoutManager = LinearLayoutManager(activity)
        adapter = AdapterSportsmens(itemList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        add_button = view.findViewById(R.id.add_button)
        Log.d("new","F")
        add_button.setOnClickListener{
            Log.d("new","D")
            val builder = AlertDialog.Builder(requireContext())
            val inflater = requireActivity().layoutInflater
            val dialogLayout = inflater.inflate(R.layout.c_activity_input, null)
            val edit = dialogLayout.findViewById<EditText>(R.id.input)
            with(builder){
                Log.d("new","A")
                setTitle("Введите ID спортсмена")
                setPositiveButton("Добавить") { dialog, which ->
                    Log.d("new","B")
                    val random = Random()
                    val randomNumber = random.nextInt(1000)
                    database.child("users").child(edit.text.toString()).get().addOnSuccessListener{
                        if(it.exists()){
                            val role = it.child("role").value.toString()
                            if(role == "S"){
                                val currentUser = Firebase.auth.currentUser
                                lateinit var email: String
                                currentUser?.let {
                                    email = it.email.toString()
                                }

                                itemList.add(Item(
                                    edit.text.toString(),
                                    "https://picsum.photos/200?random=$randomNumber",
                                    "Item " + (size++).toString()
                                ))

                                database.child("groups").child(email.split("@")[0])
                                    .child(param1.toString()).child("members").setValue(itemList)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
            builder.setView(dialogLayout)
            builder.show()
        }

        adapter.setOnDeleteClickListener(object : AdapterSportsmens.OnDeleteClickListener {
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
                if (direction== ItemTouchHelper.RIGHT) {
                    //deleteBtn.visibility= VISIBLE
                    adapter.showDeleteButton(position)
                } else if (direction== ItemTouchHelper.LEFT) {
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
                    // Set the delete button visibility based on the swipe direction
                    //Log.d("SportsmensFragment", "onChildDraw surely called")
                    val itemView = viewHolder.itemView
                    itemView.translationY = 0f
                    // deleteBtn = itemView.findViewById<TextView>(R.id.item_delete_button)

                    itemView.translationX = dX
                    // Draw the swipe background color

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun ConfirmDelete(position: Int){
        val builder = AlertDialog.Builder(requireContext())
        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }
        with(builder){
            setTitle("Подтвердите удаление")
            setPositiveButton("Подтвердить"){dialog, which->
                //adapter.hideDeleteButton(position)

                Log.d("Main","Pos")
                Log.d("SportsmensFragment position", position.toString())
                Log.d("SportsmensFragment size", adapter.itemCount.toString())
                Log.d("SportsmensFragment elements", "Item list: $itemList")
                itemList.removeAt(position)
                database.child("groups").child(email.split("@")[0]).child(param1.toString())
                    .child("members").setValue(itemList)
                adapter.removeItem(position)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MembersOfGroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            MembersOfGroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}