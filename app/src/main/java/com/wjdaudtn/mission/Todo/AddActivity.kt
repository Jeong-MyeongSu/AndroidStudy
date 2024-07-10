package com.wjdaudtn.mission.todo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.marginEnd
import androidx.core.view.marginRight
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.todo.database.Todo
import com.wjdaudtn.mission.todo.database.TodoDao
import com.wjdaudtn.mission.databinding.ActivityAddBinding
import com.wjdaudtn.mission.todo.util.Const.Companion.DATE_PICKER_TAG
import com.wjdaudtn.mission.todo.util.Const.Companion.DEFAULT_VALUE
import com.wjdaudtn.mission.todo.util.Const.Companion.DEFAULT_VALUE_ALARM
import com.wjdaudtn.mission.todo.util.Const.Companion.FORMAT_PATTEN_DATE
import com.wjdaudtn.mission.todo.util.Const.Companion.FORMAT_PATTEN_TODAY
import com.wjdaudtn.mission.todo.util.Const.Companion.ONE
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ALARM_SWITCH
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_CONTENT
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ID
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_MILLISECOND
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_TITLE
import com.wjdaudtn.mission.todo.util.Const.Companion.SAVE_NULL_TEXT_TOAST
import com.wjdaudtn.mission.todo.util.Const.Companion.TIME_IS_OVER
import com.wjdaudtn.mission.todo.util.Const.Companion.TIME_PICKER_TAG
import com.wjdaudtn.mission.todo.util.Const.Companion.TIME_ZONE_ID
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var mId = -1
    private var mMillisecond = 0L
    private var alarmSwitch = 0
    private lateinit var calendar: Calendar

    private lateinit var dbInstance: TodoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbInstance = DataBaseInit().getTodoDao(this)

        mId = intent.getIntExtra(RESULT_KEY_ID, DEFAULT_VALUE)
        mMillisecond = intent.getLongExtra(RESULT_KEY_MILLISECOND, DEFAULT_VALUE.toLong())
        alarmSwitch = intent.getIntExtra(RESULT_KEY_ALARM_SWITCH, DEFAULT_VALUE_ALARM)

        val mTodo= if (mId != -1) dbInstance.getTodoById(mId) else null
        if (mTodo != null) {

            mMillisecond = mTodo.millisecond
            alarmSwitch = mTodo.alarmSwitch

        }
        calendar = Calendar.getInstance().apply{
            timeInMillis = mMillisecond
        }
        settingBinding(mTodo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuItem1: MenuItem? = menu?.add(0, 0, 0, "저장")
        menuItem1?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

//         커스텀 텍스트뷰 생성
//        val textView = TextView(this).apply {
//            text = "저장"
//            setPadding(16, 8, 56, 8)
//            setBackgroundResource(R.drawable.toolbar_menu_item_background)
//            setTextColor(getColor(android.R.color.black))
//        }
//        val textView = findViewById<TextView>(R.id.menu_item_text) //이거 왜 마진은 설정이 안될까??

        // 메뉴 항목에 커스텀 뷰 적용
//        menuItem1?.actionView = textView
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 업 버튼 클릭 시 뒤로 가기 동작 정의
                onBackPressedDispatcher.onBackPressed()
                true
            }
            0 -> {
                saveTodo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


/* 저장 버튼 눌렀을 때 */
    private fun saveTodo() {
        val currentCalendar = Calendar.getInstance()
        val eventCalendar = calendar.timeInMillis
        if (binding.editTextTitleAdd.text.toString() == "" || binding.editTextContentAdd.text.toString() == "") {
            Toast.makeText(baseContext, SAVE_NULL_TEXT_TOAST, Toast.LENGTH_SHORT).show()
        } else if (eventCalendar <= currentCalendar.timeInMillis) {
            Toast.makeText(baseContext, TIME_IS_OVER, Toast.LENGTH_SHORT).show()
        } else {
            val intent = intent
            intent.putExtra(RESULT_KEY_TITLE, binding.editTextTitleAdd.text.toString())
            intent.putExtra(RESULT_KEY_CONTENT, binding.editTextContentAdd.text.toString())
            intent.putExtra(RESULT_KEY_ID, mId)
            intent.putExtra(RESULT_KEY_MILLISECOND, eventCalendar)
            intent.putExtra(RESULT_KEY_ALARM_SWITCH, alarmSwitch)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


/* 데이트 픽커*/
    private fun showDatePicker(editText: TextInputEditText) {
        val todayCalendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE_ID))
        todayCalendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        val todayTimeInMillis = todayCalendar.timeInMillis

        // 내일 날짜 설정
        val tomorrowCalendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE_ID))
        tomorrowCalendar.add(Calendar.DATE, ONE)
        val tomorrowTimeInMillis = tomorrowCalendar.timeInMillis

        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(todayTimeInMillis) // 최소 날짜 설정: 오늘 날짜
            .setEnd(tomorrowTimeInMillis + (1000 * 60 * 60 * 24 * 365L)) // 최대 날짜 설정: 1년 후
            .setValidator(DateValidatorPointForward.now()) // 오늘 이후 날짜만 선택 가능 하도록 설정
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(todayTimeInMillis) // 기본 선택 날짜 설정: 오늘 날짜
            .setCalendarConstraints(constraintsBuilder)
            .build()

        datePicker.show(supportFragmentManager, DATE_PICKER_TAG)

        datePicker.addOnPositiveButtonClickListener { selection ->
            calendar.timeInMillis = selection
            val dateFormat = SimpleDateFormat(FORMAT_PATTEN_DATE, Locale.KOREA)
            val date = Date(selection)
            editText.setText(dateFormat.format(date))
        }
    }
/* 타임 픽커*/
    private fun showTimePicker(editText: TextInputEditText) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Select Time")
            .build()

        picker.show(supportFragmentManager, TIME_PICKER_TAG)

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat(FORMAT_PATTEN_TODAY, Locale.KOREA)
            val formattedTime = timeFormat.format(calendar.time)
            editText.setText(formattedTime)
        }
    }
/* 뒤로 가기 버튼*/
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

/* 버튼 TEXT 및 버튼 클릭 interface */

    @SuppressLint("SetTextI18n")
    private fun settingBinding(mTodo: Todo?){
        binding.editTextTitleAdd.setText(mTodo?.title)
        binding.editTextContentAdd.setText(mTodo?.content)
        binding.btnDate.setText("${calendar.get(Calendar.YEAR)} 년 ${calendar.get(Calendar.MONTH) + 1} 월 ${calendar.get(Calendar.DAY_OF_MONTH)} 일")
        binding.btnTime.setText("${if(calendar.get(Calendar.AM_PM) == Calendar.AM) "오전" else "오후"} ${calendar.get(Calendar.HOUR)}시 ${calendar.get(Calendar.MINUTE)}분")
        binding.btnSwitch.isChecked = alarmSwitch == 1 //1이면 true 아니면 false
        binding.btnDate.setOnClickListener(customClickListener)
        binding.btnTime.setOnClickListener(customClickListener)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            alarmSwitch = if (isChecked) 1 else 0
            Log.d("alarm", "$alarmSwitch")
        }

        //ToolBar
        setSupportActionBar(binding.todoMainToolbar)
        /**
         *         supportActionBar?.setDisplayHomeAsUpEnabled(true)
         *         supportActionBar?.setDisplayShowHomeEnabled(true)
         */
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Todo"
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로 가기 동작 정의
                finish()
            }
        })
    }

    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_date -> showDatePicker(binding.btnDate)
            R.id.btn_time -> showTimePicker(binding.btnTime)
        }
    })
}
