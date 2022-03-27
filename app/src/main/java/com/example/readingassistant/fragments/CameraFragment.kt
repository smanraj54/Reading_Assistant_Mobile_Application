package com.example.readingassistant.fragments

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.Constants.REQUEST_CODE_PERMISSIONS
import com.example.readingassistant.Constants.REQUIRED_PERMISSIONS
import com.example.readingassistant.R
import com.google.common.util.concurrent.ListenableFuture
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imageCapture: ImageCapture? = null
        if (allPermissionsGranted()) {
            lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
            cameraProviderFuture = this.activity?.let { ProcessCameraProvider.getInstance(it) } as ListenableFuture<ProcessCameraProvider>

            val previewView = view.findViewById<PreviewView>(R.id.previewView)
            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                var preview : Preview = Preview.Builder()
                    .build()

                // define camera usecases

                var cameraSelector : CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(view.display.rotation)
                    .build()

                // get camera instance
                var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageCapture)

            }, ContextCompat.getMainExecutor(this.requireActivity()))
        } else {
            print("Permissions Error")
            this.activity?.let { ActivityCompat.requestPermissions(it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS) }
            // TODO: loop back to the permissions. if the permission isnt already given, then the camera wont open right after it. I THINK. Figure it out.
        }

        // Image Capturing
        val clickPictureButton = view.findViewById<Button>(R.id.clickPictureButton)
        val outputDir = requireContext().cacheDir
        val outputFile: File = File.createTempFile("tempImageFile", ".jpg", outputDir)
        clickPictureButton.setOnClickListener(View.OnClickListener {
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(outputFile)
                .build()
            val thisActivity = this.activity?.let { it1 -> ContextCompat.getMainExecutor(it1) }
            imageCapture?.takePicture(outputOptions,
                thisActivity!!,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(error: ImageCaptureException) {
                        // TODO: error handling
                    }

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val bundle = Bundle()
                        bundle.putString("photoURI", outputFile.absolutePath)
                        bundle.putString("case", "camera")
                        setFragmentResult("photoURIBundle", bundle)
                        findNavController().navigate(R.id.action_cameraFragment_to_viewPictureFragment)
                    }
                })
        })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this.activity?.let { it1 -> ContextCompat.checkSelfPermission(it1.baseContext, it) } == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}