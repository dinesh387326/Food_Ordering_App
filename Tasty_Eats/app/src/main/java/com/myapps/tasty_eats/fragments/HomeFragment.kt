package com.myapps.tasty_eats.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.myapps.tasty_eats.DetailsActivity
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.SharedViewModel
import com.myapps.tasty_eats.adapters.PopularAdapter
import com.myapps.tasty_eats.databinding.FragmentHomeBinding
import com.myapps.tasty_eats.models.AllMenu
import com.myapps.tasty_eats.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), PopularAdapter.HomeClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var menuItems: ArrayList<AllMenu> = arrayListOf()
    private var shopIds: ArrayList<String> = arrayListOf()
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
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        //firebase
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.reference
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.CENTER_CROP))

        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

        binding.imageSlider.setItemClickListener(object : ItemClickListener{
            override fun doubleClick(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }
        })
        lifecycleScope.launch(Dispatchers.IO){
            val foodRef = databaseReference.child("menu")

            foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    menuItems.clear()
                    shopIds.clear()
                    for(foodSnapshot in snapshot.children){
                        val id = foodSnapshot.key

                        for(food in foodSnapshot.children){
                            val menuItem = food.getValue(AllMenu::class.java)
                            if (id != null) {
                                shopIds.add(id)
                            }
                            menuItem?.let {
                                menuItems.add(menuItem)
                            }
                        }
                    }
                    if(menuItems.isNotEmpty()){
                        setAdapter()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private fun setAdapter() {
        context?.let {
            val adapter = PopularAdapter(menuItems,this@HomeFragment,requireContext())
            binding.popularRecycler.layoutManager = LinearLayoutManager(requireContext())
            binding.popularRecycler.adapter = adapter
        }
    }

    companion object {

    }

    override fun onClick(position: Int) {
        val menuItem = menuItems[position]
        val cartItem = CartItem(shopIds[position],menuItem.foodName,menuItem.foodPrice,menuItem.foodImage,1)
        val userId = auth.currentUser!!.uid

        databaseReference.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(requireContext(),"Item added to Cart",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
        }

    }
}