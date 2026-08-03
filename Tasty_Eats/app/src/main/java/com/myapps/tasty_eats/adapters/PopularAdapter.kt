package com.myapps.tasty_eats.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.databinding.PopularItemBinding
import com.myapps.tasty_eats.models.AllMenu

class PopularAdapter(private val menuItems:ArrayList<AllMenu>,
                     private val listener: HomeClickListener,private val context: Context) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    inner class PopularViewHolder(private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.addToCart.setOnClickListener {
                if(binding.addToCart.text == "Add to Cart"){
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        listener.onClick(position)
                    }
                }
            }

        }
        val addBtn = binding.addToCart
        val imageView = binding.foodImage
        val foodTextView = binding.foodText
        val priceTextView = binding.priceText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return menuItems.size
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val menuItem = menuItems[position]
        val bitmap = decodeBase64ToBitmap(menuItem.foodImage ?: "")

        holder.foodTextView.text = menuItem.foodName
        holder.priceTextView.text = menuItem.foodPrice
        holder.imageView.setImageBitmap(bitmap)

        if(!menuItem.stocks!!){
            holder.addBtn.background = null
            holder.addBtn.setText("Out of Stock")
            val color = ContextCompat.getColor(context, R.color.red)
            holder.addBtn.setTextColor(color)
        }
        if(menuItem.stocks!!){
            holder.addBtn.setBackgroundResource(R.drawable.green_gradient_button)
            val color = ContextCompat.getColor(context, R.color.white)
            holder.addBtn.setTextColor(color)
            holder.addBtn.setText("Add to Cart")
        }
    }
    interface HomeClickListener{
        fun onClick(position: Int)
    }
}