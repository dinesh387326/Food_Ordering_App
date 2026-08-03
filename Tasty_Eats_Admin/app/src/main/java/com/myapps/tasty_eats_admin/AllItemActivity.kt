package com.myapps.tasty_eats_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.myapps.tasty_eats_admin.adapters.AllItemsAdapter
import com.myapps.tasty_eats_admin.databinding.ActivityAllItemBinding
import com.myapps.tasty_eats_admin.models.AllMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllItemActivity : AppCompatActivity() {
    //Firebase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var menuItems: ArrayList<AllMenu> = ArrayList()

    private lateinit var binding: ActivityAllItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        lifecycleScope.launch(Dispatchers.IO) {
            retrieveMenuItem()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retrieveMenuItem() {
        val userId = auth.currentUser!!.uid
        val foodRef = databaseReference.child("menu").child(userId)

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()

                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllItemActivity,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter() {
        val userId = auth.currentUser!!.uid
        val foodRef = databaseReference.child("menu").child(userId)
        val adapter = AllItemsAdapter(this@AllItemActivity,menuItems,foodRef)
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.menuRecyclerView.adapter = adapter
    }
}