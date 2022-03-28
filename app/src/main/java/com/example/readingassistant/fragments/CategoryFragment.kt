package com.example.readingassistant.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.example.readingassistant.adapters.ImageRecyclerViewAdapter
import com.example.readingassistant.model.Category
import com.example.readingassistant.model.Picture
import com.example.readingassistant.persistence.Persistence
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CategoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.category_fragment_list, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.category_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val database = Firebase.database
        val categories = database.getReference("categorys")
        categories.get().addOnCompleteListener{task ->
            if (!task.isSuccessful) return@addOnCompleteListener;
            val result = task.result.children

            for ( r in result) {
                println(r.toString())
                val category = CustomClassMapper.convertToCustomClass(r.value, Category::class.java)
                Persistence.categories.add(category)
            }
            var pictureList: ArrayList<Picture> = ArrayList()
            for (p in Persistence.categories[0].pictrues.entries){
                pictureList.add(p.value)
            }
            val listRecyclerViewAdapter = ImageRecyclerViewAdapter(pictureList)
            recyclerView.adapter = listRecyclerViewAdapter
        }

    }

}


