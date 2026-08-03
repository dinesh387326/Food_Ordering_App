package com.myapps.tasty_eats_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.myapps.tasty_eats_admin.adapters.DeliveryAdapter
import com.myapps.tasty_eats_admin.databinding.ActivityOutForDileveryBinding
import com.myapps.tasty_eats_admin.models.DispatchedItems
import com.myapps.tasty_eats_admin.models.OrderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OutForDileveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutForDileveryBinding
    //Firebase
    private var dispatchedOrders: ArrayList<DispatchedItems> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutForDileveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser!!.uid
        val userRef = databaseReference.child("user").child(userId)

        lifecycleScope.launch(Dispatchers.IO){
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dispatchedOrders.clear()
                    for(items in snapshot.children){
                        if(items.key == "dispatchedItems"){
                            for(newItems in items.children){
                                val item = newItems.getValue(DispatchedItems::class.java)
                                if (item != null) {
                                    if(item.status == "Not Received"){
                                        dispatchedOrders.add(item)
                                    }
                                }
                            }
                        }
                    }

                    for(items in snapshot.children){
                        if(items.key == "dispatchedItems"){
                            for(newItems in items.children){
                                val item = newItems.getValue(DispatchedItems::class.java)
                                if (item != null) {
                                    if(item.status != "Not Received"){
                                        dispatchedOrders.add(item)
                                    }
                                }
                            }
                        }
                    }

                    if(dispatchedOrders.isNotEmpty()){
                        val adapter = DeliveryAdapter(dispatchedOrders,this@OutForDileveryActivity)
                        binding.deliveryRecycler.layoutManager = LinearLayoutManager(this@OutForDileveryActivity)
                        binding.deliveryRecycler.adapter = adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@OutForDileveryActivity,error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.backButton.setOnClickListener {
            finish()
        }

    }
}