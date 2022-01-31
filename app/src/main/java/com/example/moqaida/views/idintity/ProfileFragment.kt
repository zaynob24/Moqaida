package com.example.moqaida.views.idintity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentProfileBinding
import com.example.moqaida.repositories.*
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()
        // Inflate the layout for this fragment
        binding= FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLoggedInState()

        val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_FILE,Context.MODE_PRIVATE)

        binding.emailAdrresProfile.text = sharedPref.getString(USER_EMAIL,"")
        binding.phoneNumberProfile.text =  sharedPref.getString(USER_PHONE,"")
        binding.nameProfile.text =  sharedPref.getString(USER_NAME,"")

        binding.loginTV.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_loginFragment)
        }

        //LogOut
        binding.logoutProfile.setOnClickListener {

            firebaseAuth.signOut()

            // clear User Info  in shared Pref
            sharedPref.edit().clear()
                .apply()
            checkLoggedInState()
            findNavController().navigate(R.id.action_profileFragment2_to_homeFragment)
        }

    }

    private fun checkLoggedInState() {

        firebaseAuth.currentUser?.let {

            // user logged in!
            binding.userInfoLayout.visibility = View.VISIBLE
            binding.userNotLoginLayout.visibility = View.INVISIBLE

        }?:run {
            // user are not logged in
            binding.userNotLoginLayout.visibility = View.VISIBLE
            binding.userInfoLayout.visibility = View.INVISIBLE

        }

    }

}