package com.wjdaudtn.mission.todo.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ItemMainBinding
import com.wjdaudtn.mission.todo.AddActivity
import com.wjdaudtn.mission.todo.DataBaseInit
import com.wjdaudtn.mission.todo.TodoMainActivity
import com.wjdaudtn.mission.todo.database.Todo
import com.wjdaudtn.mission.todo.database.TodoDao
import com.wjdaudtn.mission.todo.util.Const.Companion.ASIA
import com.wjdaudtn.mission.todo.util.Const.Companion.FORMAT_PATTEN_DATE
import com.wjdaudtn.mission.todo.util.Const.Companion.FORMAT_PATTEN_TODAY
import com.wjdaudtn.mission.todo.util.Const.Companion.HIGH_GRAY
import com.wjdaudtn.mission.todo.util.Const.Companion.ORANGE
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ID
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/* recyclerView view holder*/
class TodoViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class TodoAdapter(
    private var todoList: MutableList<Todo>,
    private val requestLauncher: ActivityResultLauncher<Intent>,
    private val activity: TodoMainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dbInstance: TodoDao = DataBaseInit().getTodoDao(activity.baseContext)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {  //뷰 타입에 맞춰 뷰홀더 만듬
        return TodoViewHolder(
            ItemMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TodoViewHolder).binding
        todoList.let {
            val item = it[position]
            binding.itemMainData.text = item.title
            binding.itemContentData.text = item.content

            val currentCalendar = Calendar.getInstance()
            val itemCalendar = Calendar.getInstance().apply {
                timeInMillis = item.millisecond
            }
            val formattedDate = formatDateTime(itemCalendar.timeInMillis)
            binding.itemDayTime.text = formattedDate

            /* compound drawable 에서 그림 부분 크기 수정 */
            val drawable = ContextCompat.getDrawable(activity, R.drawable.bell_alarm)
            drawable?.setBounds(0, 0, 52, 52) // 원하는 크기로 설정
            binding.itemDayTime.setCompoundDrawables(drawable, null, null, null)

            if (item.alarmSwitch == 1) {
                val timeDifference = itemCalendar.timeInMillis - currentCalendar.timeInMillis
                val minutesLeft = timeDifference / 60_000

                val textColor = when {
                    minutesLeft < 0 -> Color.parseColor(HIGH_GRAY)//방법1
                    minutesLeft <= 30 -> Color.RED
                    minutesLeft <= 60 -> Color.parseColor(ORANGE)
                    else -> Color.BLACK
                }
                binding.itemDayTime.setTextColor(textColor)
                setDrawableVisibility(binding.itemDayTime, true) //compound drawable 로 인한 visible 설정 함수

            } else {
                val colorHighGray =
                    ContextCompat.getColor(holder.itemView.context, R.color.high_gray)//방법2
                binding.itemDayTime.setTextColor(colorHighGray)
                setDrawableVisibility(binding.itemDayTime, false)

            }

            binding.root.setOnLongClickListener {
//                Log.d("delete1", "$position, ${todoList.size}, ${dbInstance.getTodoAll().size}")
                showPopupMenu(binding.root, position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val todoItem = todoList[position]

        popupMenu.inflate(R.menu.menu_list)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_change -> {
                    val intent = Intent(view.context, AddActivity::class.java)
                    intent.putExtra(RESULT_KEY_ID, todoItem.id)
                    requestLauncher.launch(intent)
                    true
                }

                R.id.item_delete -> {
                    deleteItem(todoItem)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    fun addItem(item: Todo) {
        todoList.add(item)
        notifyItemInserted((itemCount) - 1)
    }

    fun updateItem(item: Todo) {
        var sameIndex = 0
        for ((index, value) in todoList.withIndex()) {
            if (value.id == item.id) {
                value.title = item.title
                value.content = item.content
                value.millisecond = item.millisecond
                value.alarmSwitch = item.alarmSwitch
                sameIndex = index
            }
        }
        notifyItemChanged(sameIndex)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun deleteItem(todoItem: Todo) {
        activity.cancelAlarm(todoItem)
        dbInstance.setDeleteTodo(todoItem)
        todoList.remove(todoItem)
        val position = todoList.indexOf(todoItem)
        if (position != -1) { //이론상 포지션 으로 지울 때는 Removed 를 사용 해야 하지만 database 의 PrimaryKey 로 지우기 때문에 else 문만 사용 된다.
            todoList.removeAt(position)
            notifyItemRemoved(position)
        } else {
            // 항목이 목록에 없는 경우 예외 처리를 할 수 있다.
            notifyDataSetChanged()  // fallback
        }
    }



    private fun formatDateTime(milliseconds: Long): String {
        val currentMillis = Calendar.getInstance().timeInMillis
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(ASIA), Locale.KOREA)
        calendar.timeInMillis = milliseconds
        val itemMillis = calendar.timeInMillis

        val isSameDay: Boolean
        if (Build.VERSION.SDK_INT >= 34) {
            // API 34 이상 에서는 LocalDate 를 사용 하여 날짜 비교
            isSameDay = LocalDate.ofInstant(Instant.ofEpochMilli(currentMillis), ZoneId.systemDefault())
                .isEqual(
                    LocalDate.ofInstant(
                        Instant.ofEpochMilli(itemMillis),
                        ZoneId.systemDefault()
                    )
                )
        } else {
            // API 34미만 에서는 Calendar 클래스 를 사용 하여 날짜 비교
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = currentMillis

            val itemCalendar = Calendar.getInstance()
            itemCalendar.timeInMillis = itemMillis

            isSameDay = currentCalendar.get(Calendar.YEAR) == itemCalendar.get(Calendar.YEAR) &&
                    currentCalendar.get(Calendar.DAY_OF_YEAR) == itemCalendar.get(Calendar.DAY_OF_YEAR)
        }

        return if (isSameDay) { //오늘 이면 Today 아니면 날짜 마지막 문장이 return
            val sdf = SimpleDateFormat(FORMAT_PATTEN_TODAY, Locale.KOREA)
            sdf.timeZone = calendar.timeZone
            "${sdf.format(calendar.time)} Today"
        } else {
            val sdf = SimpleDateFormat(FORMAT_PATTEN_DATE, Locale.KOREA)
            sdf.timeZone = calendar.timeZone
            sdf.format(calendar.time)
        }
    }

    //compound drawable 그림만 visible 설정
    private fun setDrawableVisibility(textView: TextView, visible: Boolean) {
        val drawable = ContextCompat.getDrawable(textView.context, R.drawable.bell_alarm)?.apply {
            setBounds(0, 0, 52, 52) // 원하는 크기로 설정
        }
        if (visible) {
            // Make drawable visible
            textView.setCompoundDrawables(drawable, null, null, null)
        } else {
            // Make drawable invisible by setting it to null
            textView.setCompoundDrawables(null, null, null, null)
        }
    }
}