package com.example.chatme.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.R
import com.example.chatme.data.adapter.UserAdapter
import com.example.chatme.data.model.Users
import com.example.chatme.databinding.FragmentHomeBinding
import com.example.chatme.ui.chat.ChatFragment
import com.example.chatme.ui.groups.GroupsFragment
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.ui.login.LoginFragmentDirections
import com.example.chatme.ui.users.UsersFragment
import com.example.chatme.utils.Constant.ALL_USERS_SOCKET
import com.example.chatme.utils.Constant.UPDATE_ONLINE
import com.example.chatme.utils.Constant.USER_ID
import com.example.chatme.utils.Constant.USER_IS_ONLINE
import com.example.chatme.utils.SocketCreate
import com.example.chatme.utils.toastMsg
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Type


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var mSocket: Socket? = null
    private lateinit var mNavController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()
        mSocket = SocketCreate.getInstance(requireContext())!!.getSocket()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val UsersFragment = UsersFragment()
        val GroupsFragment = GroupsFragment()

        setCurrentFragment(UsersFragment)

        binding.bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.usersTab -> setCurrentFragment(UsersFragment)
                R.id.groupsTab -> setCurrentFragment(GroupsFragment)
            }
            true
        }

        GlobalScope.launch {
            updateUserOnlineStatus()
        }

        binding.barsIcon.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToListFragment()
            mNavController.navigate(action)
        }
    }

    private suspend fun updateUserOnlineStatus() {
        mSocket!!.emit(UPDATE_ONLINE, JSONObject().apply {
            put(USER_ID, LoginFragment.users.id)
            put(USER_IS_ONLINE, true)
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout, fragment)
            commit()
        }

    }

    override fun onStop() {
        super.onStop()
        mSocket!!.emit(UPDATE_ONLINE, JSONObject().apply {
            put(USER_ID, LoginFragment.users.id)
            put(USER_IS_ONLINE, false)
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.emit(UPDATE_ONLINE, JSONObject().apply {
            put(USER_ID, LoginFragment.users.id)
            put(USER_IS_ONLINE, false)
        })
        _binding = null
    }


}