package com.myapps.tasty_eats_admin.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.myapps.tasty_eats_admin.databinding.AllitemItemBinding
import com.myapps.tasty_eats_admin.models.AllMenu
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AllItemsAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    private val foodRef: DatabaseReference
):
    RecyclerView.Adapter<AllItemsAdapter.AddItemViewHolder>() {
    private val itemQuantities = MutableList<Int>(menuList.size){1}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = AllitemItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    inner class AddItemViewHolder(private val binding: AllitemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]
                val bitmap = decodeBase64ToBitmap(menuItem.foodImage ?: "")

                foodImage.setImageBitmap(bitmap)
                foodName.text = menuItem.foodName
                foodPrice.text = menuItem.foodPrice

                deleteBtn.setOnClickListener {
                    deleteQuantity(position)
                }

                if(menuItem.stocks == true){
                    stockBtn.setText("Out of Stock")
                }else{
                    stockBtn.setText("Available")
                }

                stockBtn.setOnClickListener {
                    if(menuItem.stocks!!){
                        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(items in snapshot.children){
                                    val item = items.getValue(AllMenu::class.java)
                                    if (item != null) {
                                        if(item.stocks == true && item.foodName == menuItem.foodName){
                                            items.ref.child("stocks").setValue(false)
                                            menuList[adapterPosition].stocks = false
                                            notifyItemChanged(adapterPosition)
                                            break
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                            }

                        })
                    }else{
                        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(items in snapshot.children){
                                    val item = items.getValue(AllMenu::class.java)
                                    if (item != null) {
                                        if(item.stocks == false && item.foodName == menuItem.foodName){
                                            items.ref.child("stocks").setValue(true)
                                            menuList[adapterPosition].stocks = true
                                            notifyItemChanged(adapterPosition)
                                            break
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }

            }
        }

        private fun deleteQuantity(position: Int){
            //deleting from menu
            foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(items in snapshot.children){
                        val item = items.getValue(AllMenu::class.java)
                        if (item != null) {

                            if(item.foodName == menuList[adapterPosition].foodName){
                                items.ref.removeValue()

                                menuList.removeAt(adapterPosition)
                                itemQuantities.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)

                                break
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

}