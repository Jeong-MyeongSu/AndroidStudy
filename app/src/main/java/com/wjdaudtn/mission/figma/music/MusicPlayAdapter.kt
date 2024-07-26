package com.wjdaudtn.mission.figma.music

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ItemFigmaMusicBinding
import com.wjdaudtn.mission.figma.music.MusicActivity.PlayInfo

class MusicPlayAdapter(
    private var playList: MutableList<PlayInfo>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var tag: String = "MusicPlayAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MusicPlayHolder(
            ItemFigmaMusicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MusicPlayHolder).bind()
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    fun getItem(position: Int): PlayInfo {
        return playList[position]
    }

    fun initPlayIcon(position: Int) {
        for ((index, item) in playList.withIndex()) {
            if (position != index ) {
                notifyItemChanged(index)
            }
        }
    }

    inner class MusicPlayHolder(var binding: ItemFigmaMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            playList.let {
                val item = it[adapterPosition]
                binding.figmaOneSectionItemTitle.text = it[adapterPosition].title
                binding.figmaOneSectionItemArtist.text = it[adapterPosition].artist

                val resourceIcon =
                    if (item.mediaPlayer.isPlaying) R.drawable.pause else R.drawable.play

                binding.btnPlay.setImageResource(resourceIcon)
                binding.btnPlay.setOnClickListener {
                    if (item.mediaPlayer.isPlaying) {
                        item.event.pause(item.mediaPlayer)
                    } else {
                        item.event.play(item.mediaPlayer, adapterPosition)
                        initPlayIcon(adapterPosition)
                    }
                    notifyItemChanged(adapterPosition)
                }

                item.mediaPlayer.setOnCompletionListener {
                    Log.d(tag, "노래 끝남")
                    item.event.stop(item.mediaPlayer)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }
}