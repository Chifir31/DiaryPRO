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

    // Bind the data to the views in each item view holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList?.get(position)
        if (currentItem != null) {
            holder.itemName.text = currentItem.text
        }
        Glide.with(holder.itemView)
            .load(currentItem?.img)
            .transform(CircleCrop())
            .into(holder.itemPicture)
        //holder.itemPicture.setImageResource(currentItem.)
        holder.itemOpenButton.setOnClickListener {
            // Open separate window
            Log.d("TAG", itemList?.size.toString())
            onOpenClickListener?.onOpenClick(position)
        }
        holder.itemDeleteButton.setOnClickListener {
            // Delete item from list and update RecyclerView
            Log.d("Check", itemList.toString() + " "+ currentItem.toString())
            onDeleteClickListener?.onDeleteClick(position)
        }
        Log.d("Set check", deleteButtonsVisible.toString())
        holder.itemDeleteButton.visibility = if (deleteButtonsVisible.contains(getItem(position))) VISIBLE else GONE

    }
    // Return the size of the list
    override fun getItemCount() = itemList?.size ?: 0
    fun getItem(position: Int): String {
        itemList?.let {
            Log.d("Id: ", it[position].itemId)
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
        //Log.d("Id: ",getItem(position))
        //Log.d("Set: ","$deleteButtonsVisible")
    }

    fun hideDeleteButton(position: Int) {
        deleteButtonsVisible.remove(getItem(position))
        notifyItemChanged(position)
        //Log.d("Id: ",getItem(position))
        //Log.d("Set: ","$deleteButtonsVisible")
    }

    // Define the view holder class
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_text)
        val itemPicture: ImageView = itemView.findViewById(R.id.item_image)
        val itemOpenButton: TextView = itemView.findViewById(R.id.item_open_button)
        val itemDeleteButton: TextView = itemView.findViewById(R.id.item_delete_button)
    }
    fun removeItem(position: Int) {
        hideDeleteButton(position)
        Log.d("Check", itemList.toString())

        val currentUser = Firebase.auth.currentUser
        lateinit var email: String
        currentUser?.let {
            email = it.email.toString()
        }
        val database = Firebase.database.reference
        itemList?.let {
            //it[position].itemId
            database.child("Exercise").child(email.split("@")[0])
                .child(it[position].itemId).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        itemList?.removeAt(position)
                        Log.d("Check", itemList.toString())
                        notifyDataSetChanged()
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            Log.d("Del",it[position].itemId)
        }

    }
}
