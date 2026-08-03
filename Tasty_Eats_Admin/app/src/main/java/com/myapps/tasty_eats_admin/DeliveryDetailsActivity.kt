package com.myapps.tasty_eats_admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.myapps.tasty_eats_admin.databinding.ActivityDeliveryDetailsBinding
import com.myapps.tasty_eats_admin.databinding.ActivityPendingDetailsBinding

class DeliveryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFields()

        binding.backButton.setOnClickListener{
            finish()
        }

    }

    private fun setFields() {
        val intent = getIntent()
        val name = intent.getStringExtra("customerName")
        val address = intent.getStringExtra("customerAddress")
        val phone = intent.getStringExtra("customerPhone")
        val email = intent.getStringExtra("customerEmail")
        val foodName = intent.getStringExtra("foodName")
        val foodPrice = intent.getStringExtra("foodPrice")
        val foodQuantity = intent.getIntExtra("foodQuantity",0)

        binding.name.setText(name)
        binding.address.setText(address)
        binding.phone.setText(phone)
        binding.email.setText(email)
        binding.textFoodName.setText(foodName)
        binding.textPrice.setText(foodPrice)
        binding.textQuantity.setText(foodQuantity.toString())
    }
}