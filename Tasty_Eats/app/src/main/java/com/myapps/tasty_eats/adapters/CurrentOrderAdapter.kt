package com.myapps.tasty_eats.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.myapps.tasty_eats.databinding.OrderedItemBinding
import com.myapps.tasty_eats.models.CurrentOrderItem
import com.myapps.tasty_eats.models.DispatchedItems
import com.myapps.tasty_eats.models.OrderItem
import com.myapps.tasty_eats.models.RecentItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CurrentOrderAdapter(private var orderItems: ArrayList<CurrentOrderItem>, private val databaseReference: DatabaseReference
                          , private val context: Context, private val userId: String, private val userEmail: String, private val userPhone: String,
                          private val listener: OnReceiveBtnClickListener):
    RecyclerView.Adapter<CurrentOrderAdapter.CurrentOrderViewHolder>() {
    lateinit var binding: OrderedItemBinding

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentOrderAdapter.CurrentOrderViewHolder {
        binding = OrderedItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CurrentOrderViewHolder(binding)
    }

    inner class CurrentOrderViewHolder(private val binding: OrderedItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bindAll(position_old: Int){
            val bitmap = decodeBase64ToBitmap(orderItems[position_old].foodImage!!)
            binding.orderFoodImage.setImageBitmap(bitmap)
            binding.orderFoodName.text = orderItems[position_old].foodName
            binding.orderFoodPrice.text = orderItems[position_old].foodPrice
            binding.orderQuantity.text = orderItems[position_old].foodQuantity.toString()
            binding.recievedBtn.setText(orderItems[position_old].status)

            binding.recievedBtn.setOnClickListener {
                val position = absoluteAdapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    if(binding.recievedBtn.text.toString() == "Press if Received"){
                        Toast.makeText(context,"Ex 1",Toast.LENGTH_SHORT).show()
                        listener.onReceiveBtn(position)
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    interface OnReceiveBtnClickListener {
        fun onReceiveBtn(position: Int)
    }

    override fun onBindViewHolder(
        holder: CurrentOrderViewHolder,
        position: Int
    ) {
        holder.bindAll(position)
    }
}
