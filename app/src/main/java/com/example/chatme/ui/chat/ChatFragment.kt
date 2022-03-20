package com.example.chatme.ui.chat

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
import com.example.chatme.data.model.Users
import com.example.chatme.databinding.FragmentChatBinding
import com.example.chatme.ui.home.HomeFragmentDirections
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.utils.Constant.MESSAGE_TEXT
import com.example.chatme.utils.Constant.MESSAGE_TO_SOCKET
import com.example.chatme.utils.Constant.MESSAGE_USERNAME
import com.example.chatme.utils.Constant.USER_ID
import com.example.chatme.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class ChatFragment : Fragment(R.layout.fragment_chat) {

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var mSocket: Socket? = null
    private lateinit var mNavController: NavController
    private val mesAdapter = MessageAdapter(arrayListOf())
    private lateinit var user : Users
    private var idMsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()

        mSocket = SocketCreate.getInstance(requireContext())!!.getSocket()
        user = arguments?.getParcelable<Users>("User")!!
        idMsg = LoginFragment.users.id + user.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)


        getMessagesFromSocket()

        binding.msgRecyclerChat.apply {
            adapter = mesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.titleTextView.text = user.name

        binding.sendBtn.setOnClickListener {
            sendMessgae()
        }

        binding.backIcon.setOnClickListener {
            val action = ChatFragmentDirections.actionChatFragmentToHomeFragment()
            mNavController.navigate(action)
        }

        return binding.root
    }


    private fun getMessagesFromSocket() {
        mSocket!!.on(MESSAGE_TO_SOCKET) { args ->
            GlobalScope.launch(Dispatchers.Main) {
                if(args[0].toString() == idMsg || (user.id + LoginFragment.users.id) == args[0].toString()) {
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
    }

    private fun sendMessgae() {
        val jsonMessage = JSONObject()
        jsonMessage.put(MESSAGE_USERNAME, LoginFragment.users.name)
        jsonMessage.put(USER_ID, LoginFragment.users.id)
        jsonMessage.put(MESSAGE_TEXT, binding.msgEditText.text.toString())

        mSocket!!.emit(MESSAGE_TO_SOCKET, idMsg ,jsonMessage)
        binding.msgEditText.setText("")
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}