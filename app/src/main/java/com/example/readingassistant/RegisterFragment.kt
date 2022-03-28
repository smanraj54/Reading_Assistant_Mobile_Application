package com.example.readingassistant

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        register(view)
    }

    private fun register(view: View){
        val registerBut = view.findViewById<Button>(R.id.registerButton)
        val firstName= view.findViewById<EditText>(R.id.firstnameInput)
        val lastName= view.findViewById<EditText>(R.id.lastnameInput)
        val userName = view.findViewById<EditText>(R.id.usernameInput)
        val passWord = view.findViewById<EditText>(R.id.passwordInput)

        registerBut.setOnClickListener{
            if(TextUtils.isEmpty(firstName.text.toString())){
                firstName.setError("Please enter first name")
                return@setOnClickListener
            } else if(TextUtils.isEmpty(lastName.text.toString())) {
                lastName.setError("Please enter last name ")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(userName.text.toString())) {
                userName.setError("Please enter user name ")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(passWord.text.toString())) {
                passWord.setError("Please enter password ")
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(userName.text.toString(),passWord.text.toString())
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        val curUser = auth.currentUser
                        val curUserDb= databaseReference?.child(curUser?.uid!!)
                        curUserDb?.child("firstname")?.setValue(firstName.text.toString())
                        curUserDb?.child("lastname")?.setValue(lastName.text.toString())
                        findNavController().navigate(R.id.mainMenuFragment)
                        Toast.makeText(this.activity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this.activity, "Registration Failed, Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}