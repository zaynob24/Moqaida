package com.example.moqaida.views.idintity

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.example.moqaida.model.Users
import com.example.moqaida.repositories.*
import com.google.firebase.auth.FirebaseAuth


private const val TAG = "LoginFragment"
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    private lateinit var sharedPref : SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var  email: String
    private lateinit var  password: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, avedInstanceState: Bundle?): View? {

          firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }

//        // To clear user details
        sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)
        sharedPrefEditor = sharedPref.edit()

        binding= FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupTextView.setOnClickListener {
            //findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            findNavController().navigate(R.id.action_loginFragment_to_enterPhoneFragment)

        }

        observer()


        binding.loginButton.setOnClickListener {

            takeEntryData() // to collect items data from all fields
            if (checkEntryData()){ // to check if all field contain data and give error massage if not
                progressDialog.show()

                loginViewModel.login(email, password)
            }else{
                Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun observer() {

        // login observer
        loginViewModel.loginLiveData.observe(viewLifecycleOwner, { email ->
            email?.let {

                progressDialog.dismiss()
                Toast.makeText(requireActivity(), R.string.user_logged_in_successfully, Toast.LENGTH_SHORT).show()


                loginViewModel.loginLiveData.postValue(null)
                //checkLoggedInState()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

            }
        })

        loginViewModel.loginErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                loginViewModel.loginErrorLiveData.postValue(null)
            }
        })

    }


    //------------------------------------------------------------------------------------------------------------//

    // to collect post data from all fields
    private fun takeEntryData() {

         email = binding.emailLoginTV.text.toString().trim()
         password = binding.passwordLoginTV.text.toString().trim()

    }

    //--------------------------------------------------------------------------------------------------------------//

    // to check if all field contain data and give error massage if not
    private fun checkEntryData() : Boolean {
        var isAllDataFilled = true


        //check email
        if (email.isEmpty() || email.isBlank()) {
            binding.emailLoginTextField.error = getString(R.string.required)
            isAllDataFilled = false

        } else {
                binding.emailLoginTextField.error = null
        }

        //check password
        if (password.isEmpty() || password.isBlank()) {
            binding.passwordLoginTextField.error = getString(R.string.required)
            isAllDataFilled = false
        } else {

                binding.passwordLoginTextField.error = null
        }

        return isAllDataFilled
    }

}
