package com.example.moqaida.adapters

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.moqaida.R
import com.example.moqaida.databinding.AllItemLayoutBinding
import com.example.moqaida.databinding.MyItemLayoutBinding
import com.example.moqaida.model.Items
import com.example.moqaida.views.main.HomeViewModel
import com.example.moqaida.views.main.MyItemViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "MyItemsAdapter"

// context to use it with Glide , homeViewModel to use it for pass item selected
class MyItemsAdapter(val context: Context, val myItemsViewModel: MyItemViewModel) :
    RecyclerView.Adapter<MyItemsAdapter.MyItemsViewHolder>() {


    // the adapter will change all adapter item if we give it new data(delete old one then add new one) but
    // if we use DiffUtil  it will keep old data and just change or add the new one

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Items>() {
        override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
            Log.d(TAG,"areItemsTheSame")

            return oldItem.imageUrl == newItem.imageUrl
        }
        override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this,DIFF_CALLBACK)

    // to give the differ our data (the list)
    fun submitList(list:List<Items>){
        Log.d(TAG,"submitList")

        differ.submitList(list)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemsAdapter.MyItemsViewHolder {


        val binding = MyItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyItemsViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.setOnClickListener {
            myItemsViewModel.myItemSelectedLiveData.postValue(item)
            holder.itemView.findNavController().navigate(R.id.action_myItemFragment_to_updateItemFragment)

        }

        holder.bind(item)

    }

    override fun getItemCount(): Int {

        Log.d(TAG,differ.currentList.size.toString())

        return differ.currentList.size    }


   inner class MyItemsViewHolder(val binding: MyItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item:Items){

            Log.d(TAG,item.itemName)
            binding.myItemNametextView.text = item.itemName


            Glide
                .with(context)
                .load(item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.logo)
                .into(binding.myItemImageView)


            // To delete item
            binding.deleteMyItemButton.setOnClickListener {

                    MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.alert_delete_massage)
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            // Respond to negative button press
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.delete) { dialog, _ ->
                            // Respond to positive button press
                             myItemsViewModel.deleteMyItem(item)
                            dialog.dismiss()
                        }
                        .show()
            }


        }
    }

}