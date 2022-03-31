package com.example.readingassistant.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.setFragmentResult
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.google.firebase.auth.FirebaseAuth

lateinit var auth: FirebaseAuth

/**
 *
 * This class is used to create Main Menu fragment.
 * This will be the first fragment to be loaded in the main activity.
 * List of options will be presented to the user.
 *
 * @constructor Creates MainMenuFragment object
 */
class MainMenuFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * Sets initial state of the views in the fragment.
         * Adds OnCLickListener for the buttons in the Main Menu and actions related to those clicks.
         */
        super.onViewCreated(view, savedInstanceState)

        // "Read Aloud" flow execution
        view.findViewById<ImageButton>(R.id.toReadAloudButton).setOnClickListener {
            findNavController().navigate(R.id.chooseInputMethodFragment)
        }

        // "Magnificaiton" flow execution
        view.findViewById<ImageButton>(R.id.toMagnifyButton).setOnClickListener {
            findNavController().navigate(R.id.magnificationCameraFragment)
        }

        // "Translation" flow execution
        view.findViewById<ImageButton>(R.id.toTranslateButton).setOnClickListener {
            var bundle = Bundle()
            bundle.putString("translate", "true")
            setFragmentResult("actionBundle", bundle)
            findNavController().navigate(R.id.chooseInputMethodFragment)
        }

        // "Gallery" flow execution
        view.findViewById<ImageButton>(R.id.toGalleryButton).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_category_fragment)
        }

        // "Log Out" flow execution
        view.findViewById<ImageButton>(R.id.logoutBtn).setOnClickListener {
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            Toast.makeText(this.activity, "Logged-Out!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }
}