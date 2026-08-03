package com.myapps.tasty_eats_admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.myapps.tasty_eats_admin.databinding.ActivityPendingDetailsBinding

class PendingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFields()

        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun setFields() {
        val intent = Intent()
        val name = intent.getStringExtra("customerName")
        val address = intent.getStringExtra("customerAddress")
        val phone = intent.getStringExtra("customerPhone")
        val email = intent.getStringExtra("customerEmail")
        val foodName = intent.getStringExtra("foodName")
        val foodPrice = intent.getStringExtra("foodPrice")
        val foodQuantity = intent.getStringExtra("foodQuantity")

        binding.name.text = name
        binding.address.text = address
        binding.phone.text = phone
        binding.email.text = email
        binding.textFoodName.text = foodName
        binding.textPrice.text = foodPrice
        binding.textQuantity.text = foodQuantity
    }
}