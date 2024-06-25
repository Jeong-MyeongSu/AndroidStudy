package com.wjdaudtn.mission.Todo.Adapter

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.AlarmReceiver
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.Todo.AddActivity
import com.wjdaudtn.mission.Todo.Database.Todo
import com.wjdaudtn.mission.Todo.Database.TodoDao
import com.wjdaudtn.mission.databinding.ItemMainBinding
import java.util.Calendar

class TodoViewHoler(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class TodoAdapter(
    private var datas: MutableList<Todo>?,
    private val requestLauncher: ActivityResultLauncher<Intent>,
    private var todoDao: TodoDao
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TodoViewHoler(
            ItemMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TodoViewHoler).binding
        datas?.let {
            val item = it[position]
            binding.itemMainData.text = item.title
            binding.itemContentData.text = item.content
            //시간
            if (item.hour <= 11 && item.hour > 0) {
                binding.ampm.text = "오전"
                if (item.minute <= 9) {
                    binding.time.text = "${item.hour}:0${item.minute}"
                } else {
                    binding.time.text = "${item.hour}:${item.minute}"
                }
            } else if (item.hour == 0) {
                binding.ampm.text = "오전"
                if (item.minute <= 9) {
                    binding.time.text = "${item.hour + 12}:0${item.minute}"
                } else {
                    binding.time.text = "${item.hour + 12}:${item.minute}"
                }

            } else if (item.hour == 12) {
                binding.ampm.text = "오후"
                if (item.minute <= 9) {
                    binding.time.text = "${item.hour}:0${item.minute}"
                } else {
                    binding.time.text = "${item.hour}:${item.minute}"
                }
            } else {
                binding.ampm.text = "오후"
                if (item.minute <= 9) {
                    binding.time.text = "${item.hour - 12}:0${item.minute}"
                } else {
                    binding.time.text = "${item.hour - 12}:${item.minute}"
                }
            }
            binding.day.text = "${item.month}월 ${item.dayOfMonth}일(${dayOfWeekString(item.year,item.month,item.dayOfMonth)})"

            binding.root.setOnLongClickListener {
                Log.d("delete1", "$position, ${datas!!.size}, ${todoDao.getTodoAll().size}")
                showPopupMenu(binding.root, position)
                true
            }

            if (item.alramSwitch == 1) {
                val colorBlack = ContextCompat.getColor(holder.itemView.context, R.color.black)
                binding.ampm.setTextColor(colorBlack)
                binding.time.setTextColor(colorBlack)
                binding.day.setTextColor(colorBlack)
                binding.imageAlarm.setTransitionVisibility(View.VISIBLE)
            } else {
                val colorHighGray =
                    ContextCompat.getColor(holder.itemView.context, R.color.high_gray)
                binding.ampm.setTextColor(colorHighGray)
                binding.time.setTextColor(colorHighGray)
                binding.day.setTextColor(colorHighGray)
            }

        }
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val todoItem = datas?.get(position)!!

        popupMenu.inflate(R.menu.menu_list)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_change -> {
                    val intent = Intent(view.context, AddActivity::class.java)
                    intent.putExtra("id", todoItem.id)
                    intent.putExtra("title", todoItem.title)
                    intent.putExtra("content", todoItem.content)
                    intent.putExtra("year", todoItem.year)
                    intent.putExtra("month", todoItem.month)
                    intent.putExtra("dayOfMonth", todoItem.dayOfMonth)
                    intent.putExtra("hour", todoItem.hour)
                    intent.putExtra("minute", todoItem.minute)
                    intent.putExtra("alarmSwitch", todoItem.alramSwitch)
                    requestLauncher.launch(intent)
                    true
                }

                R.id.item_delete -> {
                    todoDao.setDeleteTodo(todoItem)
                    Log.d("delete2", "$position, ${datas!!.size}, ${todoDao.getTodoAll().size}")
                    datas?.remove(todoItem)
                    notifyDataSetChanged()

                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    fun addItem(item: Todo) {
        datas?.add(item)
        notifyItemInserted((itemCount) - 1)
    }

    fun updateItem(item: Todo) {
        var sameIndex = 0
        for ((index, value) in datas!!.withIndex()) {
            if (value.id == item.id) {
                value.title = item.title
                value.content = item.content
                value.year = item.year
                value.month = item.month
                value.dayOfMonth = item.dayOfMonth
                value.hour = item.hour
                value.minute = item.minute
                value.alramSwitch = item.alramSwitch
                sameIndex = index
            }
        }
        notifyItemChanged(sameIndex)
    }
    private fun dayOfWeekString(p1: Int, p2: Int, p3: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(p1, p2, p3)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
    }

}