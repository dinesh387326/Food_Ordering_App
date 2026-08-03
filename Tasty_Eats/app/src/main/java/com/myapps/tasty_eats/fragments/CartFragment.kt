package com.myapps.tasty_eats.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.SharedViewModel
import com.myapps.tasty_eats.adapters.CartAdapter
import com.myapps.tasty_eats.databinding.FragmentCartBinding
import com.myapps.tasty_eats.models.AllMenu
import com.myapps.tasty_eats.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartFragment : Fragment(), CartAdapter.EmptyCartListener {
    private lateinit var binding:FragmentCartBinding
    private lateinit var viewModel: SharedViewModel
    //firebase
    private var cartItems: ArrayList<CartItem> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)
        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = auth.currentUser!!.uid

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val cartItemRef = databaseReference.child("user").child(userId).child("cartItems")

        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartItems.clear()

                    for(foodSnapshot in snapshot.children){
                        val cartItem = foodSnapshot.getValue(CartItem::class.java)
                        if (cartItem != null) {
                            cartItems.add(cartItem)
                        }
                    }
                    if(cartItems.isNotEmpty()){
                        context?.let {
                            val adapter = CartAdapter(cartItems,cartItemRef,requireContext(),this@CartFragment)
                            binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext())
                            binding.cartRecycler.adapter = adapter
                            binding.proceedButton.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
                }
        })


        binding.proceedButton.setOnClickListener {
            viewModel.cartItems.value = cartItems
            findNavController().navigate(R.id.payOutFragment)
        }

    }

    companion object {
    }

    override fun onClick() {
        binding.proceedButton.visibility = View.GONE
    }
}