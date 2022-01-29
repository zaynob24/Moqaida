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
import com.example.moqaida.util.RegisterValidations
import com.example.moqaida.views.idintity.phone.PHONE_ID_KEY


private const val TAG = "SignUpFragment"
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog


    private lateinit var  name: String
    private lateinit var  email: String
    private lateinit var  password: String
    private lateinit var  confirmPassword: String
    private lateinit var phoneNumber :String

    private val validator = RegisterValidations()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        phoneNumber = arguments?.getString(PHONE_ID_KEY)!!

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }

        //----------------------------------------------------------------------------------------------------//

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


        //----------------------------------------------------------------------------------------------------//

        // Register
        Log.d(TAG, "Before signUp Button clicked")
        binding.signUpButton.setOnClickListener {


            takeEntryData() // to collect items data from all fields
            if (checkEntryData()){ // to check if all field contain data and give error massage if not
                progressDialog.show()

                Log.d(TAG,"phoneNumber: $phoneNumber ,, signUpViewModel.signUp " )
                signUpViewModel.signUp(Users(name, email, phoneNumber), password)
            }else{
                Toast.makeText(requireContext(),getText(R.string.fill_correctly), Toast.LENGTH_SHORT).show()
            }
        }
        //----------------------------------------------------------------------------------------------------//

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
//------------------------------------------------------------------------------------------------------------//

    // to collect post data from all fields
    private fun takeEntryData() {

        name = binding.fullNameSignUpTV.text.toString().trim()
        email = binding.emailSignUpTV.text.toString().trim()
        password = binding.passwordSignUpTV.text.toString().trim()
        confirmPassword = binding.confirmPasswordSignUpTV.text.toString().trim()

    }

    //--------------------------------------------------------------------------------------------------------------//

    // to check if all field contain data and give error massage if not
    private fun checkEntryData() : Boolean {
        var isAllDataFilled = true

        //check name
        if (name.isEmpty() || name.isBlank()) {
            binding.fullNameSignUpFiled.error = getString(R.string.required)
            isAllDataFilled = false
        } else {
            binding.fullNameSignUpFiled.error = null
        }


        //check email
        if (email.isEmpty() || email.isBlank()) {
            binding.emailSignUpField.error = getString(R.string.required)
            isAllDataFilled = false
            } else {
            //check email validate
            if (validator.emailIsValid(email)){
                binding.emailSignUpField.error = null

            }else{

                isAllDataFilled = false
                binding.emailSignUpField.error = getString(R.string.invalid_email)
                Log.d(TAG,"invalid_email")
            }
        }

        //check password
        if (password.isEmpty() || password.isBlank()) {
            binding.passwordSignUpField.error = getString(R.string.required)
            isAllDataFilled = false

        } else{

            if(validator.passwordIsValid(password)){
                binding.passwordSignUpField.error = null

            }else{

                //check password validate
                isAllDataFilled = false
                binding.passwordSignUpField.error = getString(R.string.invalid_password_massage)
                Log.d(TAG,"invalid_password_massage")
            }

            }


        //check confirmPassword
        if (confirmPassword.isEmpty() || confirmPassword.isBlank()) {
            binding.confirmPasswordSignUpField.error = getString(R.string.required)
            isAllDataFilled = false

        }else if(password != confirmPassword){
            binding.confirmPasswordSignUpField.error = getString(R.string.password_not_match)
            isAllDataFilled = false
            Log.d(TAG,"password_not_match")

        }else {
            binding.confirmPasswordSignUpField.error = null
        }

        return isAllDataFilled
    }

}