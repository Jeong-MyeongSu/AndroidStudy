package com.wjdaudtn.mission.Todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
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
    private val localDate: LocalDate = LocalDate.now()//오늘 날짜 AddActivity에 intent 초기값을위해

    private fun setAlarm(todo: Todo) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("todoTitle", todo.title)
            putExtra("todoContent", todo.content)
            putExtra("todoId",todo.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            todo.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, todo.year)
            set(Calendar.MONTH, todo.month - 1)
            set(Calendar.DAY_OF_MONTH, todo.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, todo.hour)
            set(Calendar.MINUTE, todo.minute)
            set(Calendar.SECOND, 0)
        }
        Log.d("setAlarm", "Setting alarm for: ${calendar.time}")
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
    fun cancelAlarm(todo: Todo) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, todo.id, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
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
            alarmAsk(todo,alarmSwitch,year,month,dayOfMonth,hour,minute)

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
            alarmAsk(todo,alarmSwitch,year,month,dayOfMonth,hour,minute)
        }

    }

    private val alarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val todoId = intent.getIntExtra("todoId", -1)
            if (todoId != -1) {
                updateTodoItem(todoId)
            }
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
        mAdapter = TodoAdapter(mTodoDao.getTodoAll(), requestLauncher, mTodoDao, this)
        binding.recyclerviewTodo.adapter = mAdapter
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.recyclerviewTodo.addItemDecoration(dividerItemDecoration)
        binding.btnTodo.setOnClickListener(customClickListener)
        binding.btnBackMain.setOnClickListener(customClickListener)
//        binding.btnEditing.setOnClickListener(customClickListener)
        //브로드캐스트 수신기
        LocalBroadcastManager.getInstance(this).registerReceiver(alarmReceiver, IntentFilter("com.example.todo.ALARM_TRIGGERED"))
    }
    override fun onDestroy() {
        super.onDestroy()
        // 액티비티가 파괴될 때 브로드캐스트 수신기 해제
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(alarmReceiver)
    }

    //알림이 울렸을때 업데이트
    private fun updateTodoItem(todoId: Int) {
        val todo = mTodoDao.getTodoById(todoId)
        if (todo != null) {
            todo.alramSwitch = 0 // 알람이 울렸으므로 알람 스위치 꺼짐
            Log.d("알림 울린 후","${todo}")
            mTodoDao.setUpdateTodo(todo)
            mAdapter.updateItem(todo)
        }
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
        }
    })
    private fun alarmAsk(todo:Todo, alarmSwitch:Int , year:Int, month:Int, dayOfMonth:Int, hour:Int, minute:Int){
        if(alarmSwitch == 0){
            Log.d("알람","알람 꺼짐")
            Toast.makeText(this,"알람 꺼짐",Toast.LENGTH_SHORT).show()
            cancelAlarm(todo)
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
            setAlarm(todo)
        }
    }
}