package com.example.chatme.ui.groupChat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatme.R
import com.example.chatme.data.adapter.MessageAdapter
import com.example.chatme.data.model.Groups
import com.example.chatme.data.model.Message
import com.example.chatme.databinding.FragmentGroupChatBinding
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_group_chat.*
import kotlinx.android.synthetic.main.fragment_group_chat.view.*
import kotlinx.android.synthetic.main.fragment_group_chat.view.msgRecyclerChat
import kotlinx.android.synthetic.main.fragment_group_chat.view.sendBtn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class GroupChatFragment : Fragment(R.layout.fragment_group_chat) {

    private var _binding : FragmentGroupChatBinding? = null
    private val binding get() = _binding!!

    private val mesAdapter = MessageAdapter(arrayListOf())
    private lateinit var mNavController: NavController
    private var mSocket: Socket? = null

    private lateinit var group: Groups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupChatBinding.inflate(inflater, container, false)

        mSocket = SocketCreate.getInstance(requireContext())!!.getSocket()
        group = arguments?.getParcelable<Groups>("group")!!


        binding.msgRecyclerChat.apply {
            adapter = mesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        mSocket!!.on("groupChat") { args ->
            GlobalScope.launch(Dispatchers.Main) {
                if (args[0].toString() == group.id) {
                    val message = Gson().fromJson<Message>(
                        args[1].toString(),
                        Message::class.java
                    )
                    mesAdapter.apply {
                        dataMessage.add(message)
                        notifyDataSetChanged()
                    }
                }
            }
        }

        binding.sendBtn.setOnClickListener {
            sendMessgae()
        }


        binding.backBtn.setOnClickListener {
            val action = GroupChatFragmentDirections.actionGroupChatFragmentToHomeFragment()
            mNavController.navigate(action)
        }

        return binding.root
    }

    private fun sendMessgae() {
        val jsonMessage = JSONObject()
        jsonMessage.put("username", LoginFragment.users.name)
        jsonMessage.put("id", LoginFragment.users.id)
        jsonMessage.put("message", msgEditText.text.toString())

        mSocket!!.emit("groupChat", group.id, jsonMessage)
        binding.msgEditText.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}