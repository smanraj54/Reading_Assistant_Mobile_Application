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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    // After the view is created performing the magnification of camera view and flash control
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Checking all the required permissions if not available then asking for it
        if (allPermissionGranted()) {
            lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
            //Getting the camera provider
            cameraProviderFuture = this.activity?.let { ProcessCameraProvider.getInstance(it) } as ListenableFuture<ProcessCameraProvider>

            //Getting the camera preview object which is used in the layout
            val previewView = view.findViewById<PreviewView>(R.id.previewView)

            //Using listener of the camera provider to manipulate camera operations of the device
            cameraProviderFuture.addListener(Runnable {

                val cameraProvider = cameraProviderFuture.get()
                var preview : Preview = Preview.Builder()
                    .build()

                //selecting the default camera to open the view on device
                //by default back facing lens is used in the application
                var cameraSelector : CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                //getting camera variable from the camera provider object
                var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
                //Torch is enabled by default while opening the magnification feature
                camera.cameraControl.enableTorch(true)

                // controlling the flash with the click of a button on camera view
                flashControl(view, camera)

                //variable to track the zoom level
                var zoomLevel = 0.0

                //controlling the magnification level of the camera view
                zoomLevel = magnificationControl(view, camera, zoomLevel)

            }, ContextCompat.getMainExecutor(this.requireActivity()))



        } else {
            print("Permissions Error")
            this.activity?.let { ActivityCompat.requestPermissions(it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS) }
        }
    }


    // private function to control flash of the device on a button click
    private fun flashControl(view: View, camera: Camera){
        val btnFlash = view.findViewById<Button>(R.id.btnFlash)

        //changing the status of flash which tracking the button's status
        btnFlash.setOnClickListener(View.OnClickListener {
            if(btnFlash.text.toString() == "Flash On"){
                camera.cameraControl.enableTorch(true)
            }
            else{
                camera.cameraControl.enableTorch(false)
            }

        })
    }

    //controlling the magnification of the camera view on buttons click
    private fun magnificationControl(view: View, camera: Camera, currentZoomLevel: Double): Double{

        //two buttons are used to control the camera magnification. Zoom-in and zoom-out
        val btnZoomIn = view.findViewById<Button>(R.id.btnZoomIn)
        val btnZoomOut = view.findViewById<Button>(R.id.btnZoomOut)

        //this variable tracks the current zoom level
        var zoomLevel = currentZoomLevel

        //btnZoomOut.setEnabled(false)
        //on click of zoom in button the variable is upated and the camera view is zoomed in
        btnZoomIn.setOnClickListener(View.OnClickListener {
            //if the camera view zoom is not max then zoom in the view
            if(zoomLevel<1.0) {
                zoomLevel += 0.10
                //increasing the zoom of camera view
                camera.cameraControl.setLinearZoom(zoomLevel.toFloat())
                //zoom-out button is enabled
                btnZoomOut.setEnabled(true)
            }
            else{
                //disable ZoomIn Button
                btnZoomIn.setEnabled(false)

            }

        })


        //on click of zoom out button the variable is upated and the camera view is zoomed out
        btnZoomOut.setOnClickListener(View.OnClickListener {
            //if the camera view zoom is not minimum then zoom out the view
            if(zoomLevel>0) {
                zoomLevel -= 0.10
                //decreasing the zoom of camera view
                camera.cameraControl.setLinearZoom(zoomLevel.toFloat())
                //zoom-in button is enabled
                btnZoomIn.setEnabled(true)
            }
            else{
                //disable ZoomOut Button
                btnZoomOut.setEnabled(false)
            }

        })
        //returning the current zoom state
        return zoomLevel
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