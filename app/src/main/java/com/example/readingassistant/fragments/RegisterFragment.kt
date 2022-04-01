package com.example.readingassistant.fragments

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

class RegisterFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //creates a databse instance
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

        //checks that none of the fields are empty
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
            //creates a user if none of the fields are empty
            auth.createUserWithEmailAndPassword(userName.text.toString(),passWord.text.toString())
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        val curUser = auth.currentUser
                        val curUserDb= databaseReference?.child(curUser?.uid!!)
                        //saves to the database
                        curUserDb?.child("firstname")?.setValue(firstName.text.toString())
                        curUserDb?.child("lastname")?.setValue(lastName.text.toString())
                        findNavController().navigate(R.id.mainMenuFragment) //navigates to mainmenu if successful registeration
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

}