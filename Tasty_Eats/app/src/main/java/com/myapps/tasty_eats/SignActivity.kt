package com.myapps.tasty_eats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.myapps.tasty_eats.databinding.ActivityLoginBinding
import com.myapps.tasty_eats.databinding.ActivitySignBinding
import com.myapps.tasty_eats.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignActivity : AppCompatActivity() {
    //edittext
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivitySignBinding by lazy{
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //firebase initialization
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.alreadyHaveAccount.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.createButton.setOnClickListener{
            username = binding.username.text.toString()
            email = binding.editTextTextEmailAddress.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
            password = binding.editTextTextPassword.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")

            if(username.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch(Dispatchers.IO){
                    createAccount(username,email,password)
                }
            }
        }
    }

    private fun createAccount(username: String,email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                saveUserData(username,email,password)
            }else{
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                Log.d("Account","CreateAccount: Failed",task.exception)
            }
        }
    }

    private fun saveUserData(username: String,email: String, password: String) {
        val user = UserModel(username,email,password)
        val userId = auth.currentUser!!.uid

        database.child("user").child(userId).setValue(user)

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