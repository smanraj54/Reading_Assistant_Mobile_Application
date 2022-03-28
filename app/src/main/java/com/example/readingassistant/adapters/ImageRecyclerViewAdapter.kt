package com.example.readingassistant.adapters;
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.readingassistant.R
import com.example.readingassistant.model.Picture
import com.example.readingassistant.persistence.Persistence
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class ImageRecyclerViewAdapter(val Pictures: ArrayList<Picture>):
        RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageItem>() {

    inner class ImageItem(imageItemView: View?): RecyclerView.ViewHolder(imageItemView!!) {
        val imageView: ImageView? = imageItemView?.findViewById(R.id.image_view_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.fragment_image_item, parent, false)
        return ImageItem(itemView)
    }

    override fun onBindViewHolder(holder: ImageItem, position: Int) {
        val imageView = holder.imageView
        val picture: Picture = Pictures[position]
        holder.imageView?.setOnClickListener {
        var text = picture.text
            lateinit var tts: TextToSpeech
            val path = Persistence.mainActivity.filesDir?.absolutePath+"/audio.mp3"

            tts = TextToSpeech(Persistence.mainActivity, object: TextToSpeech.OnInitListener{
                override fun onInit(p0: Int) {
                    if (p0 == TextToSpeech.SUCCESS) {
                        tts.language = Locale.CANADA
                        tts.synthesizeToFile(picture.text,null, File(path),"audio.mp3")
                    }
                }
            })

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    print("TTS started")
                }
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDone(utteranceId: String) {
                    Persistence.mainActivity.runOnUiThread {
                        val bundle = Bundle()
                        bundle.putString("text", picture.text)
                        bundle.putString("title", "Image text")
                        bundle.putString("audioPath", path)

                        Persistence.mainActivity.supportFragmentManager.fragments.first().childFragmentManager.fragments.first()
                            ?.setFragmentResult("mediaPlayerDocument", bundle)
                        Persistence.mainActivity.findViewById<View>(R.id.fragment_container_view)
                            .findNavController()
                            .navigate(R.id.category_fragment_to_mediaPlayerFragment)
                    }
                }

                override fun onError(utteranceId: String) {
                    Log.e("TTS error",utteranceId)
                }
            })

        }

        Persistence.mainActivity.runOnUiThread(Thread{
            val url = URL(picture.url)
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