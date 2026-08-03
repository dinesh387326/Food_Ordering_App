package com.myapps.tasty_eats_admin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapps.tasty_eats_admin.databinding.ActivityAddItemBinding
import com.myapps.tasty_eats_admin.models.AllMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddItemActivity : AppCompatActivity() {
    // Food item details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredients: String
    private var foodImageUri: Uri? = null
    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    //layout
    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.addItemBtn.setOnClickListener {
            foodName = binding.enterFoodName.text.toString()
            foodPrice = binding.enterFoodPrice.text.toString()
            foodIngredients = binding.ingredients.text.toString()
            foodDescription = binding.description.text.toString()
            if(foodName.isBlank() || foodPrice.isBlank() || foodIngredients.isBlank() || foodDescription.isBlank()){
                Toast.makeText(this,"Fill all the details",Toast.LENGTH_SHORT).show()
            }
            else {
                lifecycleScope.launch{
                    withContext(Dispatchers.IO){
                        uploadData()
                    }
                }
            }
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
            if(uri != null){
                foodImageUri = uri
                binding.selectedImage.setImageURI(uri)
            }
        }

        binding.selectImage.setOnClickListener{
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    // function to convert image to base64String
    private fun encodeImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uploadData() {
        if(foodImageUri != null){
            val base64Image = encodeImageToBase64(foodImageUri!!)
            val newItem = AllMenu(foodName,foodPrice,base64Image,foodDescription,foodIngredients,true)
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val key = database.reference.child("menu").child(userId).push().key!!
            database.reference.child("menu").child(userId).child(key).setValue(newItem).addOnSuccessListener {
                Toast.makeText(this,"Item added successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"Select an Image",Toast.LENGTH_SHORT).show()
        }
    }
}