package com.wjdaudtn.mission.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityTodoMainBinding
import com.wjdaudtn.mission.todo.adapter.TodoAdapter
import com.wjdaudtn.mission.todo.database.Todo
import com.wjdaudtn.mission.todo.database.TodoDao
import com.wjdaudtn.mission.todo.util.Const.Companion.ALARM_RECEIVER_ACTION
import com.wjdaudtn.mission.todo.util.Const.Companion.CANCEL_ALARM_TOAST
import com.wjdaudtn.mission.todo.util.Const.Companion.DEFAULT_VALUE
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ALARM_SWITCH
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_CONTENT
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ID
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_MILLISECOND
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_TITLE
import java.util.Calendar

class TodoMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoMainBinding
    private lateinit var mAdapter: TodoAdapter
    private var todayMillisecond = Calendar.getInstance().timeInMillis

    private lateinit var dbInstance: TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // database 빌드 및 DAO 초기화
        dbInstance = DataBaseInit().getTodoDao(applicationContext)

        // Broadcast 수신기
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(alarmReceiver, IntentFilter(ALARM_RECEIVER_ACTION))
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 업 버튼 클릭 시 뒤로 가기 동작 정의
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val id = data?.getIntExtra(RESULT_KEY_ID, DEFAULT_VALUE) ?: -1
        val title = data?.getStringExtra(RESULT_KEY_TITLE)
        val content = data?.getStringExtra(RESULT_KEY_CONTENT)
        val millisecond = data?.getLongExtra(RESULT_KEY_MILLISECOND, DEFAULT_VALUE.toLong()) ?: -1
        val alarmSwitch = data?.getIntExtra(RESULT_KEY_ALARM_SWITCH, DEFAULT_VALUE) ?: -1

        if (title == null || content == null) {
            return@registerForActivityResult
        }

        if (id != -1) {
            val todo = Todo().apply {
                this.id = id
                this.title = title
                this.content = content
                this.millisecond = millisecond
                this.alarmSwitch = alarmSwitch
            }
            // database 에 Todo 객체 update
            dbInstance.setUpdateTodo(todo)
            mAdapter.updateItem(todo)
            alarmAsk(todo, alarmSwitch, millisecond)

        } else {
            val todo = Todo().apply {
                this.title = title
                this.content = content
                this.millisecond = millisecond
                this.alarmSwitch = alarmSwitch
            }
            // db id 가져 와야함.
            // database 에 Todo 객체 삽입
            val localId = dbInstance.setInsertTodo(todo)
            todo.id = localId.toInt()
            mAdapter.addItem(todo)
            alarmAsk(todo, alarmSwitch, millisecond)
        }
    }

    private val alarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val todoId = intent.getIntExtra(RESULT_KEY_ID, DEFAULT_VALUE)
            if (todoId != -1) {
                updateTodoItem(todoId)
            }
        }
    }

    //알림이 울렸을 때 update
    private fun updateTodoItem(todoId: Int) {
        val todo = dbInstance.getTodoById(todoId)
        if (todo != null) {
            todo.alarmSwitch = 0 // 알람이 울려 알람 스위치 꺼짐
            dbInstance.setUpdateTodo(todo)
            mAdapter.updateItem(todo)
        }
    }

    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_todo -> {
                val intent = Intent(
                    this@TodoMainActivity.baseContext,
                    AddActivity::class.java
                )
                todayMillisecond = Calendar.getInstance().timeInMillis + 1000 * 60 * 60
                intent.putExtra(RESULT_KEY_MILLISECOND, todayMillisecond)
                requestLauncher.launch(intent)
            }

        }
    })

    private fun alarmAsk(todo: Todo, alarmSwitch: Int, millisecond: Long) {
        if (alarmSwitch == 0) {
            Toast.makeText(baseContext, CANCEL_ALARM_TOAST, Toast.LENGTH_SHORT)
                .show()
            cancelAlarm(todo)
        } else {
            val currentTime = Calendar.getInstance().timeInMillis
            val remainingTime = millisecond - currentTime
            val seconds = remainingTime / 1000 % 60
            val minutes = remainingTime / 1000 / 60 % 60
            val hours = remainingTime / 1000 / 60 / 60 % 24
            val days = remainingTime / 1000 / 60 / 60 / 24

            val remainingTimeString = "남은 시간: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
            Toast.makeText(baseContext, remainingTimeString, Toast.LENGTH_SHORT)
                .show()
            setAlarm(todo)
        }
    }

    private fun setAlarm(todo: Todo) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java).apply {
            putExtra(RESULT_KEY_TITLE, todo.title)
            putExtra(RESULT_KEY_CONTENT, todo.content)
            putExtra(RESULT_KEY_ID, todo.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            todo.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = todo.millisecond
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancelAlarm(todo: Todo) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            todo.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerviewTodo.layoutManager = layoutManager
        mAdapter =
            TodoAdapter(dbInstance.getTodoAll(), requestLauncher, this)
        binding.recyclerviewTodo.adapter = mAdapter
        val dividerItemDecoration =
            DividerItemDecoration(baseContext, layoutManager.orientation)
        binding.recyclerviewTodo.addItemDecoration(dividerItemDecoration)
        binding.btnTodo.setOnClickListener(customClickListener)

        //ToolBar
        setSupportActionBar(binding.todoMainToolbar)
        /**
         *         supportActionBar?.setDisplayHomeAsUpEnabled(true)
         *         supportActionBar?.setDisplayShowHomeEnabled(true)
         */
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        // OnBackPressedDispatcher를 사용하여 뒤로 가기 동작을 처리
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로 가기 동작 정의
                finish()
            }
        })

    }

}