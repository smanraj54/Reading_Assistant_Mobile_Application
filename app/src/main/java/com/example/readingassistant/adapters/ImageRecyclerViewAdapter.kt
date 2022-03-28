package com.example.readingassistant.adapters;
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.readingassistant.R
import com.example.readingassistant.model.Picture
import com.example.readingassistant.persistence.Persistence
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

public class ImageRecyclerViewAdapter(val Pictures: ArrayList<Picture>):
        RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageItem>() {

    inner class ImageItem(imageItemView: View?): RecyclerView.ViewHolder(imageItemView!!) {
        val imageView: ImageView? = imageItemView?.findViewById(R.id.image_view_item)
        var itemPosition = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.fragment_image_item, parent, false)
        return ImageItem(itemView)
    }

    override fun onBindViewHolder(holder: ImageItem, position: Int) {
        val imageView = holder.imageView
        val Picture: Picture = this.Pictures[0]
        Persistence.mainActivity.runOnUiThread(Thread{
            val url = URL(Picture.url)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connectTimeout = 5000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) {
                val inputStream: InputStream = conn.inputStream
                imageView?.setImageBitmap(BitmapFactory.decodeStream(inputStream))
            }
        })
    }

    override fun getItemCount(): Int {
        return this.Pictures.size
    }


}