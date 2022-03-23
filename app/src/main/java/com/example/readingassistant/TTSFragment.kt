package com.example.readingassistant

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.databinding.FragmentTtsBinding
import java.io.File
import java.util.*

class TTSFragment : Fragment() {

    private var _binding: FragmentTtsBinding? = null
    private lateinit var tts: TextToSpeech
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //for testing purposes, to be removed
        var title = "title"
        var text = ""
        for (i in 0..50) {
            text += "sample text "
        }
        binding.textBox.setText(text)
        binding.titleBox.setText(title)
        //end testing section

        tts = TextToSpeech(activity, object: TextToSpeech.OnInitListener{
            override fun onInit(p0: Int) {
                if (p0 == TextToSpeech.SUCCESS) {
                    tts.language = Locale.CANADA
                }
            }
        })

        binding.speakButton.setOnClickListener {

            val path = activity?.filesDir?.absolutePath+"/audio.mp3"

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                }

                override fun onDone(utteranceId: String) {

                    val bundle = Bundle()
                    bundle.putString("text",text)
                    bundle.putString("title",title)
                    bundle.putString("audioPath",path)

                    setFragmentResult("mediaPlayerDocument",bundle)
                    findNavController().navigate(R.id.action_TTSFragment_to_MediaPlayerFragment)
                }

                override fun onError(utteranceId: String) {
                    //TODO:Handle error
                }
            })

            tts.synthesizeToFile(text,null, File(path),"audio.mp3")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}