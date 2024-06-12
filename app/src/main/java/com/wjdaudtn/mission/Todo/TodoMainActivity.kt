package com.wjdaudtn.mission.Todo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityTodoMainBinding

class TodoMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityTodoMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTodo.setOnClickListener(customClickListener)
    }
    private val customClickListener: View.OnClickListener = (object:View.OnClickListener{
        override fun onClick(v: View?) {
            var intent = Intent(this@TodoMainActivity,AddActivity::class.java)
            startActivity(intent)
        }
    })
}