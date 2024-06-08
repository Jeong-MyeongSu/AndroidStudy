package com.wjdaudtn.mission

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wjdaudtn.mission.RecyclerView.ViewHolderTwo
import com.wjdaudtn.mission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRecyclerSample1.setOnClickListener{
            val intent = Intent(this, ViewHolderTwo::class.java)
            startActivity(intent)
        }
    }
}