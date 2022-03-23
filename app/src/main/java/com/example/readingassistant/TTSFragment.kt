package com.example.readingassistant

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.readingassistant.databinding.FragmentTtsBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
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

        tts = TextToSpeech(activity, object: TextToSpeech.OnInitListener{
            override fun onInit(p0: Int) {
                if (p0 == TextToSpeech.SUCCESS) {
                    tts.language = Locale.CANADA
                }
            }
        })

        binding.speakButton.setOnClickListener {
            val textBox: EditText = binding.textBox
            val titlebox: EditText = binding.titleBox
            val text = textBox.text.toString()
            val title = titlebox.text.toString()

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            findNavController().navigate(R.id.action_TTSFragment_to_MediaPlayerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}