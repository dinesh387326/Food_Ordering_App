package com.myapps.tasty_eats.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.myapps.tasty_eats.databinding.CartItemBinding
import com.myapps.tasty_eats.models.AllMenu
import com.myapps.tasty_eats.models.CartItem

class CartAdapter(private val cartItems: ArrayList<CartItem>,private val cartItemRef: DatabaseReference,private val
context: Context,private val listener: EmptyCartListener): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    lateinit var binding: CartItemBinding

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    inner class CartViewHolder(private val binding: CartItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(position_old: Int){
            val bitmap = decodeBase64ToBitmap(cartItems[position_old].foodImage!!)
            binding.imageView5.setImageBitmap(bitmap)
            binding.foodName.setText(cartItems[position_old].foodName)
            binding.cartPrice.setText(cartItems[position_old].foodPrice)
            binding.textQuantity.setText(cartItems[position_old].foodQuantity.toString())

            binding.buttonMinus.setOnClickListener{
                val position = absoluteAdapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    if(cartItems[position].foodQuantity!!>1){
                        //updating item quantity in database
                        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (itemSnapshot in snapshot.children) {
                                    val item = itemSnapshot.getValue(CartItem::class.java)
                                    if (item != null &&
                                        item.foodName == cartItems[position].foodName &&
                                        item.foodPrice == cartItems[position].foodPrice &&
                                        item.foodImage == cartItems[position].foodImage
                                    ) {
                                        // 🟢 Update foodQuantity field (e.g., increment by 1)
                                        //updating in array of adapter
                                        cartItems[position].foodQuantity = cartItems[position].foodQuantity!! - 1
                                        binding.textQuantity.setText(cartItems[position].foodQuantity.toString())

                                        val newQuantity = item.foodQuantity?.minus(1)
                                        itemSnapshot.ref.child("foodQuantity").setValue(newQuantity)
                                        break
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }
                }
            }
            binding.buttonPlus.setOnClickListener{
                val position = absoluteAdapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    if(cartItems[position].foodQuantity!!<10){
                        //updating item quantity in database
                        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (itemSnapshot in snapshot.children) {
                                    val item = itemSnapshot.getValue(CartItem::class.java)
                                    if (item != null &&
                                        item.foodName == cartItems[position].foodName &&
                                        item.foodPrice == cartItems[position].foodPrice &&
                                        item.foodImage == cartItems[position].foodImage
                                    ) {
                                        // 🟢 Update foodQuantity field (e.g., increment by 1)
                                        //updating in array of adapter
                                        cartItems[position].foodQuantity = cartItems[position].foodQuantity!! + 1
                                        binding.textQuantity.setText(cartItems[position].foodQuantity.toString())

                                        val newQuantity = item.foodQuantity?.plus(1)
                                        itemSnapshot.ref.child("foodQuantity").setValue(newQuantity)
                                        break
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }
                }
            }
            binding.buttonDelete.setOnClickListener {
                val position = absoluteAdapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    //deleting item from database
                    cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (itemSnapshot in snapshot.children) {
                                val item = itemSnapshot.getValue(CartItem::class.java)
                                if (item != null &&
                                    item.foodName == cartItems[position].foodName &&
                                    item.foodPrice == cartItems[position].foodPrice &&
                                    item.foodImage == cartItems[position].foodImage
                                ) {
                                    // Found the matching item, delete it using its push key
                                    //deleting from array of adapter
                                    cartItems.removeAt(position)
                                    notifyItemRemoved(position)

                                    //setting visibility of proceed button of corresponding activity to GONE
                                    if(cartItems.isEmpty()){
                                        listener.onClick()
                                    }

                                    itemSnapshot.ref.removeValue()
                                    Toast.makeText(context, "Item removed from Cart", Toast.LENGTH_SHORT).show()
                                    break
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                }
            }
        }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    interface EmptyCartListener{
        fun onClick()
    }
}