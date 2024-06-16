package com.wjdaudtn.mission.Todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.wjdaudtn.mission.MainActivity
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.Todo.Adapter.TodoAdapter
import com.wjdaudtn.mission.Todo.Database.Todo
import com.wjdaudtn.mission.Todo.Database.TodoDao
import com.wjdaudtn.mission.Todo.Database.TodoDatabase
import com.wjdaudtn.mission.databinding.ActivityTodoMainBinding

class TodoMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoMainBinding

    data class Text(val title: String, val content: String)

    private lateinit var mAdapter: TodoAdapter

    private lateinit var mTodoData: TodoDatabase
    private lateinit var mTodoDao: TodoDao


    private val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val title = result.data?.getStringExtra("result_title")
        val content = result.data?.getStringExtra("result_content")
        val id = result.data?.getIntExtra("id", -1) ?: -1

        if (title == null || content == null) {
            return@registerForActivityResult
        }

        if (id != -1) {
            val todo = Todo().apply {
                this.id = id
                this.title = title
                this.content = content
            }
            // 데이터베이스에 Todo 객체 업데이트
            mTodoDao.setUpdateTodo(todo)
            mAdapter.updateItem(todo)
        } else {
            val todo = Todo().apply {
                this.title = title
                this.content = content
            }

            // db id 가져와야함.
            // 데이터베이스에 Todo 객체 삽입
            val id = mTodoDao.setInsertTodo(todo)
            Log.d("id","$id")
            todo.id = id.toInt()
            mAdapter.addItem(todo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 데이터베이스 빌드 및 DAO 초기화
        mTodoData = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "Todo_db"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        mTodoDao = mTodoData.todoDao()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerviewTodo.layoutManager = layoutManager
        mAdapter = TodoAdapter(mTodoDao.getTodoAll(), requestLauncher, mTodoDao)
        binding.recyclerviewTodo.adapter = mAdapter

        binding.btnTodo.setOnClickListener(customClickListener)
        binding.btnBackMain.setOnClickListener(customClickListener)

    }

    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_todo -> {
                val intent = Intent(this@TodoMainActivity, AddActivity::class.java)
                requestLauncher.launch(intent)
            }

            R.id.btn_back_main -> {
                val intent = Intent(this@TodoMainActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    })
}