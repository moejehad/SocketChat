package com.example.chatme.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentSplashBinding
import com.example.chatme.utils.Constant

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding : FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        Handler().postDelayed({
            val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            mNavController.navigate(action)
        }, 3000)


        return binding.root
    }


}