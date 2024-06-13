package com.wjdaudtn.mission.Todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.databinding.ItemMainBinding

class TodoViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class TodoAdapter(var datas:MutableList<TodoMainActivity.Text>?):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TodoViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return datas?.size ?:0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TodoViewHolder).binding
        datas?.let {
            val item = it[position]
            binding.itemMainData.text = item.title
            binding.itemContentData.text = item.content
        }

    }

}