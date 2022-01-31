package com.example.moqaida.views.dialogs

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.moqaida.R
import com.example.moqaida.databinding.ContactDialogLayoutBinding
import com.example.moqaida.model.Requests

import com.example.moqaida.views.main.BarteringRequestViewModel
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.moqaida.repositories.SHARED_PREF_FILE
import com.example.moqaida.repositories.USER_NAME
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import java.net.URLEncoder


private const val TAG = "ContactDialogFragment"
class ContactDialogFragment (val requests: Requests): DialogFragment(){

    private lateinit var binding: ContactDialogLayoutBinding

   // private lateinit var  yourItemName: String
    //private lateinit var  description: String

    private lateinit var progressDialog: ProgressDialog
    val  firebaseAuth = FirebaseAuth.getInstance()

   // private val barteringRequestViewModel: BarteringRequestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Loading...")
        progressDialog.setCancelable(false)

        // To make dialog with round cornner
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner_shape)
        binding = ContactDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.descriptionBarteringDialog.setText("${requests.itemNameMassage}\n${getString(R.string.hi_iam)} ${requests.user?.fullName}\n${requests.itemDescriptionMassage}")

        val sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)

        //-----------------------------------------------------------------//

        // contact via whatsapp
        binding.whatsappIcon.setOnClickListener {

            val packageManager = requireContext().packageManager
            val i = Intent(Intent.ACTION_VIEW)

            //phone formate
            //966576269839

                //message Example :
                 /*
                 Hi,I'm zainab
                 I read your message " phone
                  */
            try {
                val url =
                    "https://api.whatsapp.com/send?phone=" + requests.user!!.phoneNumber + "&text=" + URLEncoder.encode(
                        "${getString(R.string.hi_iam)} ${sharedPref.getString(USER_NAME,"").toString()}" +
                                "\n${getString(R.string.i_read_your_message)}\n\"${requests.itemNameMassage} , ${requests.itemDescriptionMassage}\" \n" ,
                        "UTF-8"
                    )
                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                if (i.resolveActivity(packageManager) != null) {
                    requireContext().startActivity(i)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        // contact via email
        binding.emailIcon.setOnClickListener {

            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", requests.user!!.email, null
                )
            )

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, requests.itemNameMassage)
            emailIntent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.hi_iam)} ${sharedPref.getString(USER_NAME,"").toString()}" +
            "\n${getString(R.string.i_read_your_message)}\n\"${requests.itemNameMassage} , ${requests.itemDescriptionMassage}\" \n")
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        //-----------------------------------------------------------------//


        binding.closeImageButton.setOnClickListener {
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

}