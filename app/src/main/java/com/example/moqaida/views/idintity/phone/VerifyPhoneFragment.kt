package com.example.moqaida.views.idintity.phone

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentVierfyPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG = "VerifyPhoneFragment"
class VerifyPhoneFragment : Fragment() {


    private lateinit var binding: FragmentVierfyPhoneBinding
    private lateinit var progressDialog: ProgressDialog

    val phoneNumber = "+966548048523"
    //private lateinit var phoneNumber : String

    //create an instance of FirebaseAuth and initialize it with the FirebaseAuth.getInstance() method
    var auth = FirebaseAuth.getInstance()
    //help us save the validation token and then resend token
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        phoneNumber = arguments?.getString(PHONE_ID_KEY)!!
//
//        Log.d(TAG,"see:"+ phoneNumber)

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Loading...")
        progressDialog.setCancelable(false)

        //--------------------------------------------------------------------------------------------------------------------//

          //TODO------------------------------

        //set the language of the SMS text that will be sent with the setLanuageCode() method.
        auth.setLanguageCode(Locale.getDefault().language)


        //-----------------------------------------------------------------------------------------------------------//

//        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                // This callback will be invoked in two situations:
//                // 1 - Instant verification. In some cases the phone number can be instantly
//                //     verified without needing to send or enter a verification code.
//                // 2 - Auto-retrieval. On some devices Google Play services can automatically
//                //     detect the incoming verification SMS and perform verification without
//                //     user action.
//                Log.d(TAG, "onVerificationCompleted:$credential")
//            }
//
//            override fun onVerificationFailed(e: FirebaseException) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.d(TAG, "onVerificationFailed", e)
//
//                if (e is FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e is FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }
//
//                // Show a message and update the UI
//            }
//
//            override fun onCodeSent(
//                verificationId: String,
//                token: PhoneAuthProvider.ForceResendingToken
//            ) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")
//
//                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
//            }
//        }


//---------------------------------------------------------------------------------------------------------------------------//
        binding = FragmentVierfyPhoneBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //----------------------------------------------------------------------------------------------------//

        //TODO------------------------------

//        Log.d(TAG,phoneNumber)
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumber)       // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(requireActivity())                 // Activity (for callback binding)
//            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//        Log.d(TAG,phoneNumber)
//        //-----------------------------------------------------------------//


        //TODO------------------------------


        binding.virfyPhoneTVButton.setOnClickListener {

            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, binding.virfyPhoneTV.text.toString())

            signInWithPhoneAuthCredential(credential)

        }

        //-----------------------------------------------------------------//

    }

    //--------------------------------------------------------------------------------------------------//

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    findNavController().navigate(R.id.action_verifyPhoneFragment_to_signUpFragment)


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                        Toast.makeText(requireContext(), "wrong verification code", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
            }
    }

    //--------------------------------------------------------------------------------------------------//

}