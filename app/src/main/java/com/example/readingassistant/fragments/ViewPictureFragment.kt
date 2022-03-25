package com.example.readingassistant.fragments

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.readingassistant.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewPictureFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewPictureFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_view_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var inputImage: InputImage? = null
        setFragmentResultListener("photoURIBundle") {requestKey, bundle ->
            val photoURI = bundle.getString("photoURI")
            var bitmap: Bitmap?

            if (bundle.getString("case") == "gallery") {
                val inputImageStream = context?.getContentResolver()?.openInputStream(Uri.parse(photoURI))
                bitmap = BitmapFactory.decodeStream(inputImageStream)
                inputImage = InputImage.fromBitmap(bitmap, 0)
                val imageView: ImageView = view.findViewById(R.id.imageView) as ImageView
                imageView.setImageBitmap(bitmap)
            } else { // if (bundle.getString("case") == "camera") {
                bitmap = BitmapFactory.decodeFile(photoURI)
                val ei = ExifInterface(photoURI.toString())
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                var rotatedBitmap: Bitmap? = null
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270F)
                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                    else -> rotatedBitmap = bitmap
                }

                inputImage = rotatedBitmap?.let { InputImage.fromBitmap(it, 0) }
                val imageView: ImageView = view.findViewById(R.id.imageView) as ImageView
                imageView.setImageBitmap(rotatedBitmap)
            }

        }

        val readFromImageButton = view.findViewById<Button>(R.id.readFromImageButton)
        readFromImageButton.setOnClickListener(View.OnClickListener {
            // translate api
            if (inputImage == null) {
                // handle error
            } else {
                performOCR(inputImage!!)
            }
        })
    }

    private fun performOCR(inputImage: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
            // Task completed successfully
            print("Image to Text completed")
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
        print(result.result.text)
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    } // https://stackoverflow.com/a/14066265

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewPictureFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewPictureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}