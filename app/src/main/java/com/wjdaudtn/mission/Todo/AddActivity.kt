package com.wjdaudtn.mission.Todo

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var mId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mId = intent.getIntExtra("id", -1)

        binding.editTextTitleAdd.setText(intent.getStringExtra("title"))
        binding.editTextContentAdd.setText(intent.getStringExtra("content"))

        binding.btnSave.setOnClickListener(customClickListener)
        binding.btnBackTodo.setOnClickListener(customClickListener)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }
    private val customClickListener: View.OnClickListener = (View.OnClickListener { v ->
        when(v.id){
            R.id.btn_save -> {
                val intent = intent
                intent.putExtra("result_title", binding.editTextTitleAdd.text.toString())
                intent.putExtra("result_content", binding.editTextContentAdd.text.toString())
                intent.putExtra("id",mId)

                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            R.id.btn_back_todo -> {
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    })

    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}