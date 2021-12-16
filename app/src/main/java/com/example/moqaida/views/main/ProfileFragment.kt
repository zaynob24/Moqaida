package com.example.moqaida.views.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentProfileBinding
import com.example.moqaida.repositories.SHARED_PREF_FILE
import com.example.moqaida.repositories.USER_ID


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginProfileTV.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_loginFragment)
        }

        binding.buttonprofile.setOnClickListener {


             val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_FILE,Context.MODE_PRIVATE)
            binding.textprofile.text = sharedPref.getString(USER_ID,"")

        }

    }

}