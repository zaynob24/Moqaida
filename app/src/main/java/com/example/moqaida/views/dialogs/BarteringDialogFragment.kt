package com.example.moqaida.views.dialogs

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.moqaida.R
import com.example.moqaida.databinding.FragmentBarteringDialogBinding
import com.example.moqaida.model.Items
import com.example.moqaida.model.Requests
import com.example.moqaida.model.Users
import com.example.moqaida.repositories.SHARED_PREF_FILE
import com.example.moqaida.repositories.USER_EMAIL
import com.example.moqaida.repositories.USER_NAME
import com.example.moqaida.repositories.USER_PHONE
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "BarteringDialogFragment"
class BarteringDialogFragment (val item:Items): DialogFragment(){

    private lateinit var binding: FragmentBarteringDialogBinding

    private lateinit var  yourItemName: String
    private lateinit var  description: String

    private lateinit var progressDialog: ProgressDialog

    val  firebaseAuth = FirebaseAuth.getInstance()

    private val barteringDialogViewModel: BarteringDialogViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Loading...")
        progressDialog.setCancelable(false)

        // To make dialog with round cornner
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner_shape)
        binding = FragmentBarteringDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLoggedInState()



        observer()

        //-----------------------------------------------------------------//

        // send Bartering Request

        binding.sendRequestButton.setOnClickListener {


            takeEntryData() // to collect items data from all fields

            if (checkEntryData()){ // to check if all field contain data and give error massage if not

                progressDialog.show()

                // send Bartering Request details to fireStore

                val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)

                // get current user information
                val currentUser = Users()
                currentUser.fullName = sharedPref.getString(USER_NAME,"").toString()
                currentUser.email = sharedPref.getString(USER_EMAIL,"").toString()
                currentUser.phoneNumber = sharedPref.getString(USER_PHONE,"").toString()

                Log.d(TAG,"Requests: ${currentUser.fullName} ,${currentUser.email},${currentUser.phoneNumber}")


                val request =(Requests(yourItemName,description,currentUser,item))

                barteringDialogViewModel.sendBarteringRequest(request)

                Log.d(TAG,"Requests: ${request.itemNameMassage} ,${request.user!!.phoneNumber},${request.item!!.itemName}")

            }else{
                Toast.makeText(requireContext(),getText(R.string.fill_required), Toast.LENGTH_SHORT).show()
            }

        }

        //-----------------------------------------------------------------//


        binding.closeBarteringButton.setOnClickListener {
            dismiss()
        }

        binding.loginTV.setOnClickListener {

            findNavController().navigate(R.id.action_ItemDetailsFragment_to_loginFragment)
            dismiss()

        }

        }

    //--------------------------------------------------------------------------------------------------//


    override fun onStart() {
        super.onStart()
         val width = (resources.displayMetrics.widthPixels * 0.95).toInt()

        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    }

    //--------------------------------------------------------------------------------------------------//


    // to collect data from all fields
     private fun takeEntryData() {
        yourItemName = binding.itemNameBarteringDialog.text.toString().trim()
        description = binding.descriptionBarteringDialog.text.toString().trim()
}

    //--------------------------------------------------------------------------------------------------//


    // to check if all field contain data and give error massage if not
    private fun checkEntryData() : Boolean {
        var isAllDataFilled  = true

        //check your Item Name
        if (yourItemName.isEmpty()|| yourItemName.isBlank()){
            binding.itemNameBarteringFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.itemNameBarteringFiled.error = null
        }


        //check description
        if (description.isEmpty()|| description.isBlank()){
            binding.descriptionBarteringFiled.error = getString(R.string.required)
            isAllDataFilled = false
        }else{
            binding.descriptionBarteringFiled.error = null
        }

        return isAllDataFilled
}

    //--------------------------------------------------------------------------------------------------//

    private fun observer() {

        // login observer
        barteringDialogViewModel.sendBarteringRequestLiveData.observe(viewLifecycleOwner, {
            it?.let {

                progressDialog.dismiss()
                Toast.makeText(requireActivity(), R.string.send_request_successfully, Toast.LENGTH_SHORT).show()


                barteringDialogViewModel.sendBarteringRequestLiveData.postValue(null)
                 dismiss()

            }
        })

        barteringDialogViewModel.sendBarteringRequestErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                barteringDialogViewModel.sendBarteringRequestErrorLiveData.postValue(null)
            }
        })

    }

    //--------------------------------------------------------------------------------------------------------------//

    private fun checkLoggedInState() {

        firebaseAuth.currentUser?.let {

            // user logged in!
            binding.barteringLayout.visibility = View.VISIBLE
            binding.barteringNotLoginLayout.visibility = View.GONE

        }?:run {
            // user are not logged in
            binding.barteringNotLoginLayout.visibility = View.VISIBLE
            binding.barteringLayout.visibility = View.GONE

        }

    }
}