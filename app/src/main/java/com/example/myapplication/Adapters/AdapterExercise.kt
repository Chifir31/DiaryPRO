import android.icu.text.MessagePattern.ApostropheMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myapplication.R
import com.example.myapplication.data.Exercise
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.*


class AdapterExercise(private val itemList: MutableList<Exercise>?) : RecyclerView.Adapter<AdapterExercise.ItemViewHolder>() {
    private val deleteButtonsVisible = mutableSetOf<String>()
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onOpenClickListener: OnOpenClickListener? = null
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }
    interface OnOpenClickListener {
        fun onOpenClick(position: Int)
    }
    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }
    fun setOnOpenClickListener(listener: OnOpenClickListener) {
        onOpenClickListener = listener
    }
    // Create a new view holder for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_example, parent, false)
        return ItemViewHolder(view)
    }

    fun setImage(trainingName: String): Int {
        if (trainingName == "ОФП"){
            return R.drawable.baseline_ofp_24
        }
        if (trainingName == "Плавание"){
            return R.drawable.baseline_swim_24
        }
        if (trainingName == "Лыжи"){
            return R.drawable.baseline_skiing_24
        }
        if (trainingName == "Велосипед"){
            return R.drawable.baseline_bike_24
        }
        return R.drawable.baseline_running_24
    }
    // Bind the data to the views in each item view holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList?.get(position)
        if (currentItem != null) {
            holder.itemName.text = currentItem.text
        }
        val image = currentItem?.let { setImage(it.text) }
        Glide.with(holder.itemView)
            .load(image)
            .transform()
            .into(holder.itemPicture)
        //CircleCrop()
        //holder.itemPicture.setImageResource(currentItem.)
        holder.itemOpenButton.setOnClickListener {
            // Open separate window
            onOpenClickListener?.onOpenClick(position)
        }
        holder.itemDeleteButton.setOnClickListener {
            // Delete item from list and update RecyclerView
            onDeleteClickListener?.onDeleteClick(position)
        }
        holder.itemDeleteButton.visibility = if (deleteButtonsVisible.contains(getItem(position))) VISIBLE else GONE

    }
    // Return the size of the list
    override fun getItemCount() = itemList?.size ?: 0
    fun getItem(position: Int): String {
        itemList?.let {
            return it[position].itemId
        }
        return ""
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

    // Define the view holder class
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_text)
        val itemPicture: ImageView = itemView.findViewById(R.id.item_image)
        val itemOpenButton: TextView = itemView.findViewById(R.id.item_open_button)
        val itemDeleteButton: TextView = itemView.findViewById(R.id.item_delete_button)
    }
    fun removeItem(position: Int, user: String) {
        hideDeleteButton(position)
        Log.d("Check", itemList.toString())

        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }
        val database = Firebase.database.reference
        itemList?.let {
            Log.d("List Exercise", it.toString())
            it[position].itemId
            database.child("Exercise").child(user)
                .child(it[position].itemId).ref.removeValue().addOnSuccessListener {
                    // Value removed successfully
                    Log.d("S", "S")
                }.addOnFailureListener{
                    Log.d("F", "F")
                }
            Log.d("checking", "email " + user + "item " + it[position].itemId + ", " + database.child("Exercise").child(user).child(it[position].itemId).toString())
        }
        Log.d("Check1", itemList.toString())
        itemList?.removeAt(position)
        Log.d("Check1", itemList.toString())
        Log.d("Check", itemList.toString())
        notifyDataSetChanged()
    }
}
