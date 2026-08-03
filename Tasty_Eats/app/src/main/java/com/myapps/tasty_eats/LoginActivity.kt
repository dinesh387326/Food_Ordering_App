package com.myapps.tasty_eats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.myapps.tasty_eats.databinding.ActivityIntroBinding
import com.myapps.tasty_eats.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    //edittext
    private lateinit var email: String
    private lateinit var password: String
    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //firebase initialization
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.dontHaveAccount.setOnClickListener{
            val intent = Intent(this,SignActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener{
            email = binding.editTextTextEmailAddress.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
            password = binding.editTextTextPassword.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                signInUser(email,password)
            }
        }

    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if(task.isSuccessful){
                isVerified()
            }else{
                Toast.makeText(this,"Incorrect email and password",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isVerified() {
        val firebaseUser = auth.currentUser
        val flag = firebaseUser?.isEmailVerified

        if(flag!!){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this,"Please verify your email",Toast.LENGTH_SHORT).show()
            firebaseUser.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Verification mail sent",Toast.LENGTH_SHORT).show()
                    binding.editTextTextEmailAddress.text = null
                    binding.editTextTextPassword.text = null
                }else{
                    Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null && currentUser.isEmailVerified){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}