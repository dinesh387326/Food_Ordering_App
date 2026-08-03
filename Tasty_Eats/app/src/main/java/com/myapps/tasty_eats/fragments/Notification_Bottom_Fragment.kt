package com.myapps.tasty_eats.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.adapters.NotificationAdapter
import com.myapps.tasty_eats.databinding.FragmentHistoryBinding
import com.myapps.tasty_eats.databinding.FragmentNotificationBottomBinding


class Notification_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notification = arrayListOf("Your order has been Canceled Successfully", "Order has been taken by the driver", "Congrats Your Order Placed")
        val notificationImages = arrayListOf(R.drawable.sademoji,R.drawable.truck,R.drawable.tickmark)
        val adapter = NotificationAdapter(notification,notificationImages)
        binding.notificationRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecycler.adapter = adapter

        return binding.root
    }

    companion object {

    }
}