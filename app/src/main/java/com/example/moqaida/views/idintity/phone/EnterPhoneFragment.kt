package com.example.moqaida.views.idintity.phone

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.moqaida.MainActivity
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentEnterphoneBinding


private const val TAG = "EnterPhoneFragment"
const val PHONE_ID_KEY = "phoneID"

class EnterPhoneFragment : Fragment() {
    private lateinit var binding: FragmentEnterphoneBinding

    private lateinit var  phoneNumber: String
    private lateinit var  startPhoneNumber: String // (ex:966) International Dialing Code


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEnterphoneBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // to make editText accept number only
        binding.startPhoneEnterPhoneTV.inputType = InputType.TYPE_CLASS_NUMBER
        binding.phoneEnterPhoneTV.inputType = InputType.TYPE_CLASS_NUMBER

        binding.nextButton.setOnClickListener {

            takeEntryData() // to collect items data from all fields
            if (checkEntryData()){ // to check if all field contain data and give error massage if not

                val completePhoneNumber = "+$startPhoneNumber$phoneNumber"
                val args = Bundle()
                args.putString(PHONE_ID_KEY, completePhoneNumber)
                findNavController().navigate(R.id.action_enterPhoneFragment_to_verifyPhoneFragment,args)

            }else{
                Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
            }
        }

        //------------------------------------------------------------------------------------------------------------//

        // Navigate the user to Login page (LoginFragment)
        binding.loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_enterPhoneFragment_to_loginFragment)
        }

//        //TODO ERASE
//        binding.go.setOnClickListener {
//
//            takeEntryData()
//            Log.d(TAG,"go")
//            Log.d(TAG,startPhoneNumber)
//            Log.d(TAG,phoneNumber)
//
//            val completePhoneNumber = "+$startPhoneNumber$phoneNumber"
//            Log.d(TAG,completePhoneNumber)
//
//            val args = Bundle()
//            args.putString(PHONE_ID_KEY, completePhoneNumber)
//            Log.d(TAG,completePhoneNumber)
//           findNavController().navigate(R.id.action_enterPhoneFragment_to_verifyPhoneFragment,args)
//            Log.d(TAG,"completePhoneNumber")
//
//        }

    }


//------------------------------------------------------------------------------------------------------------//

    // to collect post data from all fields
    private fun takeEntryData() {

        phoneNumber = binding.phoneEnterPhoneTV.text.toString().trim()
        startPhoneNumber = binding.startPhoneEnterPhoneTV.text.toString().trim()

    }

    //--------------------------------------------------------------------------------------------------------------//

    // to check if all field contain data and give error massage if not
    private fun checkEntryData() : Boolean {
        var isAllDataFilled = true


        //check phoneNumber
        if (phoneNumber.isEmpty() || phoneNumber.isBlank()) {
            binding.phoneEnterPhoneField.error = getString(R.string.required)
            isAllDataFilled = false

        } else {
            binding.phoneEnterPhoneField.error = null
        }

        //check startPhoneNumber
        if (startPhoneNumber.isEmpty() || startPhoneNumber.isBlank()) {
            binding.startPhoneEnterPhoneField.error = getString(R.string.required)
            isAllDataFilled = false
        } else {

            binding.startPhoneEnterPhoneField.error = null
        }

        return isAllDataFilled
    }

}