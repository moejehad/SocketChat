package com.example.chatme.ui.login

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.example.chatme.data.model.Users
import com.example.chatme.databinding.FragmentLoginBinding
import com.example.chatme.ui.splash.SplashFragmentDirections
import com.example.chatme.utils.Constant.LOGIN_EMAIL
import com.example.chatme.utils.Constant.LOGIN_PASS
import com.example.chatme.utils.Constant.LOGIN_SOCKET
import com.example.chatme.utils.Constant.LOG_TAG
import com.example.chatme.utils.SocketCreate
import com.example.chatme.utils.toastMsg
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNavController: NavController
    private var mSocket: Socket? = null
    lateinit var progressDialog: ProgressDialog
    lateinit var idApp : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNavController = findNavController()

        mSocket = SocketCreate.getInstance(requireContext())!!.getSocket()
        idApp = UUID.randomUUID().toString()

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Login ...")
        progressDialog.setCancelable(false)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)


        GlobalScope.launch {
            getDataFromSocket()
        }

        binding.LoginButton.setOnClickListener {
            loginProcess()
        }

        binding.registerBtnFromLogin.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            mNavController.navigate(action)
        }

        return binding.root
    }

    private suspend fun getDataFromSocket() {
        mSocket!!.on(LOGIN_SOCKET) { args ->
            Log.e(LOG_TAG, "${args[0]}")
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    if (args[0].toString() == idApp)
                        if (args[1].toString().toBoolean()) {
                            users = Gson().fromJson(args[2].toString(),Users::class.java)
                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                            mNavController.navigate(action)
                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                            requireContext().toastMsg("This user is not exist")
                        }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG,e.toString())
            }
        }
    }

    private fun loginProcess() {
        progressDialog.show()
        val loginEmail = binding.editTextEmail.text
        val loginPassowrd = binding.editTextPassword.text


        if (loginEmail.isEmpty()){
            progressDialog.dismiss()
            binding.editTextEmail.setError("Enter Email Please")
        }else if (Patterns.EMAIL_ADDRESS.matcher(editTextEmail.toString()).matches()){
            progressDialog.dismiss()
            binding.editTextEmail.setError("Enter a valid email please")
        }else if(loginPassowrd!!.isEmpty()){
            progressDialog.dismiss()
            binding.editTextPassword.setError("Enter Password Please")
        }else {
            val jsonObject = JSONObject()
            jsonObject.put(LOGIN_EMAIL, loginEmail)
            jsonObject.put(LOGIN_PASS, loginPassowrd)
            mSocket!!.emit(LOGIN_SOCKET, idApp, true, jsonObject)
        }

    }


    companion object {
        lateinit var users : Users
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}