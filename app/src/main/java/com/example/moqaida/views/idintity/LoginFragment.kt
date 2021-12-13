package com.example.moqaida.views.idintity

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentLoginBinding


private const val TAG = "LoginFragment"
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }

        binding= FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        observer()


        binding.loginButton.setOnClickListener {
            val email:String= binding.emailLoginTV.text.toString()
            val password:String= binding.passwordLoginTV.text.toString()

            if(email.isNotEmpty()&& password.isNotEmpty()){

                progressDialog.show()
                Log.d(TAG, "Inside if password == confirmPassword")
                loginViewModel.login(email, password)

            }
        }
    }

    private fun observer() {
        loginViewModel.loginLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), R.string.user_logged_in_successfully, Toast.LENGTH_SHORT).show()

                findNavController().popBackStack()
                loginViewModel.loginLiveData.postValue(null)
            }
        })

        loginViewModel.loginErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                loginViewModel.loginErrorLiveData.postValue(null)
            }
        })    }
}