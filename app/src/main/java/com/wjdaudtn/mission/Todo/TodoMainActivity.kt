package com.wjdaudtn.mission.Todo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.MainActivity
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityTodoMainBinding

class TodoMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityTodoMainBinding
    data class Text(val title: String, val content: String)
    lateinit var datas: MutableList<Text>
    lateinit var adapter: TodoAdapter

    //what
    @SuppressLint("NotifyDataSetChanged")
    val requestLuncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        val title = result.data?.getStringExtra("result_title")
        val content = result.data?.getStringExtra("result_content")
        val position = result.data?.getIntExtra("position",-1) ?:-1
        if (title != null && content != null && position != -1) {
            datas[position] = Text(title, content)
            adapter.notifyItemChanged(position)
        } else if (title != null && content != null) {
            datas.add(Text(title, content))
            adapter.notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val position = intent.getIntExtra("position",-1)
        //what
        datas = savedInstanceState?.let {
            it.getStringArrayList("datas")?.map { str -> Text(str, "") }?.toMutableList()
        } ?: mutableListOf()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerviewTodo.layoutManager = layoutManager
        adapter = TodoAdapter(datas,requestLuncher)
        binding.recyclerviewTodo.adapter = adapter

        binding.btnTodo.setOnClickListener(customClickListener)
        binding.btnBackMain.setOnClickListener(customClickListener)

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("datas", ArrayList(datas.map { it.title }))
    }

    private val customClickListener: View.OnClickListener = (object:View.OnClickListener{
        override fun onClick(v: View?) {
            if (v != null) {
                when(v.id){
                    R.id.btn_todo ->{
                        var intent = Intent(this@TodoMainActivity,AddActivity::class.java)
                        requestLuncher.launch(intent)
                    }
                    R.id.btn_back_main ->{
                        var intent = Intent(this@TodoMainActivity,MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    })


}