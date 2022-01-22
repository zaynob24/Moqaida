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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.*
import java.util.concurrent.TimeUnit



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
    private lateinit var phone :String


    //create an instance of FirebaseAuth and initialize it with the FirebaseAuth.getInstance() method
    var auth = FirebaseAuth.getInstance()
    //help us save the validation token and then resend token
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks :PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }

        //----------------------------------------------------------------------------------------------------//

        //set the language of the SMS text that will be sent with the setLanuageCode() method.
        auth.setLanguageCode(Locale.getDefault().language)


        //-----------------------------------------------------------------------------------------------------------//

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
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
//        binding.getVierfybutton.setOnClickListener {
//
//            phone = binding.virfyPhoneTV.text.toString()
//            Log.d(TAG,phone)
//            val options = PhoneAuthOptions.newBuilder(auth)
//                .setPhoneNumber(phone)       // Phone number to verify
//                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                .setActivity(requireActivity())                 // Activity (for callback binding)
//                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//                .build()
//            PhoneAuthProvider.verifyPhoneNumber(options)
//            Log.d(TAG,phone)
//
//        }

//        binding.virfyPhoneTVButton.setOnClickListener {
//
//            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, binding.virfyPhoneTV.text.toString())
//
//            signInWithPhoneAuthCredential(credential)
//
//        }


        //----------------------------------------------------------------------------------------------------//

        // Register
        Log.d(TAG, "Before signUp Button clicked")
        binding.signUpButton.setOnClickListener {


            takeEntryData() // to collect items data from all fields
            if (checkEntryData()){ // to check if all field contain data and give error massage if not
                progressDialog.show()

                signUpViewModel.signUp(Users(name, email, phoneNumber), password)
            }else{
                Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
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

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    //--------------------------------------------------------------------------------------------------------------//

    // to collect post data from all fields
    private fun takeEntryData() {

        name = binding.fullNameSignUpTV.text.toString().trim()
        email = binding.emailSignUpTV.text.toString().trim()
        password = binding.passwordSignUpTV.text.toString().trim()
        confirmPassword = binding.confirmPasswordSignUpTV.text.toString().trim()
        // phoneNumber = binding.phoneSignUpTV.text.toString().trim()

    }

    //--------------------------------------------------------------------------------------------------------------//

    // to check if all field contain data and give error massage if not
    private fun checkEntryData() : Boolean {
        var isAllDataFilled = true

        //check name
        if (name.isEmpty() || name.isBlank()) {
            binding.fullNameSignUpTV.error = getString(R.string.required)
            isAllDataFilled = false
        } else {
            binding.fullNameSignUpTV.error = null
        }


        //check email
        if (email.isEmpty() || email.isBlank()) {
            binding.emailSignUpTV.error = getString(R.string.required)
            isAllDataFilled = false
        } else {
            binding.emailSignUpTV.error = null
        }

//        //check phone number
//        if (phoneNumber.isEmpty() || phoneNumber.isBlank()) {
//            binding.phoneSignUpTV.error = getString(R.string.required)
//            isAllDataFilled = false
//        } else {
//            binding.phoneSignUpTV.error = null
//        }

        //check password
        if (password.isEmpty() || password.isBlank()) {
            binding.passwordSignUpTV.error = getString(R.string.required)
            isAllDataFilled = false
        } else {
            binding.passwordSignUpTV.error = null
        }

        //check confirmPassword
        if (confirmPassword.isEmpty() || confirmPassword.isBlank()) {
            binding.confirmPasswordSignUpTV.error = getString(R.string.required)
            isAllDataFilled = false
        }else if(password != confirmPassword){
            binding.confirmPasswordSignUpTV.error = getString(R.string.required)
            isAllDataFilled = false
        }else {
            binding.confirmPasswordSignUpTV.error = null
        }

        return isAllDataFilled
    }

}