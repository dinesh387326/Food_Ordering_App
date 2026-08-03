package com.myapps.tasty_eats_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.myapps.tasty_eats_admin.databinding.ActivityAdminProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        val userRef = databaseReference.child("user").child(userId)

        lifecycleScope.launch(Dispatchers.IO){
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(items in snapshot.children){
                        if(items.key == "name"){
                            val value = items.getValue(String::class.java)
                            binding.nameEdit.setText(value)
                        }
                        if(items.key == "email"){
                            val value = items.getValue(String::class.java)
                            binding.emailEdit.setText(value)
                        }
                        if(items.key == "address"){
                            val value = items.getValue(String::class.java)
                            binding.addressEdit.setText(value)
                        }
                        if(items.key == "phone"){
                            val value = items.getValue(String::class.java)
                            binding.phoneEdit.setText(value)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AdminProfileActivity,error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        var isEnable = false

        binding.profileEdit.setOnClickListener {
            isEnable = !isEnable
            binding.nameEdit.isEnabled = isEnable
            binding.emailEdit.isEnabled = isEnable
            binding.addressEdit.isEnabled = isEnable
            binding.phoneEdit.isEnabled = isEnable
            if(isEnable){
                binding.nameEdit.requestFocus()
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            binding.saveBtn.setOnClickListener {
                val name = binding.nameEdit.text.toString()
                val email = binding.emailEdit.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
                val phone = binding.phoneEdit.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
                val address = binding.addressEdit.text.toString()

                // nested condition to confirm that all edit values updated in database
                userRef.child("name").setValue(name).addOnSuccessListener {
                    userRef.child("email").setValue(email).addOnSuccessListener {
                        userRef.child("address").setValue(address).addOnSuccessListener {
                            userRef.child("phone").setValue(phone).addOnSuccessListener {
                                Toast.makeText(this@AdminProfileActivity,"Updated Successfully", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener { error ->
                                Toast.makeText(this@AdminProfileActivity,error.message, Toast.LENGTH_SHORT).show() }
                        }.addOnFailureListener { error ->
                            Toast.makeText(this@AdminProfileActivity,error.message, Toast.LENGTH_SHORT).show() }
                    }.addOnFailureListener { error ->
                        Toast.makeText(this@AdminProfileActivity,error.message, Toast.LENGTH_SHORT).show() }
                }.addOnFailureListener { error ->
                    Toast.makeText(this@AdminProfileActivity,error.message, Toast.LENGTH_SHORT).show() }
                if(isEnable){
                    binding.nameEdit.isEnabled = false
                    binding.emailEdit.isEnabled = false
                    binding.addressEdit.isEnabled = false
                    binding.phoneEdit.isEnabled = false
                }
            }
        }

    }
}