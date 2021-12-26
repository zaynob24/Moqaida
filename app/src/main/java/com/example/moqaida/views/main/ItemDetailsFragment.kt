package com.example.moqaida.views.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.moqaida.MainActivity
import com.example.moqaida.databinding.FragmentItemDetailsBinding
import com.example.moqaida.views.dialogs.BarteringDialogFragment
import com.example.moqaida.views.dialogs.ImageDialogFragment


private const val TAG = "ItemDetailsFragment"
class ItemDetailsFragment : Fragment() {
    private lateinit var binding: FragmentItemDetailsBinding
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()


    }


    private fun observer() {
        homeViewModel.selectedItemsLiveData.observe(viewLifecycleOwner, { item ->
            item?.let {
                Log.d(TAG, item.toString())
                homeViewModel.selectedItemsLiveData.postValue(null)

                Glide
                    .with(requireContext())
                    .load(item.imageUrl)
                    .into(binding.itemDetailsimageView)

                binding.itemNameDetailsTextView.text = item.itemName
                binding.cityNameDetails.text = item.location
                binding.yearsOfUseDetails.text = item.yearsOfUse
                binding.purchasedPriceDetails.text = item.purchasedPrice
                binding.descrptionDetails.text = item.description

                binding.barteringButton.setOnClickListener {

                    val activity = requireContext() as? MainActivity
                    item?.let { item ->
                        BarteringDialogFragment(item).show(
                            activity!!.supportFragmentManager,
                            "BarteringDialogFragment"
                        )
                    }

                }

            }
        })
    }
}