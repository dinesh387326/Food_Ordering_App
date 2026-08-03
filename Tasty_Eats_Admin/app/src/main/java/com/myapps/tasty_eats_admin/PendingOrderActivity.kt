package com.myapps.tasty_eats_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.myapps.tasty_eats_admin.adapters.PendingOrderAdapter
import com.myapps.tasty_eats_admin.databinding.ActivityPendingOrderBinding
import com.myapps.tasty_eats_admin.models.CurrentOrderItem
import com.myapps.tasty_eats_admin.models.DispatchedItems
import com.myapps.tasty_eats_admin.models.OrderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnAcceptBtnClickListener {

    private lateinit var binding: ActivityPendingOrderBinding
    private var pendingOrders: ArrayList<OrderItem> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var customerName: String
    private lateinit var customerAddress: String
    private lateinit var customerEmail: String
    private lateinit var customerPhone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener{
            finish()
        }

        val userId = auth.currentUser!!.uid
        val userRef = databaseReference.child("user").child(userId)

        lifecycleScope.launch(Dispatchers.IO){
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(items in snapshot.children){
                        if(items.key == "pendingOrders"){
                            pendingOrders.clear()
                            for(newItems in items.children){
                                val item = newItems.getValue(OrderItem::class.java)
                                if (item != null) {
                                    pendingOrders.add(item)
                                }
                            }
                        }
                    }

                    if(pendingOrders.isNotEmpty()){
                        Log.d("Reached", "POA Reached")
                        val adapter = PendingOrderAdapter(pendingOrders,this@PendingOrderActivity,databaseReference,userId,this@PendingOrderActivity)
                        binding.pendingRecycler.layoutManager = LinearLayoutManager(this@PendingOrderActivity)
                        binding.pendingRecycler.adapter = adapter
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PendingOrderActivity,error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    override fun onAcceptBtn(position: Int) {
        val userId = auth.currentUser!!.uid
        try{
            lifecycleScope.launch {
                val snapshot = withContext(Dispatchers.IO) {
                    databaseReference.child("user")
                        .child(pendingOrders[position].customerId!!)
                        .child("currentOrders")
                        .get()
                        .await()
                }

                for(items in snapshot.children){
                    val item = items.getValue(CurrentOrderItem::class.java)
                    if (item != null) {
                        if(item.shopId == userId && item.foodName == pendingOrders[position].foodName
                            && item.foodQuantity == pendingOrders[position].foodQuantity){
                            Toast.makeText(this@PendingOrderActivity,"C1",Toast.LENGTH_SHORT).show()
                            items.ref.child("status").setValue("Press if Received").await()
                            Toast.makeText(this@PendingOrderActivity,"C2",Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                }
                //adding item to dispatchedItems
                val snapshot2 = withContext(Dispatchers.IO) {
                    databaseReference.child("user").child(pendingOrders[position].customerId!!)
                        .get()
                        .await()
                }

                for(items in snapshot2.children){
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

                val dispatchedItem = DispatchedItems(customerName,customerAddress,customerEmail,customerPhone,pendingOrders[position].foodName,pendingOrders[position].foodPrice,
                    pendingOrders[position].foodImage,pendingOrders[position].foodQuantity,"Not Received")
                databaseReference.child("user").child(userId).child("dispatchedItems").push().setValue(dispatchedItem).await()

                // updating completedOrders and earnings of shop

                val snapshot3 = withContext(Dispatchers.IO) {
                    databaseReference.child("user").child(userId).child("pendingOrders")
                        .get()
                        .await()
                }

                for(items in snapshot3.children){
                    val item = items.getValue(OrderItem::class.java)
                    if (item != null) {
                        if(item.customerId == pendingOrders[position].customerId &&
                            item.foodName == pendingOrders[position].foodName &&
                            item.foodQuantity == pendingOrders[position].foodQuantity){
                            items.ref.removeValue().await()
                            pendingOrders.removeAt(position)
                            binding.pendingRecycler.adapter?.notifyItemRemoved(position)
                            Toast.makeText(this@PendingOrderActivity,"Order Dispatched",Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                }
        } }catch (e: Exception) {
            Toast.makeText(
                this,
                e.message ?: "Unknown error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}