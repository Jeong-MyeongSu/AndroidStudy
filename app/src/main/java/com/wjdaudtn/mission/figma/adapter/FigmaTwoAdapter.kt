package com.wjdaudtn.mission.figma.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.databinding.FigmaTwoListDayBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListLeftBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListRightBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListRightWithImageBinding
import com.wjdaudtn.mission.figma.FigmaTwoActivity
import com.wjdaudtn.mission.recyclerView.ViewHolderTwo.MyViewHolder1

/**
 *packageName    : com.wjdaudtn.mission.figma.adapter
 * fileName       : FigmaTwoAdapter
 * author         : licen
 * date           : 2024-07-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-11        licen       최초 생성
 */
class FigmaTwoAdapter(private var contextList:MutableList<FigmaTwoActivity.StringAndType>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> FigmaViewHolder1(
                FigmaTwoListLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            2 -> FigmaViewHolder2(
                FigmaTwoListRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            3 -> FigmaViewHolder3(
                FigmaTwoListDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> FigmaViewHolder4(
                FigmaTwoListRightWithImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is FigmaViewHolder1 -> holder.binding.figmaTwoListLeftText.text = contextList[position].string
            is FigmaViewHolder2 -> holder.binding.figmaTwoListRightText.text = contextList[position].string
            is FigmaViewHolder3 -> holder.binding.figmaTwoListDayText.text = contextList[position].string
            is FigmaViewHolder4 -> holder.binding.figmaTwoListWithImageText.text = contextList[position].string
        }
    }

    override fun getItemCount(): Int {
        return contextList.size
    }

    override fun getItemViewType(position: Int): Int {
        return contextList[position].type
    }
}
class FigmaViewHolder1(val binding:FigmaTwoListLeftBinding): RecyclerView.ViewHolder(binding.root)
class FigmaViewHolder2(val binding:FigmaTwoListRightBinding): RecyclerView.ViewHolder(binding.root)
class FigmaViewHolder3(val binding:FigmaTwoListDayBinding): RecyclerView.ViewHolder(binding.root)
class FigmaViewHolder4(val binding:FigmaTwoListRightWithImageBinding): RecyclerView.ViewHolder(binding.root)
