package com.wjdaudtn.mission.Todo

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ItemMainBinding

class TodoViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class TodoAdapter(var datas: MutableList<TodoMainActivity.Text>?, val requestLauncher: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TodoViewHolder(
            ItemMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TodoViewHolder).binding
        datas?.let {
            val item = it[position]
            binding.itemMainData.text = item.title
            binding.itemContentData.text = item.content
            binding.root.setOnLongClickListener {
                showPopupMenu(binding.root, position)
                true
            }
        }
    }
    private fun showPopupMenu(view:View, position: Int){
        val popupMenu = PopupMenu(view.context,view)
        popupMenu.inflate(R.menu.menu_list)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.item_change -> {
                    val intent = Intent(view.context, AddActivity::class.java)
                    intent.putExtra("title", datas?.get(position)?.title)
                    intent.putExtra("content", datas?.get(position)?.content)
                    intent.putExtra("position",position)
                    requestLauncher.launch(intent)
                    true
                }
                R.id.item_delete ->{
                    datas?.removeAt(position)
                    notifyItemRemoved(position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}