package com.example.readingassistant.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*
import com.google.mlkit.nl.translate.TranslateRemoteModel.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.*


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

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    private var translate: Boolean = false

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

        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        progressBar.isVisible = false
        progressText.isVisible = false

        var inputImage: InputImage? = null
        setFragmentResultListener("photoURIBundle") {requestKey, bundle ->
            val photoURI = bundle.getString("photoURI")
            var bitmap: Bitmap?

            if (bundle.getString("translate") == "true") {
                this.translate = true
            }

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
        //Text to speech API
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

            override fun onDone(utteranceId: String) {
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
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun displayError(message: String) {
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
    }

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