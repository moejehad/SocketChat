package com.example.chatme.ui.listNav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.databinding.FragmentListBinding

class ListFragment : Fragment(R.layout.fragment_list) {

    private var _binding : FragmentListBinding? = null
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
        _binding = FragmentListBinding.inflate(inflater, container, false)


        binding.backIcon.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToHomeFragment()
            mNavController.navigate(action)
        }


        return binding.root
    }


}