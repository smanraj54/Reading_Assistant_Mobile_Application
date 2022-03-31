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
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.example.readingassistant.fragments.MainMenuFragment
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [loginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class loginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment loginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            loginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}