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

import com.example.moqaida.databinding.FragmentSignUpBinding
import com.example.moqaida.model.Users


private const val TAG = "SignUpFragment"
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }

        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate the user to Login page (LoginFragment)
        binding.loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        observer()

        // Register
        Log.d(TAG, "Before signUp Button clicked")
        binding.signUpButton.setOnClickListener {
            val name = binding.fullNameSignUpTV.text.toString().trim()
            val phoneNumber = binding.phoneSignUpTV.text.toString().trim()
            val email: String = binding.emailSignUpTV.text.toString().trim()
            val password: String = binding.passwordSignUpTV.text.toString().trim()
            val confirmPassword = binding.confirmPasswordSignUpTV.text.toString().trim()
            Log.d(TAG, "inside signUp Button clicked")

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && phoneNumber.isNotEmpty()) {
                if (password == confirmPassword) {
                    progressDialog.show()
                    Log.d(TAG, "Inside if password == confirmPassword")
                    signUpViewModel.signUp(Users(name, email, phoneNumber), password)

                }
            }
        }
    }


    fun observer() {
        signUpViewModel.signUpLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), R.string.user_registers_successfully, Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                signUpViewModel.signUpLiveData.postValue(null)
            }
        })

        signUpViewModel.signUpErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                signUpViewModel.signUpErrorLiveData.postValue(null)
            }
        })
    }
}