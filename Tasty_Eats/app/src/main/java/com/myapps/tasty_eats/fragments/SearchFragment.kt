package com.myapps.tasty_eats.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
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
import com.myapps.tasty_eats.adapters.CartAdapter
import com.myapps.tasty_eats.adapters.MenuBottomSheetAdapter
import com.myapps.tasty_eats.databinding.FragmentSearchBinding
import com.myapps.tasty_eats.models.AllMenu
import com.myapps.tasty_eats.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

class SearchFragment : Fragment(),MenuBottomSheetAdapter.MenuClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuBottomSheetAdapter
    //firebase
    private var menuItems: ArrayList<AllMenu> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var searchFoodName : ArrayList<String> = arrayListOf()
    private var searchItemPrice : ArrayList<String> = arrayListOf()
    private var searchImage : ArrayList<String> = arrayListOf()
    private var searchShopIds : ArrayList<String> = arrayListOf()
    private var searchStocks: ArrayList<Boolean> = arrayListOf()

    // for inverted indexing
    val indexing = HashMap<String, MutableList<Int>>()

    private var filterMenuFood: MutableList<String> = mutableListOf()
    private var filterMenuItem: MutableList<String> = mutableListOf()
    private var filterMenuImage: MutableList<String> = mutableListOf()
    private var filterStocks: MutableList<Boolean> = mutableListOf()
    private var filterShopIds: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.reference
        auth = FirebaseAuth.getInstance()
        binding = FragmentSearchBinding.inflate(inflater,container,false)

        return binding.root
    }

    private fun showAllMenu() {
        filterMenuFood.clear()
        filterMenuItem.clear()
        filterMenuImage.clear()
        filterShopIds.clear()
        filterStocks.clear()


        searchFoodName.forEachIndexed { index, _ ->
                    filterMenuFood.add(searchFoodName[index])
                    filterMenuItem.add(searchItemPrice[index])
                    filterMenuImage.add(searchImage[index])
                    filterShopIds.add(searchShopIds[index])
                    filterStocks.add(searchStocks[index])
                    val words = searchFoodName[index]
                        .lowercase()
                        .split("\\s+".toRegex())

                    for (word in words) {
                        indexing.getOrPut(word) {
                            mutableListOf()
                        }.add(index)
                    }
        }

        adapter.notifyDataSetChanged()

    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String): Boolean {
                filterMenuItems(p0)
                return true
            }

            override fun onQueryTextChange(p0: String): Boolean {
                filterMenuItems(p0)
                return true
            }
        })
    }

    private fun filterMenuItems(p0: String) {
        filterMenuFood.clear()
        filterMenuItem.clear()
        filterMenuImage.clear()
        filterShopIds.clear()
        filterStocks.clear()


        val words = p0.lowercase().split(" ")
        var result: Set<Int>? = null

        for (word in words) {

                val ids = indexing[word]?.toSet() ?: emptySet()

                result = if (result == null)
                    ids
                else
                    result!!.intersect(ids)
            }

        result!!.forEach { index ->
                filterMenuFood.add(searchFoodName[index])
                filterMenuItem.add(searchItemPrice[index])
                filterMenuImage.add(searchImage[index])
                filterShopIds.add(searchShopIds[index])
                filterStocks.add(searchStocks[index])
        }


        adapter.notifyDataSetChanged()

    }

//    private fun getItems(){
//        val foodRef = databaseReference.child("menu")
//        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                menuItems.clear()
//                searchFoodName.clear()
//                searchItemPrice.clear()
//                searchImage.clear()
//                searchShopIds.clear()
//                searchStocks.clear()
//
//                for (foodSnapshot in snapshot.children) {
//                    val id = foodSnapshot.key
//                    for (food in foodSnapshot.children) {
//                        val menuItem = food.getValue(AllMenu::class.java)
//                        if(id != null){
//                            searchShopIds.add(id)
//                        }
//                        menuItem?.let {
//                            menuItems.add(menuItem)
//                        }
//                    }
//                }
//
//                for(each in menuItems){
//                    each.foodName?.let {
//                        searchFoodName.add(each.foodName)
//                    }
//                    each.foodPrice?.let {
//                        searchItemPrice.add(each.foodPrice)
//                    }
//                    each.foodImage?.let {
//                        searchImage.add(each.foodImage)
//                    }
//                    each.stocks?.let {
//                        searchStocks.add(each.stocks!!)
//                    }
//                }
//                Toast.makeText(requireContext(),"Menu Called 1",Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            try {
                lifecycleScope.launch {
                val snapshot = withContext(Dispatchers.IO) {
                    databaseReference
                        .child("menu")
                        .get()
                        .await()
                }

                menuItems.clear()
                searchFoodName.clear()
                searchItemPrice.clear()
                searchImage.clear()
                searchShopIds.clear()
                searchStocks.clear()

                for (foodSnapshot in snapshot.children) {

                    val shopId = foodSnapshot.key

                    for (food in foodSnapshot.children) {

                        val menuItem = food.getValue(AllMenu::class.java)

                        if (shopId != null) {
                            searchShopIds.add(shopId)
                        }

                        menuItem?.let {
                            menuItems.add(it)
                        }
                    }
                }

                for (each in menuItems) {

                    each.foodName?.let {
                        searchFoodName.add(it)
                    }

                    each.foodPrice?.let {
                        searchItemPrice.add(it)
                    }

                    each.foodImage?.let {
                        searchImage.add(it)
                    }

                    each.stocks?.let {
                        searchStocks.add(it)
                    }
                }

                adapter = MenuBottomSheetAdapter(filterMenuFood,filterMenuItem,filterMenuImage,filterStocks,this@SearchFragment,requireContext())
                binding.searchRecycler.layoutManager = LinearLayoutManager(requireContext())
                binding.searchRecycler.adapter = adapter
                showAllMenu()
                adapter.notifyDataSetChanged()
                setupSearchView()
            }
            }
            catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    e.message ?: "Unknown error",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {

    }

    override fun onClick(position: Int) {
//        val intent = Intent(requireContext(), DetailsActivity::class.java)
//        intent.putExtra("detailsFood",filterMenuFood[position])
//        intent.putExtra("detailsImage",filterMenuImage[position])
//        requireContext().startActivity(intent)
        val cartItem = CartItem(filterShopIds[position],filterMenuFood[position],filterMenuItem[position],filterMenuImage[position],1)
        val userId = auth.currentUser!!.uid

        databaseReference.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(requireContext(),"Item added to Cart",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
        }

    }
}