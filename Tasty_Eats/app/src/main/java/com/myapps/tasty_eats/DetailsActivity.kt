package com.myapps.tasty_eats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.myapps.tasty_eats.databinding.ActivityDetailsBinding
import com.myapps.tasty_eats.fragments.CartFragment

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodName = intent.getStringExtra("detailsFood")
        val foodImage = intent.getIntExtra("detailsImage",0)
        val foodPrice = intent.getStringExtra("detailsPrice")

        binding.detailsName.text = foodName
        binding.detailsImage.setImageResource(foodImage)

        binding.detailBackBtn.setOnClickListener {
            finish()
        }

//        binding.buttonAddToCart.setOnClickListener {
//            val viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
//            viewModel.foodName.value = foodName
//            viewModel.foodPrice.value = foodPrice
//            viewModel.foodImage.value = foodImage
//        }

    }
}