package com.example.readingassistant.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.readingassistant.R
import com.example.readingassistant.adapters.CategoryRecyclerViewAdapter
import com.example.readingassistant.model.Category
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
            val result = task.result
            val arrayList = result.value as ArrayList<*>

            for ( a in arrayList) {
                val category = CustomClassMapper.convertToCustomClass(a, Category::class.java)
                Persistence.categories.add(category)
                Log.d("A", category.toString());
            }

            val listRecyclerViewAdapter = CategoryRecyclerViewAdapter(Persistence.categories)
            recyclerView.adapter = listRecyclerViewAdapter
        }

        recyclerView.setOnClickListener { view->
            print(view)
        }
    }

}


