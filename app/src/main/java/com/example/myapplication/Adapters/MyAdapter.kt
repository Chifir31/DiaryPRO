import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.example.myapplication.navigation_pages.SportsmensFragment


public class MyAdapter(private val itemList: MutableList<Item>) : RecyclerView.Adapter<MyAdapter.ItemViewHolder>() {
    // Create a new view holder for each item in the list
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }
    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }
    private val deleteButtonsVisible = mutableSetOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    // Bind the data to the views in each item view holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.text
        //holder.itemPicture.setImageResource(currentItem.)
        holder.itemOpenButton.setOnClickListener {
            // Open separate window
            Log.d("TAG", itemList.size.toString())
        }
        holder.itemDeleteButton.setOnClickListener {
            // Delete item from list and update RecyclerView
            onDeleteClickListener?.onDeleteClick(position)
            //removeItem(position)
            //deleteButtonsVisible.remove(position)
            //notifyItemRemoved(position)
            //holder.itemDeleteButton.visibility=GONE
        }
        holder.itemDeleteButton.visibility = if (deleteButtonsVisible.contains(getItem(position))) VISIBLE else GONE

    }
    // Return the size of the list
    override fun getItemCount() = itemList.size
    fun getItem(position: Int): String {
        return itemList[position].itemId
    }
    fun showDeleteButton(position: Int) {
        deleteButtonsVisible.add(getItem(position))
        notifyItemChanged(position)
        Log.d("Id: ",getItem(position))
        Log.d("Set: ","$deleteButtonsVisible")
    }

    fun hideDeleteButton(position: Int) {
        deleteButtonsVisible.remove(getItem(position))
        notifyItemChanged(position)
        Log.d("Id: ",getItem(position))
        Log.d("Set: ","$deleteButtonsVisible")
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
        itemList.removeAt(position)
        notifyItemRemoved(position)

    }
    //class ClipData.Item(val text: String, val imageResourceId: Int, var itemId: String)
}
