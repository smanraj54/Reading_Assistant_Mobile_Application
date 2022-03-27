package com.example.readingassistant.adapters

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.readingassistant.R
import com.example.readingassistant.model.Category
import com.example.readingassistant.persistence.Persistence
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class CategoryRecyclerViewAdapter(private val categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryItem>() {

    inner class CategoryItem(categoryItemView: View?): RecyclerView.ViewHolder(categoryItemView!!) {
        val imageView: ImageView? = categoryItemView?.findViewById(R.id.item_image_view)
        val titleTextView: TextView? = categoryItemView?.findViewById(R.id.item_title)
        val descTextView: TextView? = categoryItemView?.findViewById(R.id.item_desc)
        var itemPosition = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val categoryItemView = layoutInflater.inflate(R.layout.category_fragment_item, parent, false)
        return CategoryItem(categoryItemView)
    }

    override fun onBindViewHolder(holder: CategoryItem, position: Int) {
        val category = categories[position]
        val imageView =holder.imageView
        val imagePath = category.pictrues[0].url
        Persistence.mainActivity.runOnUiThread(Thread{
            print(imagePath)
            val url = URL(imagePath)

            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connectTimeout = 5000
            conn.requestMethod = "GET"
             if (conn.responseCode == 200) {
                 val inputStream: InputStream = conn.inputStream
                 imageView?.setImageBitmap(BitmapFactory.decodeStream(inputStream))
             }
        })
        imageView?.setOnClickListener { v->
            Log.d("-------+-------", v.toString()+position)
//            findNavController().navigate(R.id.action_notesListFragment_to_createNoteFragment
        }
        holder.titleTextView?.text = category.title
        holder.descTextView?.text = category.desc
        holder.itemPosition = position
    }

    override fun getItemCount(): Int {
        return categories.size
    }


}