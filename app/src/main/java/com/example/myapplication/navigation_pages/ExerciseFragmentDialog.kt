package com.example.myapplication.navigation_pages

import AdapterExercise
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseFragmentDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class ExerciseFragmentDialog : Fragment() {
    private lateinit var img: ImageView
    private lateinit var toolbar_text: TextView
    private lateinit var plan: TextView
    private lateinit var state: TextView
    private lateinit var plan_text: TextView
    private lateinit var state_text: TextView
    private lateinit var finish_btn: TextView
    private lateinit var save_btn: TextView
    private lateinit var type: Spinner
    private lateinit var comment: EditText
    private lateinit var type_text: TextView
    private lateinit var comment_text: TextView
    private lateinit var comment1: TextView
    private lateinit var comment1_text: TextView
    private lateinit var itemList: ArrayMap<String, MutableList<Exercise>>
    private lateinit var itemList1: MutableList<Exercise>
    private lateinit var stateList: ArrayMap<String, String>
    private var param1: String? = null
    private var param2: String? = null
    private var param3: Int? = null
    private lateinit var mainActivity: MainActivity
    lateinit var preferences: SharedPreferences
    lateinit var editor :  SharedPreferences.Editor
    private lateinit var database: DatabaseReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onResume() {
        super.onResume()
        // Hide the navigation view when this fragment is opened
        mainActivity.setNavViewVisibility(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getInt(ARG_PARAM3)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_dialog, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences= requireContext().getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)
        editor= preferences.edit()
        //dateTextView = view.findViewById(R.id.date_textview)
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        //dateTextView.text = dateFormat.format(currentDate)
        img = view.findViewById(R.id.exercise_img)
        plan = view.findViewById(R.id.plan_edit)
        state  = view.findViewById(R.id.state_edit)
        plan_text = view.findViewById(R.id.plan_text)
        state_text  = view.findViewById(R.id.state_text)
        toolbar_text  = view.findViewById(R.id.toolbar_text)
        finish_btn  = view.findViewById(R.id.finish_btn)
        save_btn = view.findViewById(R.id.save_btn)
        type = view.findViewById(R.id.type_edit)
        comment = view.findViewById(R.id.com_edit)
        type_text = view.findViewById(R.id.type_text)
        comment_text = view.findViewById(R.id.com_text)
        comment1 = view.findViewById(R.id.com1_edit)
        comment1_text = view.findViewById(R.id.com1_text)
        val randomImageUrl = "https://picsum.photos/200" // Replace with URL of your image
        Glide.with(this)
            .load(randomImageUrl)
            .transform(CircleCrop())
            .placeholder(R.drawable.circular_background) // placeholder image while the actual image loads
            //.error(R.drawable.error_image) // image to display if there is an error loading the actual image
            .into(img)
        val random = Random()
        val randomNumber = random.nextInt(1000)
        var date = Date()

        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }

        var tempList = ArrayMap<String, MutableList<Exercise>>()
        database = Firebase.database.reference
        database.child("Exercise").child(email.split("@")[0]).get().addOnSuccessListener {
            if(it.exists()){
                val children = it.children
                children.forEach{
                    val img = it.child("img").getValue().toString()
                    val itemComm = it.child("itemComm").getValue().toString()
                    val itemDate = it.child("itemDate").getValue().toString()
                    val itemDesc = it.child("itemDesc").getValue().toString()
                    val itemId = it.child("itemId").getValue().toString()
                    val itemState = it.child("itemState").getValue().toString()
                    val text = it.child("text").getValue().toString()

                    if(tempList[email.split("@")[0]]==null){
                        tempList.apply {
                            put(
                                email.split("@")[0],
                                mutableListOf(
                                    Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
                                )
                            )

                        }
                    }else{
                        tempList[email.split("@")[0]]?.add(
                            Exercise(text,img,itemDate,itemDesc,itemState,itemComm,itemId)
                        )
                    }
                }
            }else{
                tempList = ArrayMap<String, MutableList<Exercise>>()
            }
            itemList = tempList
            stateList = (requireActivity() as MainActivity).statemap
            itemList1 = (itemList[email.split("@")[0]]?.filter {
                val calendar = Calendar.getInstance()
                calendar.time = Date(it.itemDate.toLong())
                calendar.get(Calendar.DAY_OF_MONTH) == date.date &&
                        calendar.get(Calendar.MONTH) == date.month &&
                        calendar.get(Calendar.YEAR) == date.year+1900
            } as MutableList<Exercise>)
            toolbar_text.setText(param1.toString())
            plan.setText(param3?.let { itemList1.get(it).itemDesc}.toString())
            state.setText(stateList[param3?.let { itemList1.get(it).itemState }].toString())
            if(param3?.let { itemList1.get(it).itemState }!="p"){
                comment1.isVisible=true
                comment1.setText(param3?.let { itemList1.get(it).itemCom}.toString())
                comment1_text.isVisible=true
            }
            val array = arrayOf(stateList["c"], stateList["h"], stateList["f"])
            val adapterspinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)
            // Set the adapter for the Spinner
            type.adapter = adapterspinner
            val item = param3?.let { it1 -> itemList1.getOrNull(it1) }
            item?.let {
                if(it.itemState == "p"){
                    finish_btn.isVisible = true
                }
                else {
                finish_btn.isVisible = false
            }
            }
            finish_btn.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                with(builder){
                    setTitle("Завершить тренировку?")
                    setPositiveButton("Да"){dialog, which->
                        ChangeVisibility(false)
                    }
                    setNegativeButton("Нет"){dialog, which->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
            save_btn.setOnClickListener {
                val item = param3?.let { it1 -> itemList1.getOrNull(it1) }
                item?.let {
                    it.itemState = stateList.keyAt(stateList.indexOfValue(type.selectedItem.toString()))
                }
                Log.d("item", item.toString())
                Log.d("itemList1", itemList1.toString())
                Log.d("itemList", itemList.toString())
                item?.itemCom = comment.text.toString()
                comment1.setText(item?.itemCom)
                database.child("Exercise").child(email.split("@")[0])
                    .child(item?.itemId!!).child("itemCom").setValue(comment.text.toString())

                val currentUser = Firebase.auth.currentUser
                lateinit var email: String
                currentUser?.let {
                    email = it.email.toString()
                }
                database.child("Exercise").child(email.split("@")[0])
                    .child(item?.itemId!!).child("itemState").setValue(item?.itemState)

                editor.putString("exerciseList", Gson().toJson(itemList))
                editor.apply()
                state.setText(stateList[item?.itemState].toString())
                ChangeVisibility(true)
            }
        }

        /*itemList = tempList
        stateList = (requireActivity() as MainActivity).statemap
        itemList1 = (itemList[email.split("@")[0]]?.filter {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it.itemDate)
            calendar.get(Calendar.DAY_OF_MONTH) == date.date &&
                    calendar.get(Calendar.MONTH) == date.month &&
                    calendar.get(Calendar.YEAR) == date.year+1900
        } as MutableList<Exercise>)
        toolbar_text.setText(param1.toString())
        plan.setText(param3?.let { itemList1.get(it).itemDesc}.toString())
        state.setText(stateList[param3?.let { itemList1.get(it).itemState }].toString())
        if(param3?.let { itemList1.get(it).itemState }!="p"){
            comment1.isVisible=true
            comment1.setText(param3?.let { itemList1.get(it).itemCom}.toString())
            comment1_text.isVisible=true
        }
        val array = arrayOf(stateList["c"], stateList["h"], stateList["f"])
        val adapterspinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)
        // Set the adapter for the Spinner
        type.adapter = adapterspinner
        finish_btn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            with(builder){
                setTitle("Завершить тренировку?")
                setPositiveButton("Да"){dialog, which->
                    ChangeVisibility(false)
                }
                setNegativeButton("Нет"){dialog, which->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
        save_btn.setOnClickListener {
            val item = param3?.let { it1 -> itemList1.getOrNull(it1) }
            item?.let {
                it.itemState = stateList.keyAt(stateList.indexOfValue(type.selectedItem.toString()))
            }
            Log.d("item", item.toString())
            Log.d("itemList1", itemList1.toString())
            Log.d("itemList", itemList.toString())
            item?.itemCom = comment.text.toString()
            comment1.setText(item?.itemCom)

            val currentUser = Firebase.auth.currentUser
            lateinit var email: String
            currentUser?.let {
                email = it.email.toString()
            }
            database.child("Exercise").child(email.split("@")[0])
                .child(item?.itemId!!).child("itemState").setValue(item?.itemState)

            editor.putString("exerciseList", Gson().toJson(itemList))
            editor.apply()
            state.setText(stateList[item?.itemState].toString())
            ChangeVisibility(true)
        }*/
    }
@RequiresApi(Build.VERSION_CODES.Q)
fun ChangeVisibility(bool: Boolean) {
    plan.isVisible = bool
    plan_text.isVisible = bool
    state.isVisible = bool
    state_text.isVisible = bool
    val item = param3?.let { it1 -> itemList1.getOrNull(it1) }
    item?.let {
        if(it.itemState == "p"){
            finish_btn.isVisible = true
        }
        else {
            finish_btn.isVisible = false
        }
    }
    comment1.isVisible = bool
    comment1_text.isVisible = bool
    type.isVisible = !bool
    type_text.isVisible = !bool
    comment.isVisible = !bool
    comment_text.isVisible = !bool
    save_btn.isVisible = !bool
}
fun EditWindow(position: Int){
//    val builder = AlertDialog.Builder(
//        requireContext(),
//        android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen
//    )
//    val inflater = requireActivity().layoutInflater
//    val dialogLayout = inflater.inflate(R.layout.fragment_exercise_dialog, null)
//    val type = dialogLayout.findViewById<Spinner>(R.id.type_edit)
//    //val sportsmen = dialogLayout.findViewById<TextView>(R.id.sportsmen_edit)
//    val toolbar_text = dialogLayout.findViewById<TextView>(R.id.toolbar_text)
//    val edit_btn = dialogLayout.findViewById<ImageButton>(R.id.edit_button)
//    val plan = dialogLayout.findViewById<EditText>(R.id.plan_edit)
//    var dateSelected = Date()
//    toolbar_text.setText("Тренировка")
//    //date = dialogLayout.findViewById<TextView>(R.id.date_edit)
//    val array = arrayOf(stateList['c'], stateList['h'], stateList['f'])
//    val adapterspinner =
//        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)
//    // Set the adapter for the Spinner
//    fun state(boolean: Boolean){
//        type.isEnabled=boolean
//        plan.isFocusable=boolean
//        plan.isFocusableInTouchMode=boolean
//        //date.isEnabled=boolean
//    }
//    state(false)
//    type.adapter = adapterspinner
//    //sportsmen.text = param1.toString()
//    val calendar = Calendar.getInstance()
//    val year = calendar.get(android.icu.util.Calendar.YEAR)
//    val month = calendar.get(android.icu.util.Calendar.MONTH)
//    val day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
//    var tmp = (itemList[param2]?.filter {
//        val calendar = Calendar.getInstance()
//        calendar.time = it.itemDate
//        calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
//                calendar.get(Calendar.MONTH) == selectedDate.month &&
//                calendar.get(Calendar.YEAR) == selectedDate.year+1900
//    } as MutableList<Exercise>?)
//    var tmp1 = tmp?.get(position)?.itemDate
//    date.setText(
//    tmp1?.date.toString() + '.' + tmp1?.month.toString() + '.' + (tmp1?.year?.plus(
//    1900
//    )).toString()+ ' ' + tmp1?.hours.toString() + ':' + tmp1?.minutes.toString()
//    )
//    Log.d("item", tmp.toString())
//    type.setSelection(adapterspinner.getPosition(tmp?.get(position)?.text))
//    Log.d("ItemDesc", tmp?.get(position)?.itemDesc.toString())
//    plan.setText(tmp?.get(position)?.itemDesc.toString())
//
//    edit_btn.visibility= VISIBLE
//    edit_btn.setOnClickListener {
//        toolbar_text.setText("Изменение тренировки")
//        edit_btn.visibility= View.GONE
//        state(true)
//    }
//    with(builder) {
//        setPositiveButton("Сохранить") { dialog, which ->
//            val random = Random()
//            val randomNumber = random.nextInt(1000)
//            tmp?.get(position)?.text = type.selectedItem.toString()
//            tmp?.get(position)?.img = "https://picsum.photos/200?random=$randomNumber"
//            tmp?.get(position)?.itemDate = dateSelected
//            tmp?.get(position)?.itemDesc = plan.text.toString()
//            tmp?.get(position)?.itemId = "Item " + (size++).toString()
//            //(type.selectedItem.toString(), "https://picsum.photos/200?random=$randomNumber", dateSelected, plan.toString(), "Item "+(size++).toString())
//            //itemList[param2]?.add(Exercise(type.selectedItem.toString(), "https://picsum.photos/200?random=$randomNumber", dateSelected, plan.toString(), "Item "+(size++).toString()))
//            adapter = AdapterExercise(itemList[param2]?.filter {
//                val calendar = Calendar.getInstance()
//                calendar.time = it.itemDate
//                calendar.get(Calendar.DAY_OF_MONTH) == selectedDate.date &&
//                        calendar.get(Calendar.MONTH) == selectedDate.month &&
//                        calendar.get(Calendar.YEAR) == selectedDate.year + 1900
//            } as MutableList<Exercise>?)
//            adapter.notifyItemInserted(itemList[param2]?.size ?: 0)
//            recyclerView.adapter = adapter
//            setupListeners()
//            editor.putString("exerciseList", Gson().toJson(itemList))
//            editor.apply()
//            Log.d("SportsmensFragment size", adapter.itemCount.toString())
//            Log.d("SportsmensFragment elements", "Item list: $itemList")
//            edit_btn.visibility= View.VISIBLE
//            state(false)
//        }//Log.d("Main","Positive")}
//        /*setNegativeButton("Отмена") { dialog, which -> Log.d("Main", "Negative")
//            edit_btn.visibility= View.VISIBLE
//            state(false)}*/
//    }
//    builder.setView(dialogLayout)
//    builder.show()
}

    companion object {
        fun newInstance(param1: String, param2: String, param3: Int): Fragment {
            return ExerciseFragmentDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                }
            }
        }
    }
}