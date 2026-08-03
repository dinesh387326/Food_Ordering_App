package com.myapps.tasty_eats_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.myapps.tasty_eats_admin.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing auth and database
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.noAccount.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {

            email = binding.editTextEmailLogin.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")
            password = binding.editTextPassword.text.toString().replace(Regex("^[\\p{Zs}\\s]+|[\\p{Zs}\\s]+$"), "")

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all the details",Toast.LENGTH_SHORT).show()
            }
            else{
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        signInUser(email,password)
                    }
                }
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
                    binding.editTextEmailLogin.text = null
                    binding.editTextPassword.text = null
                }else{
                    Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val currentUser = auth.currentUser
            if(currentUser != null && currentUser.isEmailVerified){
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}