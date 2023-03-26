import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

public class MyAdapter(private val itemList: MutableList<ClipData.Item>) : RecyclerView.Adapter<MyAdapter.ItemViewHolder>() {
    private var buttonPosition = RecyclerView.NO_POSITION
    // Create a new view holder for each item in the list
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
        }
        holder.itemDeleteButton.setOnClickListener {
            // Delete item from list and update RecyclerView
            removeItem(position)
            holder.itemDeleteButton.visibility=GONE
        }

    }

    // Return the size of the list
    override fun getItemCount() = itemList.size
    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)

    }

    fun showDeleteButton(holder: ItemViewHolder, position: Int) {
        buttonPosition = position
        holder.itemDeleteButton.visibility=VISIBLE
        notifyItemChanged(position)
    }

    fun hideDeleteButton(holder: ItemViewHolder) {
        if (buttonPosition != RecyclerView.NO_POSITION) {
            val previousPosition = buttonPosition
            buttonPosition = RecyclerView.NO_POSITION

            notifyItemChanged(previousPosition)
        }

    }
    // Define the view holder class
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_text)
        val itemPicture: ImageView = itemView.findViewById(R.id.item_image)
        val itemOpenButton: TextView = itemView.findViewById(R.id.item_open_button)
        val itemDeleteButton: TextView = itemView.findViewById(R.id.item_delete_button)
    }
}