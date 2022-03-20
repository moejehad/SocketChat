package com.example.chatme.ui.users

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.R
import com.example.chatme.data.adapter.UserAdapter
import com.example.chatme.data.model.Users
import com.example.chatme.databinding.FragmentUsersBinding
import com.example.chatme.ui.home.HomeFragmentDirections
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.utils.Constant
import com.example.chatme.utils.Constant.LOG_TAG
import com.example.chatme.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.coroutines.*
import java.lang.reflect.Type

class UsersFragment : Fragment(R.layout.fragment_users) {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private var mSocket: Socket? = null
    private lateinit var mNavController: NavController

    private val adapterUser = UserAdapter(arrayListOf(), object : UserAdapter.OnClickItem {
        override fun onClick(user: Users) {
            val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(user)
            mNavController.navigate(action)
        }

    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()
        mSocket = SocketCreate.getInstance(requireContext())!!.getSocket()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.UsersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.UsersRecycler.adapter = adapterUser

        GlobalScope.launch(Dispatchers.Main) {
            getUsersFromSocket()
        }


    }

    private suspend fun getUsersFromSocket() {
        mSocket!!.emit(Constant.ALL_USERS_SOCKET, true)
        mSocket!!.on(Constant.ALL_USERS_SOCKET) { ars ->

            val usersList: Type = object : TypeToken<List<Users>>() {}.type
            val userList = Gson().fromJson<List<Users>>(ars[0].toString(), usersList)

            GlobalScope.launch(Dispatchers.Main) {
                adapterUser.apply {
                    adapterUser.data.clear()
                    data.addAll(userList)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        adapterUser.data.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

