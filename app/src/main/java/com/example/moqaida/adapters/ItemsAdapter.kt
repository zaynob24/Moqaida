package com.example.moqaida.adapters

import android.content.Context
import android.net.Uri
import android.nfc.Tag
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.moqaida.R
import com.example.moqaida.databinding.AllItemLayoutBinding
import com.example.moqaida.model.Items
import com.example.moqaida.views.main.HomeViewModel
import com.squareup.picasso.Picasso

private const val TAG = "ItemsAdapter"
// context to use it with Glide , homeViewModel to use it for pass item selected
class ItemsAdapter(val context: Context ,val homeViewModel: HomeViewModel) :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {


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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ItemsViewHolder {

        Log.d(TAG,"onCreateViewHolder")

        val binding = AllItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.setOnClickListener {
            homeViewModel.selectedItemsLiveData.postValue(item)
            holder.itemView.findNavController().navigate(R.id.action_homeFragment_to_ItemDetailsFragment)

        }

        holder.bind(item)

    }

    override fun getItemCount(): Int {
        Log.d(TAG,differ.currentList.size.toString())

        return differ.currentList.size
    }


    inner class ItemsViewHolder(val binding: AllItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item:Items){

            Log.d(TAG,item.itemName)
            binding.itemName.text = item.itemName

            //val url = "https://firebasestorage.googleapis.com/v0/b/moqaida-z.appspot.com/o/images%2F1639688238968?alt=media&token=c1cbbd99-21e8-4887-b309-2388412dea6f"
            Log.d(TAG,item.imageUrl)

            Glide
                .with(context)
                .load(item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true) // to stop Cache so when add new post the list updated
                .placeholder(R.drawable.logo)
                .into(binding.itemImageView)

        }
    }

}