package com.wjdaudtn.mission

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.wjdaudtn.mission.RecyclerView.ViewHolderTwo
import com.wjdaudtn.mission.Todo.TodoMainActivity
import com.wjdaudtn.mission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRecyclerSample1.setOnClickListener(MyOnClickListener)
        binding.goTodo.setOnClickListener(MyOnClickListener)

    }

    private val MyOnClickListener: View.OnClickListener = (object:View.OnClickListener{
        override fun onClick(v: View?) {
            if(v == null){
                return;
            }
            when(v.id){
                R.id.tv_recycler_sample_1 -> {
                    val intent = Intent(this@MainActivity, ViewHolderTwo::class.java)
                    startActivity(intent)
                }
                R.id.go_todo ->{
                    val intent = Intent(this@MainActivity, TodoMainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    })
}