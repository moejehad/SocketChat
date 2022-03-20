package com.example.chatme.ui.createGroup

import android.os.Bundle
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
import com.example.chatme.databinding.FragmentCreateGroupBinding
import com.example.chatme.ui.groups.GroupsFragmentDirections
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.utils.Constant
import com.example.chatme.utils.Constant.ADD_GROUP
import com.example.chatme.utils.Constant.ALL_USERS_SOCKET
import com.example.chatme.utils.Constant.GROUP_ID
import com.example.chatme.utils.Constant.GROUP_NAME
import com.example.chatme.utils.Constant.USER_GROUP_ID
import com.example.chatme.utils.SocketCreate
import com.example.chatme.utils.toastMsg
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class CreateGroupFragment : Fragment(R.layout.fragment_create_group) {

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private var mSocket: Socket? = null
    private lateinit var mNavController: NavController

    val usersId = ArrayList<String>()

    private val adapterUser = UserAdapter(arrayListOf(), object : UserAdapter.OnClickItem {
        override fun onClick(user: Users) {
            if (!usersId.contains(user.id)) {
                usersId.add(user.id.toString())
            }
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
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)


        usersId.add(LoginFragment.users.id.toString())
        binding.rcDataUser.layoutManager = LinearLayoutManager(requireContext())
        binding.rcDataUser.adapter = adapterUser

        GlobalScope.launch(Dispatchers.Main) {
            getUsersFromSocket()
        }

        binding.backIcon.setOnClickListener {
            val action = CreateGroupFragmentDirections.actionCreateGroupFragmentToHomeFragment()
            mNavController.navigate(action)
        }

        binding.btnAddGroup.setOnClickListener {
            saveDataIntoSocket()
        }


        return binding.root
    }

    private fun saveDataIntoSocket() {

        val nameGroup = binding.txtNameGroup.text
        val dataGroup = JSONObject()

        if (nameGroup.isEmpty()) {
            binding.txtNameGroup.setError("Enter Group Name Please")
        } else {
            dataGroup.put(GROUP_NAME, nameGroup)
            dataGroup.put(GROUP_ID, UUID.randomUUID().toString())
            val userIdJson = JSONArray()
            for (id in usersId) {
                userIdJson.put(id)
            }
            dataGroup.put(USER_GROUP_ID, userIdJson)
            mSocket!!.emit(ADD_GROUP, dataGroup)

            val action = CreateGroupFragmentDirections.actionCreateGroupFragmentToHomeFragment()
            mNavController.navigate(action)

            requireActivity().toastMsg("Group Created Successfully")

        }


    }

    private suspend fun getUsersFromSocket() {
        mSocket!!.emit(ALL_USERS_SOCKET, true)
        mSocket!!.on(ALL_USERS_SOCKET) { ars ->

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


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }


}