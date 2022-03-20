package com.example.chatme.ui.groups

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
import com.example.chatme.data.adapter.GroupAdapter
import com.example.chatme.data.adapter.UserAdapter
import com.example.chatme.data.model.Groups
import com.example.chatme.data.model.Users
import com.example.chatme.databinding.FragmentGroupsBinding
import com.example.chatme.ui.home.HomeFragmentDirections
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.ui.login.LoginFragmentDirections
import com.example.chatme.utils.Constant
import com.example.chatme.utils.Constant.ADD_GROUP
import com.example.chatme.utils.Constant.ALL_GROUPS
import com.example.chatme.utils.Constant.LOG_TAG
import com.example.chatme.utils.SocketCreate
import com.example.chatme.utils.toastMsg
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_groups.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class GroupsFragment : Fragment(R.layout.fragment_groups) {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private var mSocket: Socket? = null
    private lateinit var mNavController: NavController

    private val adapterGroup = GroupAdapter(arrayListOf(), object : GroupAdapter.OnClickItem {
        override fun onClick(group: Groups) {
            val action = HomeFragmentDirections.actionHomeFragmentToGroupChatFragment(group)
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
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)

        binding.rcDataGroup.layoutManager = LinearLayoutManager(requireContext())
        binding.rcDataGroup.adapter = adapterGroup

        GlobalScope.launch {
            getGroupsFromSocket()
        }

        binding.btnCreateGroup.setOnClickListener {
            try {
                val action = HomeFragmentDirections.actionHomeFragmentToCreateGroupFragment()
                mNavController.navigate(action)
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.toString())
            }

        }

        return binding.root
    }

    private suspend fun getGroupsFromSocket() {
        mSocket!!.emit(ALL_GROUPS, true)
        mSocket!!.on(ALL_GROUPS) { args ->
            GlobalScope.launch(Dispatchers.Main) {
                val groupList: Type = object : TypeToken<List<Groups>>() {}.type
                val groups = Gson().fromJson<List<Groups>>(args[0].toString(), groupList)

                var userGroups = mutableListOf<Groups>()

                groups.forEach { group ->
                    group.userId.map { id ->
                        if (LoginFragment.users.id == id){
                            userGroups.add(group)
                        }
                    }
                }

                adapterGroup.apply {
                    adapterGroup.data.clear()
                    data.addAll(userGroups)
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