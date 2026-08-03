package com.myapps.tasty_eats.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats.databinding.BuyAgainItemBinding
import com.myapps.tasty_eats.models.RecentItem

class BuyAgainAdapter(private val recentItems:ArrayList<RecentItem>,
                      private val listener: HistoryClickListener,
    private val context: Context) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>(){

    override fun onBindViewHolder(p0: BuyAgainViewHolder, p1: Int) {
        p0.bind(recentItems[p1].foodName!!,recentItems[p1].foodPrice!!,recentItems[p1].foodImage!!)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount(): Int = recentItems.size

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.buyAgainBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onClick(position)
                }
            }

        }
        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            val bitmap = decodeBase64ToBitmap(foodImage)
            binding.foodName.text = foodName
            binding.foodPrice.text = foodPrice
            binding.foodImage.setImageBitmap(bitmap)
        }
    }
    interface HistoryClickListener{
        fun onClick(position: Int)
    }

}