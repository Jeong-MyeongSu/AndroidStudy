package com.wjdaudtn.mission.Todo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityAddBinding
import java.util.Calendar

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var mId = -1
    private var year = 0
    private var month = 0
    private var dayOfmonth = 0
    private var alarmSwitch = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mId = intent.getIntExtra("id", -1)
        year = intent.getIntExtra("year", -1)
        month = intent.getIntExtra("month", -1)
        dayOfmonth = intent.getIntExtra("dayOfMonth", -1)
        alarmSwitch = intent.getIntExtra("alarmSwitch", 0)


        binding.editTextTitleAdd.setText(intent.getStringExtra("title"))
        binding.editTextContentAdd.setText(intent.getStringExtra("content"))
        binding.btnDate.text = "$year 년 $month 월 $dayOfmonth 일"
        binding.timePicker.hour = intent.getIntExtra("hour", -1)
        binding.timePicker.minute = intent.getIntExtra("minute", -1)
        if (alarmSwitch == 1) {
            binding.btnSwitch.isChecked = true
        } else {
            binding.btnSwitch.isChecked = false
        }
        binding.btnSave.setOnClickListener(customClickListener)
        binding.btnBackTodo.setOnClickListener(customClickListener)
        binding.btnDate.setOnClickListener(customClickListener)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmSwitch = 1
            } else {
                alarmSwitch = 0
            }

            Log.d("alarm", "$alarmSwitch")
        }

    }

    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_save -> daveTodo()
            R.id.btn_date -> showDate()
            R.id.btn_back_todo -> {
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    })


    fun daveTodo(){
        val currentCalendar = Calendar.getInstance()
        val eventCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, dayOfmonth)
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }
        if (binding.editTextTitleAdd.text.toString() == "" || binding.editTextContentAdd.text.toString() == "") {
            Toast.makeText(this, "제목과 내용을 입력하세요", Toast.LENGTH_SHORT).show()
        } else if (eventCalendar.timeInMillis <= currentCalendar.timeInMillis) {
            Toast.makeText(this, "시간이 지났습니다.", Toast.LENGTH_SHORT).show()
        } else {
            val intent = intent
            intent.putExtra("result_title", binding.editTextTitleAdd.text.toString())
            intent.putExtra("result_content", binding.editTextContentAdd.text.toString())
            intent.putExtra("id", mId)
            intent.putExtra("year", year)
            intent.putExtra("month", month)
            intent.putExtra("dayOfMonth", dayOfmonth)
            intent.putExtra("hour", binding.timePicker.hour)
            intent.putExtra("minute", binding.timePicker.minute)
            intent.putExtra("alarmSwitch", alarmSwitch)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    /**
     * 할일 추가 화면에서 날짜 버튼 클릭 했을때
     */
    fun showDate() {
        // 현재 날짜를 얻기 위한 Calendar 인스턴스 생성
        val calendar = Calendar.getInstance()
        // DatePickerDialog 생성
        val datePickerDialog = DatePickerDialog(
            this,
            object : DatePickerDialog.OnDateSetListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    binding.btnDate.text = "${p1}년 ${p2 + 1}월 ${p3}일"
                    year = p1
                    month = p2 + 1
                    dayOfmonth = p3
                }
            },
            intent.getIntExtra("year", -1),
            intent.getIntExtra("month", -1) - 1,
            intent.getIntExtra("dayOfMonth", -1)
        )
        // 최소 날짜를 현재 날짜로 설정
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        // DatePickerDialog 표시
        datePickerDialog.show()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


}