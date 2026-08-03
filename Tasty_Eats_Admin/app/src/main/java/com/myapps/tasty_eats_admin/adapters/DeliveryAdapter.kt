package com.myapps.tasty_eats_admin.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats_admin.DeliveryDetailsActivity
import com.myapps.tasty_eats_admin.PendingDetailsActivity
import com.myapps.tasty_eats_admin.databinding.DileveryItemBinding
import com.myapps.tasty_eats_admin.models.DispatchedItems

class DeliveryAdapter(private val dispatchedOrders: ArrayList<DispatchedItems>,
                      private val context: Context): RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    val colorMap = mapOf("Received" to Color.GREEN, "Not Received" to Color.RED, "Pending" to Color.GRAY)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DileveryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }

    override fun getItemCount(): Int = dispatchedOrders.size

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class DeliveryViewHolder(private val binding: DileveryItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(position_old: Int) {
            binding.apply {
                customerNameText.text = dispatchedOrders[position_old].customerName
                moneyStatusText.text = dispatchedOrders[position_old].status
                moneyStatusText.setTextColor(colorMap[dispatchedOrders[position_old].status]?: Color.BLACK)
                if(dispatchedOrders[position_old].status == "Received"){
                    textDeliveryStatus.setText("Delivered")
                    statusColorCard.backgroundTintList = ColorStateList.valueOf(colorMap["Received"]?: Color.BLACK)
                }else{
                    statusColorCard.backgroundTintList = ColorStateList.valueOf(colorMap["Pending"]?: Color.BLACK)
                }
                deliveryCard.setOnClickListener {
                    val position = adapterPosition
                    val intent = Intent(context, DeliveryDetailsActivity::class.java)
                    intent.putExtra("customerName",dispatchedOrders[position].customerName)
                    intent.putExtra("customerEmail",dispatchedOrders[position].customerEmail)
                    intent.putExtra("customerAddress",dispatchedOrders[position].customerAddress)
                    intent.putExtra("customerPhone",dispatchedOrders[position].customerPhone)
                    intent.putExtra("foodName",dispatchedOrders[position].foodName)
                    intent.putExtra("foodPrice",dispatchedOrders[position].foodPrice)
                    intent.putExtra("foodQuantity",dispatchedOrders[position].foodQuantity)
                    context.startActivity(intent)
                }
            }
        }

    }
}