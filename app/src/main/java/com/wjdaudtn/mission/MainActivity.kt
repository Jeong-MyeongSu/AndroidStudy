package com.wjdaudtn.mission

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wjdaudtn.mission.recyclerView.ViewHolderTwo
import com.wjdaudtn.mission.todo.TodoMainActivity
import com.wjdaudtn.mission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRecyclerSample1.setOnClickListener(customOnclickListener)
        binding.goTodo.setOnClickListener(customOnclickListener)
    }
    private val customOnclickListener: View.OnClickListener = (object:View.OnClickListener{
        override fun onClick(v: View?) {
            if(v == null){
                return
            }
            when(v.id){
                R.id.tv_recycler_sample_1 -> {
                    val intent = Intent(this@MainActivity.baseContext, ViewHolderTwo::class.java)
                    startActivity(intent)
                }
                R.id.go_todo ->{
                    val intent = Intent(this@MainActivity.baseContext, TodoMainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    })
}