package com.example.readingassistant.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.readingassistant.R
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController

/**
 * This class is used to create ChooseInputMethodFragment.
 * This fragment will allow the user to open Camera View to capture an image or open Phone Gallery to select and image
 * @constructor Creates ChooseInputMethodFragment object
 */
class ChooseInputMethodFragment : Fragment() {

    private var navigationBundle: Bundle? = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("actionBundle") { requestKey, bundle ->
            if (bundle.getString("translate") == "true") {
                this.navigationBundle = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_input_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * Sets initial state of the views in the fragment.
         * Adds OnCLickListener for the buttons and actions related to those clicks.
         */
        super.onViewCreated(view, savedInstanceState)
        val cameraButton = view.findViewById<ImageButton>(R.id.openCameraButton)
        val galleryButton = view.findViewById<ImageButton>(R.id.openGalleryButton)

        cameraButton.setOnClickListener(View.OnClickListener {
            this.navigationBundle?.let { it1 -> setFragmentResult("actionBundle", it1) }
            findNavController().navigate(R.id.cameraFragment)
        })

        galleryButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            openGalleryResult.launch(intent)
        })
    }

    val openGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        /**
         * ActivityResultCallback would allow us to access the image selected by the user from the device gallery
         */
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoURI = result.data?.data
            this.navigationBundle?.putString("photoURI", photoURI.toString())
            this.navigationBundle?.putString("case", "gallery")
            this.navigationBundle?.let { setFragmentResult("photoURIBundle", it) }
            findNavController().navigate(R.id.action_chooseInputMethodFragment_to_viewPictureFragment)
        } else {
            displayError("Gallery could not be opened")
            this.requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    })

    private fun displayError(message: String) {
        /**
         * Displays Toast on error
         * @param message Error message to be displayed in the toast
         */
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}