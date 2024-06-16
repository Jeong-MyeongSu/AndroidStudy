package com.wjdaudtn.mission.Todo.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.Todo.AddActivity
import com.wjdaudtn.mission.Todo.Database.Todo
import com.wjdaudtn.mission.Todo.Database.TodoDao
import com.wjdaudtn.mission.databinding.ItemMainBinding

class TodoViewHoler(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class TodoAdapter(private var datas: MutableList<Todo>?, private val requestLauncher: ActivityResultLauncher<Intent>, private var todoDao: TodoDao) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TodoViewHoler(
            ItemMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TodoViewHoler).binding
        datas?.let{
            val item = it[position]
            binding.itemMainData.text = item.title
            binding.itemContentData.text = item.content
            binding.root.setOnLongClickListener{
                showPopupMenu(binding.root,position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return datas?.size ?:0
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showPopupMenu(view:View, position:Int){
        val popupMenu = PopupMenu(view.context,view)
        val todoItem = datas?.get(position)!!

        popupMenu.inflate(R.menu.menu_list)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.item_change ->{
                    val intent = Intent(view.context, AddActivity::class.java)
                    intent.putExtra("id",todoItem.id)
                    intent.putExtra("title",todoItem.title)
                    intent.putExtra("content",todoItem.content)
                    requestLauncher.launch(intent)
                    true
                }
                R.id.item_delete ->{
                    todoDao.setDeleteTodo(todoItem)
                    datas?.remove(todoItem)
                    notifyDataSetChanged()
                    true
                }
                else ->false
            }
        }
        popupMenu.show()
    }

    fun addItem(item: Todo) {
        datas?.add(item)
        notifyItemInserted((itemCount) -1)
    }
    fun updateItem(item: Todo){
        var sameIndex = 0
        for (index in datas!!.indices) {
            val todoItem = datas!![index]
            if (todoItem.id == item.id) {
                todoItem.title = item.title
                todoItem.content = item.content
                sameIndex = index
            }
        }
        notifyItemChanged(sameIndex)
    }
}