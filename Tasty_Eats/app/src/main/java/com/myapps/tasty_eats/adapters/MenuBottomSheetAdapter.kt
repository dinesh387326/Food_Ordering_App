package com.myapps.tasty_eats.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.databinding.SearchItemBinding

class MenuBottomSheetAdapter(private val menuItems: MutableList<String>, private val menuItemPrice: MutableList<String>
                              , private val menuImages: MutableList<String>,private val menuStocks: MutableList<Boolean>
                              , private val listener: MenuClickListener,private val context: Context):  RecyclerView.Adapter<MenuBottomSheetAdapter.MenuBottomViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuBottomViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuBottomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: MenuBottomViewHolder, position: Int) {
       holder.bind(position)

    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    inner class MenuBottomViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.addToCart2.setOnClickListener {
                if(binding.addToCart2.text == "Add to Cart"){
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        listener.onClick(position)
                    }
                }
            }
        }
        fun bind(position: Int){
            binding.apply {
                val bitmap = decodeBase64ToBitmap(menuImages[position])
                menuImage.setImageBitmap(bitmap)
                menuFood.text = menuItems[position]
                menuPrice.text = menuItemPrice[position]

                if(!menuStocks[position]!!){
                    addToCart2.background = null
                    addToCart2.setText("Out of Stock")
                    val color = ContextCompat.getColor(context, R.color.red)
                    addToCart2.setTextColor(color)
                }
                if(menuStocks[position]!!){
                    addToCart2.setBackgroundResource(R.drawable.green_gradient_button)
                    val color = ContextCompat.getColor(context, R.color.white)
                    addToCart2.setTextColor(color)
                    addToCart2.setText("Add to Cart")
                }

            }
        }

    }
    interface MenuClickListener{
        fun onClick(position: Int)
    }

}