package com.wjdaudtn.mission.figma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.databinding.FigmaTwoListDayBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListLeftBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListRightBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListRightWithImageBinding

import com.wjdaudtn.mission.figma.fragment.FigmaTwoFragmentPartTwo

/**
 *packageName    : com.wjdaudtn.mission.figma.adapter
 * fileName       : FigmaTwoAdapterPartTwo
 * author         : licen
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        licen       최초 생성
 */
class FigmaTwoAdapterPartTwo(private var item: MutableList<FigmaTwoFragmentPartTwo.TalkItem2>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> FigmaViewHolderPartTwo1(
                FigmaTwoListLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            2 -> FigmaViewHolderPartTwo2(
                FigmaTwoListRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            3 -> FigmaViewHolderPartTwo3(
                FigmaTwoListDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> FigmaViewHolderPartTwo4(
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
            is FigmaViewHolderPartTwo1 -> holder.binding.figmaTwoListLeftText.text = item[position].content
            is FigmaViewHolderPartTwo2 -> holder.binding.figmaTwoListRightText.text = item[position].content
            is FigmaViewHolderPartTwo3 -> holder.binding.figmaTwoListDayText.text = item[position].content
            is FigmaViewHolderPartTwo4 -> holder.binding.figmaTwoListWithImageText.text = item[position].content
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun getItemViewType(position: Int): Int {
        return item[position].user
    }
    class FigmaViewHolderPartTwo1(val binding: FigmaTwoListLeftBinding): RecyclerView.ViewHolder(binding.root)
    class FigmaViewHolderPartTwo2(val binding: FigmaTwoListRightBinding): RecyclerView.ViewHolder(binding.root)
    class FigmaViewHolderPartTwo3(val binding: FigmaTwoListDayBinding): RecyclerView.ViewHolder(binding.root) //다음 날로 넘어갈 때
    class FigmaViewHolderPartTwo4(val binding: FigmaTwoListRightWithImageBinding): RecyclerView.ViewHolder(binding.root)//사진을 같이 넘길 때
}