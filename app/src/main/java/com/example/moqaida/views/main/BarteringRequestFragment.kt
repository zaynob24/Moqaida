package com.example.moqaida.views.main

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.moqaida.R
import com.example.moqaida.adapters.ItemsAdapter
import com.example.moqaida.adapters.RequestAdapter
import com.example.moqaida.databinding.FragmentBarteringRequestBinding
import com.example.moqaida.databinding.FragmentHomeBinding

private const val TAG = "BarteringRequestFragmen"
class BarteringRequestFragment : Fragment() {

    private lateinit var binding: FragmentBarteringRequestBinding

    private val barteringRequestViewModel: BarteringRequestViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    private lateinit var requestRecyclerViewAdapter : RequestAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }
        barteringRequestViewModel.retrieveBarteringRequest()
        binding = FragmentBarteringRequestBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestRecyclerViewAdapter = RequestAdapter(requireContext(),barteringRequestViewModel)
        binding.requestRecyclerView.adapter = requestRecyclerViewAdapter


        observer()
    }


    //--------------------------------------------------------------------------------------------------------//
    private fun observer() {

        //retrieve bartering Request observer
        barteringRequestViewModel.barteringRequestLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it.toString())
                progressDialog.dismiss()
                requestRecyclerViewAdapter.submitList(it)
                barteringRequestViewModel.barteringRequestLiveData.postValue(null)

            }
        })

        barteringRequestViewModel.barteringRequestErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                barteringRequestViewModel.barteringRequestLiveData.postValue(null)
            }
        })

        //delete bartering Request observer
        barteringRequestViewModel.deleteRequestLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it)
                barteringRequestViewModel.retrieveBarteringRequest()
                barteringRequestViewModel.deleteRequestLiveData.postValue(null)
            }
        })

        //catch delete current user selected Items observer error
        barteringRequestViewModel.deleteRequestErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it)
                progressDialog.dismiss()
                barteringRequestViewModel.deleteRequestErrorLiveData.postValue(null)
            }
        })


    }
}