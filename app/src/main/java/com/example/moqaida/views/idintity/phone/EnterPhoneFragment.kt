package com.example.moqaida.views.idintity.phone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.moqaida.MainActivity
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentEnterphoneBinding


private const val TAG = "EnterPhoneFragment"
const val PHONE_ID_KEY = "phoneID"

class EnterPhoneFragment : Fragment() {
    private lateinit var binding: FragmentEnterphoneBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterphoneBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener {

            val phoneNumber = "+${binding.startPhoneSignUpTV.text}${binding.phoneSignUpTV.text}"
//            val activity = requireContext() as? MainActivity
//
//                VerifyPhoneDialogFragment(phoneNumber).show(
//                    activity!!.supportFragmentManager,
//                    "BarteringDialogFragment"
//                )

            val args = Bundle()
            args.putString(PHONE_ID_KEY, phoneNumber)
            findNavController().navigate(R.id.action_enterPhoneFragment_to_verifyPhoneFragment,args)
        }
    }


}