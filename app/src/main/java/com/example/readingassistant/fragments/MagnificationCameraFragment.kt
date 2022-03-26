package com.example.readingassistant.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.Constants
import com.example.readingassistant.Constants.REQUEST_CODE_PERMISSIONS
import com.example.readingassistant.Constants.REQUIRED_PERMISSIONS
import com.example.readingassistant.R
//import com.example.magnificationcamera.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MagnificationCameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MagnificationCameraFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private lateinit var binding: ActivityMainBinding
//    private var imageCapture:ImageCapture?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionGranted()) {
            lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
            cameraProviderFuture = this.activity?.let { ProcessCameraProvider.getInstance(it) } as ListenableFuture<ProcessCameraProvider>

            val previewView = view.findViewById<PreviewView>(R.id.previewView)

            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                var preview : Preview = Preview.Builder()
                    .build()

                var cameraSelector : CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
                //Torch
                camera.cameraControl.enableTorch(true)
                //zoomIn

                val btnZoomIn = view.findViewById<Button>(R.id.btnZoomIn)
                val btnZoomOut = view.findViewById<Button>(R.id.btnZoomOut)
                val btnFlash = view.findViewById<Button>(R.id.btnFlash)
                //camera.cameraControl.enableTorch(true)

                //btnFlash.setForeground(R.drawable.flashoff)

                btnFlash.setOnClickListener(View.OnClickListener {
                    if(btnFlash.text.toString() == "Flash On"){
                        camera.cameraControl.enableTorch(true)
                    }
                    else{
                        camera.cameraControl.enableTorch(false)
                    }

                })

                var zoomLevel = 0.0
                btnZoomOut.setEnabled(false)
                btnZoomIn.setOnClickListener(View.OnClickListener {
                    if(zoomLevel<1.0) {
                        zoomLevel += 0.10
                        camera.cameraControl.setLinearZoom(zoomLevel.toFloat())
                        btnZoomOut.setEnabled(true)
                    }
                    else{
                        //disable ZoomIn Button
                        btnZoomIn.setEnabled(false)

                    }

                })
                btnZoomOut.setOnClickListener(View.OnClickListener {
                    if(zoomLevel>0) {
                        zoomLevel -= 0.10
                        camera.cameraControl.setLinearZoom(zoomLevel.toFloat())
                        btnZoomIn.setEnabled(true)
                    }
                    else{
                        //disable ZoomOut Button
                        btnZoomOut.setEnabled(false)
                    }

                })


            }, ContextCompat.getMainExecutor(this.requireActivity()))



        } else {
            print("Permissions Error")
            this.activity?.let { ActivityCompat.requestPermissions(it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS) }
        }
    }



    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all{
            this.activity?.let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1.baseContext, it
                )
            } == PackageManager.PERMISSION_GRANTED
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_magnification_camera, container, false)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MagnificationCameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MagnificationCameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}