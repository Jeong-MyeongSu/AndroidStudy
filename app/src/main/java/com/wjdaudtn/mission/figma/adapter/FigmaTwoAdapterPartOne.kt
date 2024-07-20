package com.wjdaudtn.mission.figma.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.databinding.FigmaTwoListDayBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListLeftBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListRightBinding
import com.wjdaudtn.mission.databinding.FigmaTwoListRightWithImageBinding
import com.wjdaudtn.mission.databinding.FragmentFigmaTwoPartOneBinding
import com.wjdaudtn.mission.figma.FigmaTwoActivity
import com.wjdaudtn.mission.figma.fragment.FigmaTwoFragmentPartOne
import com.wjdaudtn.mission.figma.fragment.FigmaTwoFragmentPartOne.TalkItem

/**
 *packageName    : com.wjdaudtn.mission.figma.adapter
 * fileName       : FigmaTwoAdapterPartOne
 * author         : licen
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        licen       최초 생성
 */
class FigmaTwoAdapterPartOne(private var item: MutableList<TalkItem>, private var zipperUpCallback: ZipperUpCallback, private var activity:FigmaTwoFragmentPartOne) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface ZipperUpCallback {
        fun zipperUpRequest()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> FigmaViewHolderPartOne1(
                FigmaTwoListLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            2 -> FigmaViewHolderPartOne2(
                FigmaTwoListRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            3 -> FigmaViewHolderPartOne3(
                FigmaTwoListDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> FigmaViewHolderPartOne4(
                FigmaTwoListRightWithImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FigmaViewHolderPartOne1 -> {
                holder.binding.figmaTwoListLeftText.text = item[position].content
                holder.binding.root.setOnClickListener{
                    activity.hideKeyboard()
//                    zipperUpCallback.zipperUpRequest()
                    Log.d("recyclerViewClick","recyclerViewClick")
                }
            }

            is FigmaViewHolderPartOne2 -> {
                holder.binding.figmaTwoListRightText.text = item[position].content
                holder.binding.root.setOnClickListener{
                    activity.hideKeyboard()
//                    zipperUpCallback.zipperUpRequest()
                    Log.d("recyclerViewClick","recyclerViewClick")
                }
            }

            is FigmaViewHolderPartOne3 -> holder.binding.figmaTwoListDayText.text =
                item[position].content

            is FigmaViewHolderPartOne4 -> holder.binding.figmaTwoListWithImageText.text =
                item[position].content
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun getItemViewType(position: Int): Int {
        return item[position].user
    }

    class FigmaViewHolderPartOne1(val binding: FigmaTwoListLeftBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FigmaViewHolderPartOne2(val binding: FigmaTwoListRightBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FigmaViewHolderPartOne3(val binding: FigmaTwoListDayBinding) :
        RecyclerView.ViewHolder(binding.root) //다음 날로 넘어갈 때

    class FigmaViewHolderPartOne4(val binding: FigmaTwoListRightWithImageBinding) :
        RecyclerView.ViewHolder(binding.root)//사진을 같이 넘길 때

    @SuppressLint("NotifyDataSetChanged")
    fun updateList() {
        notifyDataSetChanged()
    }

}
