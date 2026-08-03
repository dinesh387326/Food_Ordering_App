package com.myapps.tasty_eats_admin

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.myapps.tasty_eats_admin.databinding.ActivitySignUpBinding
import com.myapps.tasty_eats_admin.models.UserModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var nameOfRestaurant: String
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing auth and database
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val locationList = arrayOf("Jaipur", "Delhi", "Mumbai", "Kolkata")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, locationList)
        binding.autoListLocation.setAdapter(adapter)

        binding.haveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.createBtn.setOnClickListener{
            userName = binding.editTextName.text.toString()
            nameOfRestaurant = binding.editTextRestaurant.text.toString()
            email = binding.editTextEmailLogin.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
            password = binding.editTextPassword.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")

            if(userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all details",Toast.LENGTH_SHORT).show()
            }
            else{
                createAccount(email,password)
            }
        }

    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Successfully Created",Toast.LENGTH_SHORT).show()
                saveUserData()
            }
            else{
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                Log.d("Account","CreateAccount: Failed",task.exception)
            }
        }
    }

    private fun saveUserData() {
        userName = binding.editTextName.text.toString()
        nameOfRestaurant = binding.editTextRestaurant.text.toString()
        email = binding.editTextEmailLogin.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
        password = binding.editTextPassword.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
        val user = UserModel(userName,nameOfRestaurant,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
        database.child("user").child(userId).child("completedOrders").setValue(0)
        database.child("user").child(userId).child("earnings").setValue(0)
        verification()
    }

    private fun verification() {
        val firebaseUser = auth.currentUser

        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Verification mail sent",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}