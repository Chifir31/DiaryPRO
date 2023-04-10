import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.example.myapplication.navigation_pages.SportsmensFragmentDialog
import java.util.*


class AdapterSportsmens(private val itemList: MutableList<Item>) : RecyclerView.Adapter<AdapterSportsmens.ItemViewHolder>() {
    private val deleteButtonsVisible = mutableSetOf<String>()
    private var onDeleteClickListener: OnDeleteClickListener? = null
    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }


    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }
    // Create a new view holder for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_example, parent, false)
        return ItemViewHolder(view)
    }

    // Bind the data to the views in each item view holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.text
        Glide.with(holder.itemView)
            .load(currentItem.img)
            .transform(CircleCrop())
            .into(holder.itemPicture)
        //holder.itemPicture.setImageResource(currentItem.)
        holder.itemOpenButton.setOnClickListener {
            // Open separate window
            Log.d("TAG", itemList.size.toString())

//            val builder = AlertDialog.Builder(holder.itemView.context)
//            val inflater = LayoutInflater.from(holder.itemView.context)
//            val dialogView = inflater.inflate(R.layout.fragment_sportsments_dialog, null)
//            builder.setView(dialogView)
//            val dialog = builder.create()
//
//            // Set the title to the item name and today's date
//            val toolbar: androidx.appcompat.widget.Toolbar = dialogView.findViewById(R.id.toolbar)
//            toolbar.title = "Дневник тренировок\n"+currentItem.text
//            val todayDate = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
//            toolbar.subtitle = todayDate
//
//            // Set the cancel button to close the dialog
//            val cancelBtn: TextView = dialogView.findViewById(R.id.cancel_btn)
//            cancelBtn.setOnClickListener { dialog.dismiss() }
//            val addBtn: TextView = dialogView.findViewById(R.id.add_btn)
//            addBtn.setOnClickListener {  }
//            val recyclerView: RecyclerView = dialogView.findViewById(R.id.recycler_sportsmens_dialog)
//            itemList = (requireActivity() as MainActivity).sportsmensList
//            // Show the dialog
//            dialog.show()
            val exercise = itemList[position]
            val fragment = SportsmensFragmentDialog.newInstance(currentItem.text, currentItem.itemId)

            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_sportsmens, fragment)
                .addToBackStack(null)
                .commit()
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
        Log.d("Id: ",itemList[position].itemId)
        return itemList[position].itemId
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
        notifyItemRemoved(position)
        itemList.removeAt(position)
        notifyDataSetChanged()
    }
    //class ClipData.Item(val text: String, val imageResourceId: Int, var itemId: String)
}
