package com.myapps.tasty_eats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.myapps.tasty_eats.databinding.ActivityChooseLocationBinding
import com.myapps.tasty_eats.databinding.ActivityIntroBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy{
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList = arrayOf("Jaipur","Delhi","New Delhi","Gurgaon")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        binding.chooseLocationTextView.setAdapter(adapter)
    }
}