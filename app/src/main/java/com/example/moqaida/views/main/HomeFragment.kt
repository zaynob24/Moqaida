package com.example.moqaida.views.main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.moqaida.R
import com.example.moqaida.adapters.ItemsAdapter
import com.example.moqaida.databinding.FragmentHomeBinding


private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    private lateinit var itemRecyclerViewAdapter : ItemsAdapter
    //private lateinit var itemArrayList : ArrayList<Items>

   // private val itemsCollectionRef = Firebase.firestore.collection("items")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TO make option menu show (search icon)
        setHasOptionsMenu(true)

        progressDialog = ProgressDialog(requireActivity()).also {
            it.setTitle("Loading...")
            it.setCancelable(false)
        }
        homeViewModel.retrieveItems()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // link menu with action bar
        requireActivity().menuInflater.inflate(R.menu.main_menu,menu)    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       // itemArrayList = arrayListOf()
        itemRecyclerViewAdapter = ItemsAdapter(requireContext(),homeViewModel)
        binding.itemsrecyclerView.adapter = itemRecyclerViewAdapter


//       // getItems()
//        retrieveItems()
        observer()
    }

//--------------------------------------------------------------------------------------------------------//

//    private fun retrieveItems() = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val querySnapshot = itemsCollectionRef.get().await()
//            for(document in querySnapshot.documents) {
//                val item = document.toObject<Items>()
//                item?.let { itemArrayList.add(it) }
//
//
//                withContext(Dispatchers.Main) {
//                    Log.d(TAG, itemArrayList.toString())
//                }
//
//            }
//
//            withContext(Dispatchers.Main) {
//                itemRecyclerViewAdapter.submitList(itemArrayList)
//            }
//
//        } catch(e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//            }
//        }
//    }

//--------------------------------------------------------------------------------------------------------//
    private fun observer() {
        homeViewModel.retrieveItemsLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Log.d(TAG,it.toString())
                progressDialog.dismiss()
                itemRecyclerViewAdapter.submitList(it)
                homeViewModel.retrieveItemsLiveData.postValue(null)

            }
        })

        homeViewModel.retrieveItemsErrorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                homeViewModel.retrieveItemsErrorLiveData.postValue(null)
            }
        })    }
}