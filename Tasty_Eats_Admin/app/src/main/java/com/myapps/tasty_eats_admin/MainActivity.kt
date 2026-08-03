package com.myapps.tasty_eats_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.myapps.tasty_eats_admin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser!!.uid
        val userRef = databaseReference.child("user").child(userId)
        var countPendingOrders = 0;

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(items in snapshot.children){
                    if(items.key == "pendingOrders"){
                        for(newItems in items.children){
                            countPendingOrders++
                            binding.textPending.setText(countPendingOrders.toString())
                        }
                    }
                    if(items.key == "earnings"){
                        binding.textEarnings.setText(items.getValue(Int::class.java).toString())
                    }
                    if(items.key == "completedOrders"){
                        binding.textCompletedOrder.setText(items.getValue(Int::class.java).toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        })



        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }
        binding.dispatchOrderCard.setOnClickListener {
            val intent = Intent(this, OutForDileveryActivity::class.java)
            startActivity(intent)
        }
        binding.profileCard.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }
        binding.pendingOrderCard.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        binding.logOutCard.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}