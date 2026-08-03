package com.myapps.tasty_eats.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.database.database
import com.myapps.tasty_eats.LoginActivity
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    //Firebase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = auth.currentUser!!.uid
        val userRef = databaseReference.child("user").child(userId)

        lifecycleScope.launch(Dispatchers.IO){

            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(items in snapshot.children){
                        if(items.key == "username"){
                            val value = items.getValue(String::class.java)
                            binding.name.setText(value)
                        }
                        if(items.key == "email"){
                            val value = items.getValue(String::class.java)
                            binding.email.setText(value)
                        }
                        if(items.key == "address"){
                            val value = items.getValue(String::class.java)
                            binding.address.setText(value)
                        }
                        if(items.key == "phone"){
                            val value = items.getValue(String::class.java)
                            binding.phone.setText(value)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.editBtn.setOnClickListener{
            if(!binding.name.isEnabled){
                binding.name.isEnabled = true
                binding.email.isEnabled = true
                binding.address.isEnabled = true
                binding.phone.isEnabled = true
            }else{
                binding.name.isEnabled = false
                binding.email.isEnabled = false
                binding.address.isEnabled = false
                binding.phone.isEnabled = false
            }
        }
        lifecycleScope.launch(Dispatchers.IO){
            binding.saveBtn.setOnClickListener{
                val name = binding.name.text.toString()
                val email = binding.email.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
                val phone = binding.phone.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
                val address = binding.address.text.toString()

                // nested condition to confirm that all edit values updated in database
                userRef.child("username").setValue(name).addOnSuccessListener {
                    userRef.child("email").setValue(email).addOnSuccessListener {
                        userRef.child("address").setValue(address).addOnSuccessListener {
                            userRef.child("phone").setValue(phone).addOnSuccessListener {
                                Toast.makeText(requireContext(),"Updated Successfully", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener { error ->
                                Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show() }
                        }.addOnFailureListener { error ->
                            Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show() }
                    }.addOnFailureListener { error ->
                        Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show() }
                }.addOnFailureListener { error ->
                    Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show() }
                if(binding.name.isEnabled){
                    binding.name.isEnabled = false
                    binding.email.isEnabled = false
                    binding.address.isEnabled = false
                    binding.phone.isEnabled = false
                }
            }
        }

        binding.logOutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(),LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    companion object {

    }
}