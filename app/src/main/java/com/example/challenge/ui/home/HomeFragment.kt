package com.example.challenge.ui.home

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.challenge.R
import com.example.challenge.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var onReceiveFragment = true
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val buttonSent =  view.findViewById<Button>(R.id.buttonSent)
        val buttonReceived = view.findViewById<Button>(R.id.buttonReceived)

        buttonReceived.paintFlags = buttonReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        buttonSent.setOnClickListener {
            if (onReceiveFragment) {
                buttonSent.paintFlags = buttonSent.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                buttonReceived.paintFlags = buttonReceived.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                onReceiveFragment = false
            }
        }
        buttonReceived.setOnClickListener {
            if (!onReceiveFragment) {
                buttonReceived.paintFlags = buttonReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                buttonSent.paintFlags = buttonSent.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                onReceiveFragment = true
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}