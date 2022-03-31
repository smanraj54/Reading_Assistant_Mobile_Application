package com.example.readingassistant.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*
import com.example.readingassistant.model.Picture
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

/**
 * This class is used to create ViewPictureFragment.
 * This fragment will allow the user to view the image clicked using the camera or selected from the gallery before proceeding to the Translation or Gallery
 * @constructor Creates ViewPictureFragment object
 */
class ViewPictureFragment : Fragment() {
    private lateinit var photoToSave: Uri
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private var translate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * Sets initial state of the views in the fragment.
         * Adds OnCLickListener for the buttons in the Main Menu and actions related to those clicks.
         */
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        progressBar.isVisible = false
        progressText.isVisible = false

        var inputImage: InputImage? = null
        setFragmentResultListener("photoURIBundle") {requestKey, bundle ->
            var photoURI = bundle.getString("photoURI")
            var bitmap: Bitmap?
            photoToSave = Uri.parse(photoURI)

            if (bundle.getString("translate") == "true") {
                this.translate = true
            }

            if (bundle.getString("case") == "gallery") {
                val inputImageStream = context?.getContentResolver()?.openInputStream(Uri.parse(photoURI))
                bitmap = BitmapFactory.decodeStream(inputImageStream)
                inputImage = InputImage.fromBitmap(bitmap, 0)
                val imageView: ImageView = view.findViewById(R.id.imageView) as ImageView
                imageView.setImageBitmap(bitmap)
            } else if (bundle.getString("case") == "camera") {
                bitmap = BitmapFactory.decodeFile(photoURI)
                val ei = ExifInterface(photoURI.toString())
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                var rotatedBitmap: Bitmap? = null
                when (orientation) { // Orientation of an image needs to be set appropriately after an image is taken
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270F)
                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                    else -> rotatedBitmap = bitmap
                }

                inputImage = rotatedBitmap?.let { InputImage.fromBitmap(it, 0) }
                val imageView: ImageView = view.findViewById(R.id.imageView) as ImageView
                imageView.setImageBitmap(rotatedBitmap)
                // Citation: [13]
                try {
                    val outputDir = requireContext().cacheDir
                    val outputFile: File = File.createTempFile("tempImageFile", ".jpg", outputDir)
                   FileOutputStream(outputFile.absolutePath).use { out ->
                        rotatedBitmap!!.compress(Bitmap.CompressFormat.PNG,100,out)
                   }
                    photoToSave = outputFile.toUri()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        val readFromImageButton = view.findViewById<Button>(R.id.readFromImageButton)
        readFromImageButton.setOnClickListener(View.OnClickListener {
            if (inputImage == null) {
                displayError("The image could not be loaded")
                this.requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                readFromImageButton.isEnabled = false
                readFromImageButton.isVisible = false
                progressBar.isVisible = true
                progressText.isVisible = true
                performOCR(inputImage!!)
            }
        })
    }

    private fun performOCR(inputImage: InputImage) {
        /**
         * Performs Optical Character Recognition over an image to extract text from the image
         * Uses Google MLKit's Text Recognition APIs
         * @param inputImage Image of type InputImage Object
         */
        progressText.text = getString(R.string.ocr_progress)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                print("Image to Text completed")
                var text = visionText.text
                val thisActivity = this.requireActivity()

                // Translate
                if (translate) {
                    val options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.FRENCH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                    val frenchEnglishTranslator = Translation.getClient(options)
                    var conditions = DownloadConditions.Builder()
                        .requireWifi()
                        .build()
                    frenchEnglishTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            translateNow(frenchEnglishTranslator, text)
                        }
                        .addOnFailureListener { exception ->
                            displayError("Translation failed")
                            processTextToSpeech(text)
                        }
                } else {
                    processTextToSpeech(text)
                }
            }.addOnFailureListener { e ->
                displayError("Text extraction from image failed")
                this.requireActivity().supportFragmentManager.popBackStackImmediate()
            }
    }

    fun translateNow(translator: Translator, text: String) {
        /**
         * Translated the text from one language to another, as specified in the TranslationModel object
         * Uses Google MLKit's Translation APIs
         * @param translator Translator object which will process the request
         * @param text String to be translated
         */
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                processTextToSpeech(translatedText)
            }
            .addOnFailureListener { exception ->
                displayError("Translation failed")
                processTextToSpeech(text)
            }
    }

    fun processTextToSpeech(text: String) {
        /**
         * Converts text to synthetic speech
         * Uses Android's Text-to-Speech TTS APIs
         * @param text String to be converted to speech
         */
        progressText.text = getString(R.string.tts_progress)
        lateinit var tts: TextToSpeech
        val path = activity?.filesDir?.absolutePath+"/audio.mp3"

        tts = TextToSpeech(activity, object: TextToSpeech.OnInitListener{
            override fun onInit(p0: Int) {
                if (p0 == TextToSpeech.SUCCESS) {
                    tts.language = Locale.CANADA
                    tts.synthesizeToFile(text,null, File(path),"audio.mp3")
                }
            }
        })

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                print("TTS started")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDone(utteranceId: String) {
                val database = Firebase.database.reference
                val storage = FirebaseStorage.getInstance().reference.child(LocalDateTime.now().toString()+".png")
                val uploadPhoto = storage.putFile(photoToSave)
                uploadPhoto.addOnSuccessListener {
                    it.metadata?.reference?.downloadUrl?.addOnCompleteListener { task ->
                        val url = task.result.toString()
                        val picture = Picture("","",url,text)
                        val category = database.child("categorys/1/pictrues").push().setValue(picture)                         }
                }.addOnFailureListener {
                    Log.e("Image save failure",it.toString())
                }
                activity?.runOnUiThread {
                    val bundle = Bundle()
                    bundle.putString("text", text)
                    bundle.putString("title", "Image text")
                    bundle.putString("audioPath", path)

                    setFragmentResult("mediaPlayerDocument", bundle)
                    findNavController().navigate(R.id.action_viewPictureFragment_to_mediaPlayerFragment)
                }
            }

            override fun onError(utteranceId: String) {
                Log.e("TTS error",utteranceId)
                displayError("Text-to-Speech failed")
                activity?.supportFragmentManager?.popBackStackImmediate()
            }
        })
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        /**
        * Rotates the input bitmap
        * Citation: [13]
        * @param source Image bitmap which has to be rotated
         * @param angle Angle with which the bitmap is to be rotated
         */
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun displayError(message: String) {
        /**
        * Displays Toast on error
        * @param message Error message to be displayed in the toast
        */
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
    }

}