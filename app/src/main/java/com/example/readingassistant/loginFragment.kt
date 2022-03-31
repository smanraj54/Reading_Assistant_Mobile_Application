package com.example.readingassistant

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class loginFragment : Fragment() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        //checks if a user was signed in previosuly
        if(currentUser!=null){
            findNavController().navigate(R.id.mainMenuFragment)
        }
        login(view)
    }

    private fun login(view: View) {
        val loginBut = view.findViewById<Button>(R.id.loginButton)
        val userName = view.findViewById<EditText>(R.id.usernameInput)
        val passWord = view.findViewById<EditText>(R.id.passwordInput)
        val regText= view.findViewById<TextView>(R.id.registerText)
        //check that neither of the fields are empty
        loginBut.setOnClickListener {
            if (TextUtils.isEmpty(userName.text.toString())) {
                userName.setError("Please enter Email")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(passWord.text.toString())) {
                passWord.setError("Please enter Password")
                return@setOnClickListener
            }
            //checks firebase for authentication
            auth.signInWithEmailAndPassword(userName.text.toString(), passWord.text.toString())
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        //navigates to main menu if authentication was successful
                        Toast.makeText(this.activity, "Login Successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.mainMenuFragment)
                    } else{
                        Toast.makeText(this.activity, "Login Failed, Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //if user is not already registered
        regText.setOnClickListener{
            findNavController().navigate(R.id.registerFragment)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
}