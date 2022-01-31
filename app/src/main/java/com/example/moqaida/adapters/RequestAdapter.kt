package com.example.moqaida.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.moqaida.MainActivity
import com.example.moqaida.R
import com.example.moqaida.databinding.RequestItemLayoutBinding
import com.example.moqaida.model.Requests
import com.example.moqaida.views.dialogs.ContactDialogFragment
import com.example.moqaida.views.main.BarteringRequestViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


private const val TAG = "RequestAdapter"
// barteringRequestViewModel to use it for pass Request selected
class RequestAdapter(val context: Context, val barteringRequestViewModel: BarteringRequestViewModel) :
    RecyclerView.Adapter<RequestAdapter.requestViewHolder>() {

    // the adapter will change all adapter item if we give it new data(delete old one then add new one) but
    // if we use DiffUtil  it will keep old data and just change or add the new one
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Requests>() {
        override fun areItemsTheSame(oldRequests: Requests, newRequests: Requests): Boolean {
            Log.d(TAG,"areRequestsTheSame")

            return oldRequests == newRequests
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldRequests: Requests, newRequests: Requests): Boolean {
            return oldRequests == newRequests
        }

    }

    private val differ = AsyncListDiffer(this,DIFF_CALLBACK)

    // to give the differ our data (the list)
    fun submitList(list:List<Requests>){
        Log.d(TAG,"submitList")

        differ.submitList(list)
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestAdapter.requestViewHolder {

        Log.d(TAG,"onCreateViewHolder")

        val binding = RequestItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return requestViewHolder(binding)

    }

    override fun onBindViewHolder(holder: requestViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.setOnClickListener {
//            barteringRequestViewModel.selectedItemsLiveData.postValue(item)
//            holder.itemView.findNavController().navigate(R.id.action_homeFragment_to_ItemDetailsFragment)

            val activity = context as? MainActivity
            item?.let { item ->
                ContactDialogFragment(item).show(
                    activity!!.supportFragmentManager,
                    "BarteringDialogFragment"
                )
            }


        }



        holder.bind(item)
    }

    override fun getItemCount(): Int {
        Log.d(TAG,differ.currentList.size.toString())

        return differ.currentList.size    }


  inner  class requestViewHolder(val binding: RequestItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bind(request: Requests){

            Log.d(TAG,request.item!!.itemName)
            binding.yourItemNameRequest.text =request.item.itemName
            binding.thierItemNameRequest.text = request.itemNameMassage
            binding.theirNameTV.text = "${context.getString(R.string.hi_iam)} ${request.user!!.fullName}"
            binding.theirMassageTV.text = request.itemDescriptionMassage

            // To delete request
            binding.deleteRequestIcon.setOnClickListener {

                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.alert_delete_massage)
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        // Respond to negative button press
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.delete) { dialog, _ ->
                        // Respond to positive button press
                        barteringRequestViewModel.deleteBarteringRequest(request)
                        dialog.dismiss()
                    }
                    .show()
            }


        }
    }

}