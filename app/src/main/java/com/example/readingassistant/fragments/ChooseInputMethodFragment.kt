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
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.Constants


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseInputMethodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseInputMethodFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_input_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //change
        super.onViewCreated(view, savedInstanceState)
        val cameraButton = view.findViewById<ImageButton>(R.id.openCameraButton)
        val galleryButton = view.findViewById<ImageButton>(R.id.openGalleryButton)

        cameraButton.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.cameraFragment)
//            val intent = Intent("android.media.action.IMAGE_CAPTURE")
//            openCameraResult.launch(intent)
        })

        galleryButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            openGalleryResult.launch(intent)
        })
    }

    val openCameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            // Handle the Intent
            //do stuff here
        }
    })

    val openGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoURI = result.data?.data
            val bundle = Bundle()
            bundle.putString("photoURI", photoURI.toString())
            bundle.putString("case", "gallery")
            setFragmentResult("photoURIBundle", bundle)
            findNavController().navigate(R.id.action_chooseInputMethodFragment_to_viewPictureFragment)
        } else {
            // handle error
        }
    })

    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all{
            this.activity?.let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1.baseContext, it
                )
            } == PackageManager.PERMISSION_GRANTED
        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseInputMethodFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseInputMethodFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}