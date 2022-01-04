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
import androidx.navigation.fragment.findNavController
import com.example.moqaida.R
import com.example.moqaida.adapters.ItemsAdapter
import com.example.moqaida.adapters.MyItemsAdapter
import com.example.moqaida.databinding.FragmentMyItemBinding

private const val TAG = "MyItemFragment"
class MyItemFragment : Fragment() {

    private lateinit var binding: FragmentMyItemBinding

    private val myItemViewModel: MyItemViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    private lateinit var myItemRecyclerViewAdapter : MyItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }
        myItemViewModel.retrieveMyItems()
        binding = FragmentMyItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myItemRecyclerViewAdapter = MyItemsAdapter(requireContext(),myItemViewModel)
        binding.myItemRecyclerView.adapter = myItemRecyclerViewAdapter

        binding.addItemFloatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_myItemFragment_to_addItemFragment)

        }

        observer()
    }


    private fun observer() {

        //retrieve current user Items observer
        myItemViewModel.retrieveMyItemsLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it.toString())
                progressDialog.dismiss()
                myItemRecyclerViewAdapter.submitList(it)
                myItemViewModel.retrieveMyItemsLiveData.postValue(null)

            }
        })

        //catch retrieve current user Items error
        myItemViewModel.retrieveMyItemsErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                myItemViewModel.retrieveMyItemsErrorLiveData.postValue(null)
            }
        })

        //-----------------------------------------//
        //delete current user selected Items observer
        myItemViewModel.deleteMyItemsLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it)
                myItemViewModel.retrieveMyItems()
                myItemViewModel.deleteMyItemsLiveData.postValue(null)
            }
        })

        //catch delete current user selected Items observer error
        myItemViewModel.deleteMyItemsErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it)
                progressDialog.dismiss()
                myItemViewModel.deleteMyItemsErrorLiveData.postValue(null)
            }
        })

    }


}