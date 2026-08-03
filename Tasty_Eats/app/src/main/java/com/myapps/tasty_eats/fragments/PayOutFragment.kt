package com.myapps.tasty_eats.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.myapps.tasty_eats.HistoryViewModel
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.SharedViewModel
import com.myapps.tasty_eats.databinding.FragmentPayOutBinding
import com.myapps.tasty_eats.models.CartItem
import com.myapps.tasty_eats.models.CurrentOrderItem
import com.myapps.tasty_eats.models.OrderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PayOutFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentPayOutBinding
    //firebase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //firebase initialization
        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        binding = FragmentPayOutBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var flag = true
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val customerId = auth.currentUser!!.uid
        var totalAmount = 0;
        // calculating total amt
        viewModel.cartItems.observe(viewLifecycleOwner) { list ->
            for(item in list){
                totalAmount += (item.foodPrice?.toInt() ?: 0) * item.foodQuantity!!
            }
            val total = "Rs. $totalAmount"
            binding.payOutAmt.setText(total)
        }
        try{
            lifecycleScope.launch {

            val snapshot = withContext(Dispatchers.IO) {
                databaseReference
                    .child("user")
                    .child(customerId)
                    .get()
                    .await()
            }

            for(items in snapshot.children){
                if(items.key == "username"){
                    val value = items.getValue(String::class.java)
                    binding.payOutName.setText(value)
                }
                if(items.key == "address"){
                    val value = items.getValue(String::class.java)
                    binding.payOutAddress.setText(value)
                }
                if(items.key == "phone"){
                    val value = items.getValue(String::class.java)
                    binding.payOutPhone.setText(value)
                }
            }
                if(binding.payOutAmt.equals("") || binding.payOutAddress.equals("") || binding.payOutPhone.equals("")){
                    Toast.makeText(requireContext(),"Please fill details from profile",Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {

            Toast.makeText(
                    requireContext(),
                    e.message ?: "Unknown error",
                    Toast.LENGTH_SHORT
            ).show()
        }

        binding.orderBtn.setOnClickListener {

            viewModel.cartItems.observe(viewLifecycleOwner) { list ->

                for (item in list) {
                            val userId = item.shopId
                            val orderItem = OrderItem(
                                customerId,
                                item.foodName,
                                item.foodPrice,
                                item.foodImage,
                                item.foodQuantity
                            )
                            if (userId != null) {
                                databaseReference.child("user").child(userId).child("pendingOrders").push()
                                    .setValue(orderItem).addOnFailureListener {
                                        flag = false
                                        Toast.makeText(
                                            requireContext(),
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                if (!flag) {
                                    break
                                }
                                val currentOrderItem = CurrentOrderItem(userId,
                                    item.foodName,
                                    item.foodPrice,
                                    item.foodImage,
                                    item.foodQuantity,
                                    "Pending"
                                )
                                databaseReference.child("user").child(customerId).child("currentOrders").push().setValue(currentOrderItem).addOnFailureListener {
                                    flag = false
                                    Toast.makeText(
                                        requireContext(),
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                if (!flag) {
                                    break
                                }
                            }
                }
                //deleting items from cart
                val cartItemRef = databaseReference.child("user").child(customerId).child("cartItems")

                cartItemRef.removeValue().addOnSuccessListener {
                    Log.d("Database","cart items deleted")
                }.addOnFailureListener { error ->
                    Log.d("Database","problem in deleting cart items",error)
                }

                if (flag) {
                    val congratsBottomDialog = CongratsBottomSheet()
                    congratsBottomDialog.show(parentFragmentManager, "Test")
                }else{
                    Toast.makeText(requireContext(),"something went wrong",Toast.LENGTH_SHORT).show()
                }
            }

        }


    }
    companion object {

    }
}