package com.example.moqaida.adapters

import android.content.Context
import android.net.Uri
import android.nfc.Tag
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.moqaida.R
import com.example.moqaida.databinding.AllItemLayoutBinding
import com.example.moqaida.model.Items
import com.squareup.picasso.Picasso

private const val TAG = "ItemsAdapter"
class ItemsAdapter() :
    RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {



// the adapter will change all adapter item if we give it new data(delete old one then add new one) but
    // if we use DiffUtil  it will keep old data and just change or add the new one

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Items>() {
        override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
            Log.d(TAG,"areItemsTheSame")

            return oldItem == newItem
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

        holder.bind(item)
    }

    override fun getItemCount(): Int {
        Log.d(TAG,differ.currentList.size.toString())

        return differ.currentList.size
    }


     class ItemsViewHolder(val binding: AllItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item:Items){

            Log.d(TAG,item.itemName)
            binding.itemName.text = item.itemName

            val url = "https://firebasestorage.googleapis.com/v0/b/moqaida-z.appspot.com/o/images%2F${item.imageName}?alt=media&token=c1cbbd99-21e8-4887-b309-2388412dea6f"

            Log.d(TAG,url)

            Picasso.get().load(url).into(binding.itemImageView)


        }
    }

}