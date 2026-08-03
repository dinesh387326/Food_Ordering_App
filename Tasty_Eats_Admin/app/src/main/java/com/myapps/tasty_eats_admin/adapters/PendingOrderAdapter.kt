package com.myapps.tasty_eats_admin.adapters

import android.content.Context
import android.content.Intent
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
import com.myapps.tasty_eats_admin.PendingDetailsActivity
import com.myapps.tasty_eats_admin.databinding.PendingItemBinding
import com.myapps.tasty_eats_admin.models.CurrentOrderItem
import com.myapps.tasty_eats_admin.models.DispatchedItems
import com.myapps.tasty_eats_admin.models.OrderItem
import com.myapps.tasty_eats_admin.models.PendingItem

class PendingOrderAdapter(private val pendingOrders: ArrayList<OrderItem>, private val context: Context
,private val databaseReference: DatabaseReference, private val userId: String, private val listener: OnAcceptBtnClickListener): RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    private lateinit var customerName: String
    private lateinit var customerAddress: String
    private lateinit var customerEmail: String
    private lateinit var customerPhone: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = pendingOrders.size

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    interface OnAcceptBtnClickListener {
        fun onAcceptBtn(position: Int)
    }

    inner class PendingOrderViewHolder(private val binding: PendingItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(position_old: Int) {
            binding.apply {
                val customerRef = databaseReference.child("user").child(pendingOrders[adapterPosition].customerId!!)

                customerRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(items in snapshot.children){
                            if(items.key == "username"){
                                customerName = items.getValue(String::class.java).toString()
                            }
                            if(items.key == "email"){
                                customerEmail = items.getValue(String::class.java).toString()
                            }
                            if(items.key == "address"){
                                customerAddress = items.getValue(String::class.java).toString()
                            }
                            if(items.key == "phone"){
                                customerPhone = items.getValue(String::class.java).toString()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                    }
                })

                customerNamePendingText.text = pendingOrders[position_old].foodName
                numQuantityText.text = pendingOrders[position_old].foodQuantity.toString()
                val bitmap = decodeBase64ToBitmap(pendingOrders[position_old].foodImage!!)
                foodImageView.setImageBitmap(bitmap)

                orderAcceptedBtn.setOnClickListener{
                    val position = adapterPosition
                    if(position!= RecyclerView.NO_POSITION){
                        if(orderAcceptedBtn.text == "Accept"){
                            orderAcceptedBtn.setText("Dispatch")

                            val newRef = customerRef.child("currentOrders")

                            newRef.addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(items in snapshot.children){
                                        val item = items.getValue(CurrentOrderItem::class.java)
                                        if (item != null) {
                                            if(item.shopId == userId && item.foodName == pendingOrders[position].foodName
                                                && item.foodQuantity == pendingOrders[position].foodQuantity){
                                                items.ref.child("status").setValue("Accepted")
                                                break
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                                }
                            })
                            showToast("Order Accepted")
                        }
                        else{
                            listener.onAcceptBtn(position)
                        }
                    }
                }

                pendingCard.setOnClickListener {
                    val position = adapterPosition
                    val intent = Intent(context, PendingDetailsActivity::class.java)
                    intent.putExtra("customerName",customerName)
                    intent.putExtra("customerEmail",customerEmail)
                    intent.putExtra("customerAddress",customerAddress)
                    intent.putExtra("customerPhone",customerPhone)
                    intent.putExtra("foodName",pendingOrders[position].foodName)
                    intent.putExtra("foodPrice",pendingOrders[position].foodPrice)
                    intent.putExtra("foodQuantity",pendingOrders[position].foodQuantity)
                    context.startActivity(intent)
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }

    }
}