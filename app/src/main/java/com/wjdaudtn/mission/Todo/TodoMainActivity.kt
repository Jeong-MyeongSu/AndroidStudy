package com.wjdaudtn.mission.Todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.wjdaudtn.mission.AlarmReceiver
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.Todo.Adapter.TodoAdapter
import com.wjdaudtn.mission.Todo.Database.Todo
import com.wjdaudtn.mission.Todo.Database.TodoDao
import com.wjdaudtn.mission.Todo.Database.TodoDatabase
import com.wjdaudtn.mission.databinding.ActivityTodoMainBinding
import java.time.LocalDate
import java.util.Calendar

class TodoMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoMainBinding

    private lateinit var mAdapter: TodoAdapter
    private lateinit var mTodoData: TodoDatabase
    private lateinit var mTodoDao: TodoDao
    @RequiresApi(Build.VERSION_CODES.O)
    private val localDate: LocalDate = LocalDate.now()

    private fun setAlarm(todo: Todo) {

    }

    //api30이상 미만에서는 startactivityforresult, onactivityresult
    private val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val title = result.data!!.getStringExtra("result_title")
        val content = result.data!!.getStringExtra("result_content")
        val id = result.data?.getIntExtra("id", -1) ?: -1
        val hour = result.data?.getIntExtra("hour", -1) ?: -1
        val minute = result.data?.getIntExtra("minute",-1) ?: -1
        val year = result.data?.getIntExtra("year", -1) ?: -1
        val month = result.data?.getIntExtra("month", -1) ?:-1
        val dayOfMonth = result.data?.getIntExtra("dayOfMonth", -1) ?:-1
        val alarmSwitch = result.data?.getIntExtra("alarmSwitch", -1) ?:-1

        if (title == null || content == null) {
            return@registerForActivityResult
        }

        if (id != -1) {
            val todo = Todo().apply {
                this.id = id
                this.title = title
                this.content = content
                this.year = year
                this.month = month
                this.dayOfMonth = dayOfMonth
                this.hour = hour
                this.minute = minute
                this.alramSwitch = alarmSwitch
            }
            // 데이터베이스에 Todo 객체 업데이트
            mTodoDao.setUpdateTodo(todo)
            mAdapter.updateItem(todo)

        } else {
            val todo = Todo().apply {
                this.title = title
                this.content = content
                this.year = year
                this.month = month
                this.dayOfMonth = dayOfMonth
                this.hour = hour
                this.minute = minute
                this.alramSwitch = alarmSwitch
            }
            // db id 가져와야함.
            // 데이터베이스에 Todo 객체 삽입
            val id = mTodoDao.setInsertTodo(todo)
            todo.id = id.toInt()
            mAdapter.addItem(todo)

        }
        if(alarmSwitch == 0){
            Log.d("알람","알람 꺼짐")
            Toast.makeText(this,"알람 꺼짐",Toast.LENGTH_SHORT).show()
        }else{
            Log.d("알람","알람 켜짐")
            // 현재 시간 밀리초로 가져오기
            val currentTime = Calendar.getInstance().timeInMillis
            // 타겟 시간 밀리초로 설정
            val targetTime = Calendar.getInstance().apply {
                set(year, month - 1, dayOfMonth, hour, minute)
            }.timeInMillis

            // 남은 시간 계산
            val remainingTime = targetTime - currentTime
            val seconds = remainingTime / 1000 % 60
            val minutes = remainingTime / 1000 / 60 % 60
            val hours = remainingTime / 1000 / 60 / 60 % 24
            val days = remainingTime / 1000 / 60 / 60 / 24

            // 남은 시간을 문자열로 변환
            val remainingTimeString = "남은 시간: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
            // Toast 메시지로 표시
            Toast.makeText(this, remainingTimeString, Toast.LENGTH_SHORT).show()
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
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation);
        binding.recyclerviewTodo.addItemDecoration(dividerItemDecoration)
        binding.btnTodo.setOnClickListener(customClickListener)
        binding.btnBackMain.setOnClickListener(customClickListener)
        binding.btnEditing.setOnClickListener(customClickListener)

    }


    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_todo -> {
                val intent = Intent(this@TodoMainActivity, AddActivity::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra("year",localDate.year)
                    intent.putExtra("month",localDate.monthValue)
                    intent.putExtra("dayOfMonth",localDate.dayOfMonth)
                    Log.d("day","${localDate.year}, ${localDate.month}, ${localDate.dayOfMonth}")
                }
                requestLauncher.launch(intent)
            }
            R.id.btn_back_main -> {
                finish()
            }
            R.id.btn_editing -> {

            }
        }
    })
}