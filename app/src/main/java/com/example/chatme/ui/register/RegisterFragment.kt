package com.example.chatme.ui.register

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.chatme.R
import com.example.chatme.ui.login.LoginFragment
import com.example.chatme.ui.login.LoginFragmentDirections
import com.example.chatme.utils.Constant.CONNECT_ERROR
import com.example.chatme.utils.Constant.LOG_TAG
import com.example.chatme.utils.Constant.ON_CONNECT
import com.example.chatme.utils.Constant.ON_DISCONNECT
import com.example.chatme.utils.Constant.REGISTER_ON_SOCKET
import com.example.chatme.utils.Constant.USER_EMAIL
import com.example.chatme.utils.Constant.USER_ID
import com.example.chatme.utils.Constant.USER_IS_ONLINE
import com.example.chatme.utils.Constant.USER_NAME
import com.example.chatme.utils.Constant.USER_PASSWORD
import com.example.chatme.utils.SocketCreate
import com.example.chatme.utils.toastMsg
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class RegisterFragment : Fragment() {

    lateinit var root : View
    private lateinit var mNavController: NavController
    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_register, container, false)


        root.LoginBtnFromRegister.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            mNavController.navigate(action)
        }

        mSocket = SocketCreate.getInstance(requireContext())!!.getSocket()

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            GlobalScope.launch(Dispatchers.Main) {
                Log.e(CONNECT_ERROR, "EVENT_CONNECT_ERROR: ")
            }
        }

        mSocket!!.on(Socket.EVENT_CONNECT) {
            Log.e(ON_CONNECT, "Socket Connected!")
        };

        mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            GlobalScope.launch(Dispatchers.Main) {
                Log.e(ON_DISCONNECT, "Socket onDisconnect!")
            }
        })

        mSocket!!.connect()

        val idApp = UUID.randomUUID().toString()

        mSocket!!.on(REGISTER_ON_SOCKET) { args ->
            Log.e(LOG_TAG,"${args[0]}")
            GlobalScope.launch(Dispatchers.Main) {
                if (args[0].toString() == idApp){
                    Log.e(LOG_TAG,"${args[0]}")
                }
            }
        }

        root.RegisterButton.setOnClickListener {
            val name = root.RegisterEditTextName.text
            val email = root.RegisterEditTextEmail.text
            val pass = root.RegisterEditTextPassword.text

            if(name.isEmpty()){
                root.RegisterEditTextName.setError("Enter Full Name Please")
            }else if (email.isEmpty()){
                root.RegisterEditTextEmail.setError("Enter Email Please")
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                root.RegisterEditTextEmail.setError("Enter a valid email please")
            }else if (pass!!.isEmpty()){
                root.RegisterEditTextPassword.setError("Enter Password Please")
            }else{
                val jsonObject = JSONObject()
                jsonObject.put(USER_ID,UUID.randomUUID().toString())
                jsonObject.put(USER_NAME,name.toString())
                jsonObject.put(USER_EMAIL,email.toString())
                jsonObject.put(USER_PASSWORD,pass.toString())
                jsonObject.put(USER_IS_ONLINE,false)

                mSocket!!.emit(REGISTER_ON_SOCKET,idApp,jsonObject)

                emptyFormFields()
            }

        }


        return root
    }

    private fun emptyFormFields() {
        RegisterEditTextName.setText("")
        RegisterEditTextEmail.setText("")
        RegisterEditTextPassword.setText("")

        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        mNavController.navigate(action)

        requireContext().toastMsg("Successfully Registered")
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
    }*/

}